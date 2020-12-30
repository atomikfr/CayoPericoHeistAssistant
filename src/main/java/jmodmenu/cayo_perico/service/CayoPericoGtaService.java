package jmodmenu.cayo_perico.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import jmodmenu.GtaProcess;
import jmodmenu.Utils;
import jmodmenu.cayo_perico.model.BoltCutters;
import jmodmenu.cayo_perico.model.GrapplingEquipment;
import jmodmenu.cayo_perico.model.GuardTruck;
import jmodmenu.cayo_perico.model.GuardUniform;
import jmodmenu.cayo_perico.model.LootType;
import jmodmenu.cayo_perico.model.MainLoot;
import jmodmenu.cayo_perico.model.MapItem;
import jmodmenu.cayo_perico.model.Paintings;
import jmodmenu.cayo_perico.model.SecondaryCompundLoot;
import jmodmenu.cayo_perico.model.SecondaryIslandLoot;
import jmodmenu.core.Global;
import jmodmenu.core.PlayerInfo;
import jmodmenu.core.Script;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CayoPericoGtaService implements CayoPericoMapService {
	
	GtaProcess gta;
	Queue<StatEntry> queue;
	AtomicLong lastPush = new AtomicLong(  );
	volatile boolean stoppingQueue = false;
	volatile int inProgress = 0;
	
	int slotPlayer; // 0 or 1

	
	public CayoPericoGtaService(GtaProcess gta) {
		this.gta = gta;
		queue = new ConcurrentLinkedQueue<>();
		new Thread( this::loopStatQueue, "batchStatWriter" ).start();
	}
	
	
	/**
	 * Try to replace MP0_ with current player slot
	 */
	public void setStat(String name, Integer value) {
		if ( name.startsWith("MP0") && slotPlayer > 0 ) {
			name = "MP"+slotPlayer+name.substring(3);
		}
		int joaat = Utils.joaat(name);
		log.debug("Setting Stat[{}] Joaat[{}] to Value[{}]", name, joaat, value);
		setStat(joaat, value);
	}
	
	public void setStat(Integer joaat, Integer value) {
		if ( queue == null ) {
			log.warn("Queue has been deleted. Unable to set stat {} = {}", joaat, value);
			return;
		}
		StatEntry entry = new StatEntry();
		entry.joaat = joaat;
		entry.value = value;
		queue.add( entry );
		lastPush.set( System.currentTimeMillis() );
	}
	
	// used as a runnable.
	private void loopStatQueue() {
		log.info("Stat Queue loop started.");
		while (!stoppingQueue) {
			long now = System.currentTimeMillis();
			long last = lastPush.get();
			long lagTime = last > 0 ? now - last : 0;
			int queueSize = queue.size();
			
			if ( queueSize < 20 ) {
				if ( lagTime < 250 || queueSize == 0 ) {
					try {
						Thread.sleep(250);
						if ( last == 0 ) {
							lastPush.set( System.currentTimeMillis() );
						}
						continue;
					} catch (InterruptedException e) {
						Queue<?> temp = this.queue;
						this.queue = null;
						temp.clear();
						log.warn("loopStatQueue stopped.");
						return;
						// e.printStackTrace();
					}
				}
			}
			if ( stoppingQueue ) return;
			
			int nb = 0;
			Map<Integer, Integer> stats = new LinkedHashMap<>();
			StatEntry entry;
			while ( !stoppingQueue && (entry = queue.poll()) != null ) {
				inProgress++; 
				stats.put(entry.joaat, entry.value);
				if ( ++nb >= 20 ) break;
			}
			log.debug("Batching {} stat values", stats.size());
			gta.getStatManager().setStats( stats );
			inProgress = 0;
			lastPush.set( System.currentTimeMillis() );
		}
	}
	
	public void flushStats() {
		stoppingQueue = true;
		Queue<StatEntry> temp = queue;
		Map<Integer, Integer> stats = new LinkedHashMap<>();
		temp.stream()
			.forEach( entry -> stats.put(entry.joaat, entry.value) );
		gta.getStatManager().setStats( stats );
	}
	
	
	public void waitEmptyQueue() throws InterruptedException {
		log.info("Waiting for empty stat queue.");
		while( queue.size() > 0 || inProgress > 0 ) {
			log.debug( "... Queue size[{}] inProgress[{}]", queue.size(), inProgress );
			Thread.sleep(1000);
		}
	}
	
	
	/**
	 * entry point to Cayo Perico shared global.
	 * @param playerIndex in session (0-31)
	 */
	private Global glPerico(int playerIndex) {
		return gta.globals().at(1706028).at(playerIndex, 53);
	}
	
	/**
	 * Heist global cuts
	 * @param playerIndex in heist (0-4)
	 */
	private Global glCuts(int playerIndex) {
		return gta.globals().at(1704127).at(823).at(56).at(playerIndex, 1);
	}
	
	/**
	 * Return Global storing this loot value
	 * @param loot type
	 */
	private Global glMainLoot(MainLoot loot) {
		Global glLootValue = gta.globals().at(262145);
		int offset = 29233 + loot.ordinal();
		glLootValue.at(offset);
		return glLootValue;
	}
	
	private Global glBagSize() {
		return gta.globals().at(262145).at(28999);
	}
	
	
	public List<PlayerInfo> getPlayersInfo() {
		// refresh some cache data.
		slotPlayer = gta.slotNumPlayer();
		return gta.getNetPlayerManager().getPlayers();
	}
	
	@Override
	public int getLocalPlayerIndex() {
		return gta.localPlayerIndex();
	}
	
	private int getEquipmentPosition(int playerIndex, int index) {
		return (int) glPerico(playerIndex).at(5).at(index).get();
	}
	
	private int getLootPosition(int playerIndex, int index) {
		return (int) glPerico(playerIndex).at(5).at(10).at(index).get();
	}
	
	public List<MapItem> getEquipment(int playerIndex) {
		ArrayList<MapItem> items = new ArrayList<>();
		
		items.addAll(
			BoltCutters.fromGlobal(getEquipmentPosition(playerIndex, 5))
		);
		
		items.addAll(
			GuardUniform.fromGlobal(getEquipmentPosition(playerIndex, 4))
		);
		
		items.addAll(
			GrapplingEquipment.fromGlobal(getEquipmentPosition(playerIndex, 3))
		);
		
		items.add( GuardTruck.fromGlobal(getEquipmentPosition(playerIndex, 34)) );
		
		return items;
	}
	
	public List<SecondaryIslandLoot> getIslandLoot(int playerIndex) {
		List<SecondaryIslandLoot> list = new ArrayList<>();
		BiConsumer<LootType, Integer> addLootToList = (type, idx) -> {
			list.addAll( SecondaryIslandLoot.fromGlobal(type, getLootPosition(playerIndex, idx)) );
		};
		Map.of(
			LootType.CASH, 1,
			LootType.WEED, 2,
			LootType.COCAINE, 3,
			LootType.GOLD, 4
		)
		.forEach( addLootToList );
		return list;
	}
	

	@Override
	public List<SecondaryCompundLoot> getCompoundLoot(int playerIndex) {
		List<SecondaryCompundLoot> list = new ArrayList<>();
		BiConsumer<LootType, Integer> addLootToList = (type, idx) -> {
			list.addAll( SecondaryCompundLoot.fromGlobal(type, getLootPosition(playerIndex, idx)) );
		};
		Map.of(
			LootType.CASH, 9,
			LootType.WEED, 10,
			LootType.COCAINE, 11,
			LootType.GOLD, 12
		)
		.forEach( addLootToList );

		list.addAll( Paintings.fromGlobal(getLootPosition(playerIndex, 17)) );
		return list;
	}
	
	@Override
	public boolean hasScopedLoot(int playerIndex, LootType type) {
		if (type == LootType.PAINTINGS)
			return getLootPosition(playerIndex, 17) == getLootPosition(playerIndex, 18);
		
		int from = type.ordinal();
		return 
			getLootPosition(playerIndex, from+1) == getLootPosition(playerIndex, from+5)
			&& getLootPosition(playerIndex, from+9) == getLootPosition(playerIndex, from+13);
	}
	
	@Override
	public Integer getScopeData(int playerIndex, LootType type, boolean island) {
		int lootIndex = type.ordinal() + (island ? 1 : 9);
		if ( type == LootType.PAINTINGS ) {
			lootIndex = 17;
			if ( island ) return 0;
		}
		return getLootPosition(playerIndex, lootIndex);
	}
	
	
	EnumMap<LootType, String> statLootNames = new EnumMap<>(LootType.class);
	{
		statLootNames.put(LootType.CASH, "CASH");
		statLootNames.put(LootType.WEED, "WEED");
		statLootNames.put(LootType.COCAINE, "COKE");
		statLootNames.put(LootType.GOLD, "GOLD");
		statLootNames.put(LootType.PAINTINGS, "PAINT");
	}
	private String baseName(LootType type, boolean island) {
		return "MP" + slotPlayer
		+ "_H4LOOT_"
		+ statLootNames.getOrDefault(type, "CASH")
		+ ( type == LootType.PAINTINGS ? "" : island ? "_I" : "_C" );
	}
	private String scopeName(LootType type, boolean island) {
		return baseName(type, island) + "_SCOPED";
	}
	
	@Override
	public void setLootScope(LootType type, boolean island, int value) {
		/*
		int[] joaats = 
			island ? new int[] {-1690987375, 834429412, 611902845, 1712421353}
				: new int[] { -670391016, -404469783, -2365219, -1409842437};
		int joaat = type == LootType.PAINTINGS ? 1775008747 : joaats[ type.ordinal() ];
		setStat(joaat, value);
		*/
		if (value > 0) scopeOut();
		String statName = scopeName(type, island);
		setStat(statName, value);
	}
	
	@Override
	public void setLootPosition(LootType type, boolean island, int value) {
		/*
		int[] joaats = 
			island ? new int[] { -1646922924, -781945763, -1003353535, -1153023047 }
				: new int[] { -399833087, 1093424111, 972322244, 803319022 };
		
		int joaat = type == LootType.PAINTINGS ? 1997903113 : joaats[ type.ordinal() ];
		
		setStat(joaat, value);
		*/
		// batchStatWriter.accept(joaat, value);
		String statName = baseName(type, island);
		setStat(statName, value);
	}
	
	@Override
	public void scopeLoot(LootType type) {
		scopeOut();
		if ( type == LootType.PAINTINGS ) {
			// copyLootToScope(17, 1775008747);
			copyLootToScope(17, scopeName(type, false));
			return;
		}
		int from = type.ordinal();
		copyLootToScope(from+1, scopeName(type, true));
		copyLootToScope(from+9, scopeName(type, false));
	}
	
	private void copyLootToScope(int fromLootGlobal, String statName) {
		int playerIndex = gta.localPlayerIndex();
		int statScopeLootValue =  getLootPosition(playerIndex, fromLootGlobal);
		setStat(statName, statScopeLootValue);
	}
	
	@Override
	public MainLoot getMainLoot(int playerIndex) {
		int loot = (int) glPerico(playerIndex).at(5).at(9).get();
		return MainLoot.values()[ loot ];
	};
	
	@Override
	public void setMainLoot(MainLoot mainLoot) {
		setStat("MP0_H4CNF_TARGET", mainLoot.ordinal() );
	}
	
	@Override
	public int getScopedEquipment(int playerIndex) {
		// System.out.println("Get Scoped " + playerIndex);
		int val = (int) glPerico(playerIndex).at(5).get();
		// System.out.println("  -> " + val);
		return val;
	}
	
	private void scopeOut() {
		if ( (getApproach(gta.localPlayerIndex()) & 1) == 0 ) {
			// set scope_out
			addApproach(1, 1);
		}
	}
	
	@Override
	public void addScopedEquipment(int equipmentMask) {
		int playerIndex = gta.localPlayerIndex();
		Global global = glPerico(playerIndex).at(5);
		long value = global.get() | equipmentMask;
		if ( equipmentMask > 0 ) scopeOut();
		setStat("MP0_H4CNF_BS_GEN", (int)value);
		global.set(value);
	}
	
	@Override
	public int getApproach(int playerIndex) {
		// func_18 planning
		// return unk_0xCE990E643CD9D0E5(Global_1706028[iParam0 /53/].f_2, iParam1);
		int val = (int) glPerico(playerIndex).at(2).get();
		// System.out.println("  -> " + val);
		return val;
	}
	
	@Override
	public void addApproach(int approachMask, int setMask) {
		int playerIndex = gta.localPlayerIndex();
		Global global = glPerico(playerIndex).at(2);
		long value = global.get();
		value = (value & ~setMask) | (approachMask & setMask);
		
		setStat("MP0_H4_MISSIONS", (int) value);
	}
	
	@Override
	public int getWeapon(int playerIndex) {
		// Global_1706028[iParam0 /53/].f_5.f_35
		// func_359 planning
		return (int) glPerico(playerIndex).at(5).at(35).get();
	}
	
	@Override
	public void setWeapon(int weaponIndex) {
		setStat("MP0_H4CNF_WEAPONS", weaponIndex);
	}
	
	@Override
	public Map<String, Integer> getCuts() {
		Map<String, Integer> cuts = new LinkedHashMap<>();
		for (int j = 0; j < 4; j++) {
			long cut = glCuts(j).get();
			if ( cut == 0 ) break;
			cuts.put("Player " + (j+1), (int)cut);
		}
		return cuts;
	}
	
	@Override
	public void setCuts(Integer[] values) {
		for (int j = 0; j < values.length; j++) {			
			glCuts(j).set( (long) values[j] );
			// 823-67(j, 1) -> ready state ?
		}
	}
	
	@Override
	public int getCurrentLootValue(int index, MainLoot loot) {
		return (int) glMainLoot(loot).get();
	}
	
	@Override
	// Global_262145.f_28999
	public int getMyBagSize() {
		return (int) glBagSize().get();
	}

	@Override
	public void setBagSize(int newBagSize) {
		glBagSize().set((long)newBagSize);
	}
	
	@Override
	public void setLootValue(MainLoot loot, int newLootValue) {
		glMainLoot(loot).set((long)newLootValue);
	}
	
	@Override
	public int getStackLootValue(int playerIndex, LootType type) {
		// func_858 et suivante.
		// Global_1706028[iParam0 /*53*/].f_5.f_10.f_23;
		int offset = 19 + type.ordinal();
		return (int) glPerico(playerIndex).at(5).at(10).at(offset).get();
	}
	
	@Override
	public void setStackLootValue(LootType lootType, int value) {
		String statName = Arrays.asList(
			"MP0_H4LOOT_CASH_V",
			"MP0_H4LOOT_WEED_V",
			"MP0_H4LOOT_COKE_V",
			"MP0_H4LOOT_GOLD_V",
			"MP0_H4LOOT_PAINT_V"
		).get(lootType.ordinal());
		setStat(statName, value);
	}
	
	@Override
	public boolean isHardMode(int playerIndex) {
		// MPx_H4_PROGRESS 
		long register = glPerico(playerIndex).at(1).get();
		int mask = 1 << 12;
		boolean hard = (register & mask) > 0;
		log.debug("Hard mode register[{}] mask[{}] boolean[{}]", register, mask, hard);
		return hard; 
	}
	
	@Override
	public void setHardMode(Boolean hardActivated) {
		// MPx_H4_PROGRESS 
		int playerIndex = gta.localPlayerIndex();
		long register = glPerico(playerIndex).at(1).get();
		int mask = 1 << 12;
		register = hardActivated ? (register | mask) : (register & ~mask);
		log.debug("Set Hard mode register[{}] boolean[{}]", register, mask, hardActivated);
		setStat("MPx_H4_PROGRESS", (int) register);
	}
	
	@Override
	public void restartSubmarineComputer() {
		Script script = gta.getScript("heist_island_planning");
		if ( script == null ) return;
		script.locals().at(1525).setInt(2);
	}

}


class StatEntry {
	Integer joaat;
	Integer value;
}