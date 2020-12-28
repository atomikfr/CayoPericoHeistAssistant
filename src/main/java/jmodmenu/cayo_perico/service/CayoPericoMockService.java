package jmodmenu.cayo_perico.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import jmodmenu.core.PlayerInfo;

public class CayoPericoMockService implements CayoPericoMapService {

	@Override
	public List<PlayerInfo> getPlayersInfo() {
		PlayerInfo playerInfo = new PlayerInfo();
		playerInfo.setIndex(0);
		playerInfo.setName("Test Player 1");
		
		PlayerInfo playerInfo2 = new PlayerInfo();
		playerInfo2.setIndex(1);
		playerInfo2.setName("Test Player 2");
		
		return Arrays.asList( playerInfo, playerInfo2 );
	}
	
	@Override
	public int getLocalPlayerIndex() {
		return 0;
	}

	@Override
	public List<MapItem> getEquipment(int playerIndex) {
		return Arrays.asList(
			new GrapplingEquipment(2),
			new GrapplingEquipment(5),
			new GrapplingEquipment(10),
			new GrapplingEquipment(15),
			
			new GuardUniform(2),
			new GuardUniform(5),
			new GuardUniform(10),
			new GuardUniform(15),
			
			new BoltCutters(2),
			new BoltCutters(5),
			new BoltCutters(10),
			new BoltCutters(15),
			
			new GuardTruck(3)
		);
	}
	
	@Override
	public List<SecondaryIslandLoot> getIslandLoot(int playerIndex) {
		return Arrays.asList(
			new SecondaryIslandLoot(LootType.CASH, 1),
			new SecondaryIslandLoot(LootType.WEED, 3),
			new SecondaryIslandLoot(LootType.COCAINE, 4),
			new SecondaryIslandLoot(LootType.CASH, 6),
			new SecondaryIslandLoot(LootType.WEED, 8),
			new SecondaryIslandLoot(LootType.COCAINE, 9),
			new SecondaryIslandLoot(LootType.CASH, 11),
			new SecondaryIslandLoot(LootType.CASH, 12),
			new SecondaryIslandLoot(LootType.WEED, 14),
			new SecondaryIslandLoot(LootType.WEED, 15),
			new SecondaryIslandLoot(LootType.CASH, 17),
			new SecondaryIslandLoot(LootType.CASH, 18),
			new SecondaryIslandLoot(LootType.COCAINE, 20),
			new SecondaryIslandLoot(LootType.CASH, 21),
			new SecondaryIslandLoot(LootType.COCAINE, 23),
			new SecondaryIslandLoot(LootType.CASH, 24)			
		);
	}
	
	@Override
	public List<SecondaryCompundLoot> getCompoundLoot(int playerIndex) {
		return Arrays.asList(
			new Paintings(0),
			new Paintings(1),
			new Paintings(2),
			new Paintings(3),
			new Paintings(4),
			new Paintings(5),
			new Paintings(6),
			new SecondaryCompundLoot(LootType.CASH, 0),
			new SecondaryCompundLoot(LootType.CASH, 1),
			new SecondaryCompundLoot(LootType.CASH, 2),
			new SecondaryCompundLoot(LootType.CASH, 3),
			new SecondaryCompundLoot(LootType.CASH, 4),
			new SecondaryCompundLoot(LootType.CASH, 5),
			new SecondaryCompundLoot(LootType.CASH, 6),
			new SecondaryCompundLoot(LootType.CASH, 7)
		);
	}
	
	@Override
	public boolean hasScopedLoot(int playerIndex, LootType type) {
		if ( type == LootType.CASH ) return true;
		if ( type == LootType.PAINTINGS ) return true;
		if ( type == LootType.COCAINE ) return true;
		return false;
	}
	
	@Override
	public void scopeLoot(int playerIndex, LootType type) {}
	
	public MainLoot getMainLoot(int playerIndex) {
		return MainLoot.BONDS;
	}
	
	@Override
	public int getScopedEquipment(int playerIndex) {
		return 0xF;
	}
	
	@Override
	public void addScopedEquipment(int equipmentMask) {
	}
	
	@Override
	public int getApproach(int playerIndex) {
		return 60929;
	}
	
	@Override
	public void addApproach(int approachMask, int setMask) {
	}
	
	@Override
	public int getWeapon(int index) {
		return 2;
	}
	
	@Override
	public void setWeapon(int weaponIndex) {
	}
	
	@Override
	public Map<String, Integer> getCuts() {
		return new LinkedHashMap<>() {{
			put("EvilPlay3r", 70);
			put("SpookyFace", 15);
			put("CheatyGuy", 15);
		}};
	}
	
	@Override
	public void setCuts(Integer[] values) {}
	
	
	@Override
	public int getCurrentLootValue(int index, MainLoot loot) {
		return loot.value();
	}
	
	@Override
	public int getMyBagSize() {
		return 1800;
	}

	@Override
	public void setBagSize(int newBagSize) {
	}
	
	@Override
	public void setLootValue(MainLoot loot, int newLootValue) {
	}
	
	@Override
	public boolean isHardMode(int playerIndex) {
		return playerIndex%2 == 0;
	}
	
	@Override
	public void restartSubmarineComputer() {}
}