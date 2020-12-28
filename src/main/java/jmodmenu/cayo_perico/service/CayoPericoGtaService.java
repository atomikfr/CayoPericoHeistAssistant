package jmodmenu.cayo_perico.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import jmodmenu.GtaProcess;
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

public class CayoPericoGtaService implements CayoPericoMapService {
	
	GtaProcess gta;
	
	public CayoPericoGtaService(GtaProcess gta) {
		this.gta = gta;
	}
	
	public List<PlayerInfo> getPlayersInfo() {
		return gta.getNetPlayerManager().getPlayers();
	}
	
	@Override
	public int getLocalPlayerIndex() {
		return gta.localPlayerIndex();
	}
	
	private int getEquipmentPosition(int playerIndex, int index) {
		return (int) gta.globals()
			.at(1706028).at(playerIndex, 53).at(5).at(index).get();
	}
	
	private int getLootPosition(int playerIndex, int index) {
		return (int) gta.globals()
			.at(1706028).at(playerIndex, 53).at(5).at(10).at(index).get();
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
		
		int from = type.ordinal() + 1;
		return 
			getLootPosition(playerIndex, from) == getLootPosition(playerIndex, from+4)
			&& getLootPosition(playerIndex, from+8) == getLootPosition(playerIndex, from+12);
	}
	
	@Override
	public void scopeLoot(int playerIndex, LootType type) {
		int from = type.ordinal() + 1;
		if ( type == LootType.PAINTINGS ) {
			from = 17;
		}
		int statScopeLootValue =  getLootPosition(playerIndex, from);
		int statJoaat[] = new int[] {
				-670391016, -404469783, -2365219, -1409842437, 1775008747
		};
		gta.getStatManager().setStat(statJoaat[type.ordinal()], statScopeLootValue);
	}
	
	public MainLoot getMainLoot(int playerIndex) {
		int loot = (int) gta.globals()
				.at(1706028).at(playerIndex, 53).at(5).at(9).get();
		return MainLoot.values()[ loot ];
	};
	
	@Override
	public int getScopedEquipment(int playerIndex) {
		// System.out.println("Get Scoped " + playerIndex);
		int val = (int) gta.globals()
			.at(1706028).at(playerIndex, 53).at(5).get();
		// System.out.println("  -> " + val);
		return val;
	}
	
	@Override
	public void addScopedEquipment(int equipmentMask) {
		int playerIndex = gta.localPlayerIndex();
		Global global = gta.globals().at(1706028).at(playerIndex, 53).at(5);
		long value = global.get() | equipmentMask;
		
		if ( (getApproach(playerIndex) & 1) == 0 ) {
			// set scope_out
			addApproach(1, 1);
		}
		
		gta.setStat("MP0_H4CNF_BS_GEN", (int)value);
		global.set(value);
	}
	
	@Override
	public int getApproach(int playerIndex) {
		// func_18 planning
		// return unk_0xCE990E643CD9D0E5(Global_1706028[iParam0 /53/].f_2, iParam1);
		// System.out.println("Get Approach " + playerIndex);
		int val = (int) gta.globals()
			.at(1706028).at(playerIndex, 53).at(2).get();
		// System.out.println("  -> " + val);
		return val;
	}
	
	@Override
	public void addApproach(int approachMask, int setMask) {
		int playerIndex = gta.localPlayerIndex();
		Global global = gta.globals().at(1706028).at(playerIndex, 53).at(2);
		long value = global.get();
		value = (value & ~setMask) | (approachMask & setMask);
		
		gta.setStat("MP0_H4_MISSIONS", (int) value);
		global.set( value );
	}
	
	@Override
	public int getWeapon(int playerIndex) {
		// Global_1706028[iParam0 /53/].f_5.f_35
		// func_359 planning
		return (int) gta.globals()
				.at(1706028).at(playerIndex, 53).at(5).at(35).get();
	}
	
	@Override
	public void setWeapon(int weaponIndex) {
		
		gta.setStat("MP0_H4CNF_WEAPONS", weaponIndex);
		
		gta.globals()
				.at(1706028).at(gta.localPlayerIndex(), 53).at(5).at(35).set((long)weaponIndex);

	}
	
	@Override
	public Map<String, Integer> getCuts() {
		Map<String, Integer> cuts = new LinkedHashMap<>();
		for (int j = 0; j < 4; j++) {
			long cut = gta.globals().at(1704127).at(823).at(56).at(j, 1).get();
			if ( cut == 0 ) break;
			cuts.put("Player " + (j+1), (int)cut);
		}
		return cuts;
	}
	
	@Override
	public void setCuts(Integer[] values) {
		for (int j = 0; j < values.length; j++) {			
			gta.globals().at(1704127).at(823).at(56).at(j, 1).set( (long) values[j] );
			// 823-67(j, 1) -> ready state ?
		}
	}
	
	@Override
	public int getCurrentLootValue(int index, MainLoot loot) {
		return (int) getLootGlobal(loot).get();
	}
	
	private Global getLootGlobal(MainLoot loot) {
		Global glLootValue = gta.globals().at(262145);
		switch(loot) {
		case TEQUILA: glLootValue.at(29233); break;
		case NECKLACE: glLootValue.at(29234); break;
		case BONDS: glLootValue.at(29235); break;
		case DIAMOND: glLootValue.at(29236); break;
		case FILES: glLootValue.at(29237); break;
		case STATUT: glLootValue.at(29238); break;
		}
		return glLootValue;
	}
	
	@Override
	// Global_262145.f_28999
	public int getMyBagSize() {
		return (int) gta.globals().at(262145).at(28999).get();
	}
	
	@Override
	public void setBagSize(int newBagSize) {
		gta.globals().at(262145).at(28999).set((long)newBagSize);
	}
	
	@Override
	public void setLootValue(MainLoot loot, int newLootValue) {
		getLootGlobal(loot).set((long)newLootValue);
	}
	
	@Override
	public boolean isHardMode(int playerIndex) {
		return (gta.globals()
				.at(1706028).at(gta.localPlayerIndex(), 53).at(1).get() & (1<<12)) > 0; 
	}
	
	@Override
	public void restartSubmarineComputer() {
		Script script = gta.getScript("heist_island_planning");
		if ( script == null ) return;
		script.locals().at(1525).setInt(2);
	}
}