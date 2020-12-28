package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.function.Function;

import org.junit.Test;

import jmodmenu.GtaProcess;
import jmodmenu.Utils;
import jmodmenu.cayo_perico.model.MainLoot;
import jmodmenu.core.Global;
import jmodmenu.core.PlayerInfo;
import jmodmenu.core.Script;
import jmodmenu.memory.Ptr;

public class Test_1_52 {
	
	// @Test
	public void testListIslandLoots() {
		Ptr ptr = Ptr.ofProcess("GTA5.exe");
		ptr = ptr.findSig("48 8D 15 ? ? ? ? 4C 8B C0 E8 ? ? ? ? 48 85 FF 48 89 1D")
			.add(3)		
			.rip();
		ptr.dumpAround(10);			
		System.out.println(ptr);
		Ptr globals = ptr.copy();
		
		
		for (int i = 0; i < 32; i++) {
			// int func_565(int iParam0) :: heist_island_planning.ysc
			long loot = Global.globalFrom(globals)
				.at(1706028)
				.at(i, 53)
				.at(5)
				.at(9)
				.get();
			System.out.println("---- Joueur["+i+"] --> " + loot);
		}
		
	}
	
	// @Test
	public void testGtaProcess() {
		GtaProcess gta = new GtaProcess();
		assertNotNull(gta.getNetPlayerManager());
		assertNotNull(gta.localPlayer());
		assertNotNull(gta.globals());
	}
	
	// @Test
	public void testPericoLootsNetPlayerMgr() {
		GtaProcess gta = new GtaProcess();
		int i = 1;
		for ( PlayerInfo playerInfo : gta.getNetPlayerManager().getPlayers() ) {
			System.out.format("***** %d:(%d) %s\n", i++, playerInfo.getIndex(), playerInfo.getName());
			long loot = gta.globals()
					.at(1706028)
					.at(playerInfo.getIndex(), 53)
					.at(5)
					.at(9)
					.get();
				System.out.println("---- Joueur["+i+"] --> " + MainLoot.values()[(int)loot] );
		}
	}
	
	// @Test
	public void testPericoLootsPaintings() {
		GtaProcess gta = new GtaProcess();
		int i = 1;
		System.out.println("Objectifs secondaires");
		for ( PlayerInfo playerInfo : gta.getNetPlayerManager().getPlayers() ) {
			System.out.format("***** %d:(%d) %s\n", i, playerInfo.getIndex(), playerInfo.getName());
			long loot = gta.globals()
					.at(1706028)
					.at(playerInfo.getIndex(), 53)
					.at(5).at(10).at(17).get();
			System.out.println("    Peinture: --> " + loot );
			
			
			Function<Integer, Integer> getItemPositions = idx -> 
				(int) (gta.globals()
				.at(1706028).at(playerInfo.getIndex(), 53).at(5).at(idx).get());
			

			System.out.println("    Grappins: --> " + getItemPositions.apply(3) );
			System.out.println("    Uniformes: --> " + getItemPositions.apply(4) );
			System.out.println("    Pinces: --> " + getItemPositions.apply(5) );
						
						
				
			for (int j = 0; j < 4; j++) {
				loot = gta.globals()
						.at(1706028)
						.at(playerInfo.getIndex(), 53)
						.at(5).at(10).at(9+j).get();
				System.out.println("    Item repéré ("+(9+j)+"): --> " + loot );
			}
			
			for (int j = 0; j < 4; j++) {
				loot = gta.globals()
						.at(1706028)
						.at(playerInfo.getIndex(), 53)
						.at(5).at(10).at(13+j).get();
				System.out.println("    Item map ("+(13+j)+"): --> " + loot );
			}
			i++;
		}
	}
	
	
	// @Test
	public void testTriggerTshirtAward() throws Exception {
		int nextAvailable = -1;
		GtaProcess gta = new GtaProcess();
		// freemode func_2163 (v 1.52)
		for (int i = 0; i <= 9; i++) {
			if ( gta.globals().at(1385106).at(i, 106).get() == 0 ) {
				nextAvailable = i;
				break;
			}
		}
		System.out.println( "Next available -> " + nextAvailable );
		
		long before = gta.globals().at(2515199).get();
		System.out.println(before);
		
		/*
		 * 
		gta.globals().at(2515199).set(1L);
		Thread.sleep(250);
		gta.globals().at(2515199).set(before);
		
		String sVar0 = "UNLOCK_AWD_MASK";
		String sParam2 = "UNLOCK_DESC_SHIRT3";
		String sParam3 = "FeedhitTshirt04";
		String sParam4 = "MPTshirtAwards3";
		
		gta.globals().at(1385106).at(nextAvailable, 106).set(12);
		gta.globals().at(1385106).at(nextAvailable, 106).at(17).setString(sParam3, 64);
		gta.globals().at(1385106).at(nextAvailable, 106).at(1).setString(sParam4, 64);
		gta.globals().at(1385106).at(nextAvailable, 106).at(33).setString(sVar0, 64);
		gta.globals().at(1385106).at(nextAvailable, 106).at(49).setString(sParam2, 64);
		
		
		int iParam5 = -1;
		int iParam9 = -1;
		int iParam10 = 0;
		
		gta.globals().at(1385106).at(nextAvailable, 106).at(97).set(iParam5);
		gta.globals().at(1385106).at(nextAvailable, 106).at(104).set(iParam9);
		gta.globals().at(1385106).at(nextAvailable, 106).at(105).set(iParam10);
		
		// gta.globals().at(1385106).at(nextAvailable, 106).at(98).setString(12);
		*/
		
		System.out.println("Job Done !");
	}
	
	@Test
	public void testJooat() {
		System.out.println( Utils.joaat("MPPLY_AWD_FM_CR_MISSION_SCORE") );
	}
	
	
	// @Test
	public void getStatRegister() {
		GtaProcess gta = new GtaProcess();
		Global gl_MP0_LAP_DANCED_BOUGHT = gta.globals().at(2551832).at(1089, 3).at(0, 1);
		int MP0_LAP_DANCED_BOUGHT = (int) gl_MP0_LAP_DANCED_BOUGHT.get();		
		assertEquals(0xca05bafc, MP0_LAP_DANCED_BOUGHT);
		
		System.out.println( "Global " + gl_MP0_LAP_DANCED_BOUGHT);
		System.out.format("Stored  : %s\t0x%08x\n", "MP0_LAP_DANCED_BOUGHT", MP0_LAP_DANCED_BOUGHT);
		
		int myHash = Utils.joaat("MP0_LAP_DANCED_BOUGHT");
		assertEquals(0xca05bafc, myHash);
		System.out.format("computed: %s\t0x%08x\n", "MP0_LAP_DANCED_BOUGHT", myHash);
	}
	
	// @Test
	public void testModifyStat() throws Exception {
		GtaProcess gta = new GtaProcess();
		Global globalStatValue = gta.globals().at(939452).at(5526);
		Global globalTrigger = gta.globals().at(1388013).at(3, 1);
		Global globalStatIndex = gta.globals().at(2589533).at(15, 3).at(0, 1);
		Global globalCheckValue = gta.globals().at(2551544).at(276);
		
		String statName = "MP0_H4CNF_BS_GEN";
		int statValue = 131071;
		
		long previous1 = globalStatValue.get();
		
		long previous2 = globalStatIndex.get();
		assertEquals(Utils.joaat("MP0_AWD_LAPDANCES"), previous2);
		
		long previous3 = globalTrigger.get();
		assertEquals(Utils.joaat("MPPLY_AWD_FM_CR_MISSION_SCORE"), previous3);
		
		System.out.println("Before trigger, checkvalue is : " + globalCheckValue.get());
		
		globalStatIndex.set( Integer.toUnsignedLong(Utils.joaat(statName)) );
		globalTrigger.set( Integer.toUnsignedLong(Utils.joaat("MP0_LAP_DANCED_BOUGHT")) );
		globalStatValue.set( Integer.toUnsignedLong(statValue) );
		
		int loop = 0;
		int current;
		do {
			Thread.sleep(250);
			current = (int) globalCheckValue.get();
			System.out.println("loop"+loop+" checkvalue is : " + globalCheckValue.get());
		} while ( current != statValue && loop++ < 10 );
		
		globalStatIndex.set(previous2);
		globalStatValue.set(0L);
		loop = 0;
		do {
			Thread.sleep(250);
			current = (int) globalCheckValue.get();
			System.out.println("loop"+loop+" checkvalue is : " + globalCheckValue.get());
		} while ( current != statValue && loop++ < 10 );
		globalTrigger.set(previous3);
		globalStatValue.set(previous1);
	}
	
	// @Test
	public void testFixLapDance() throws Exception {
		GtaProcess gta = new GtaProcess();
		Global globalStatValue = gta.globals().at(939452).at(5526);
		Global globalTrigger = gta.globals().at(1388013).at(3, 1);
		Global globalStatIndex = gta.globals().at(2589533).at(15, 3).at(0, 1);
		Global globalCheckValue = gta.globals().at(2551544).at(276);
		
		String statName = "MP0_LAP_DANCED_BOUGHT";
		int statValue = 0;
		
		long previous1 = globalStatValue.get();
		
		long previous2 = globalStatIndex.get();
		assertEquals(Utils.joaat("MP0_AWD_LAPDANCES"), previous2);
		
		long previous3 = globalTrigger.get();
		assertEquals(Utils.joaat("MPPLY_AWD_FM_CR_MISSION_SCORE"), previous3);
		
		System.out.println("Before trigger, checkvalue is : " + globalCheckValue.get());
		
		globalStatIndex.set( Integer.toUnsignedLong(Utils.joaat(statName)) );
		globalTrigger.set( Integer.toUnsignedLong(Utils.joaat("MP0_LAP_DANCED_BOUGHT")) );
		globalStatValue.set( Integer.toUnsignedLong(statValue) );
		
		int loop = 0;
		int current;
		do {
			Thread.sleep(250);
			current = (int) globalCheckValue.get();
			System.out.println("loop"+loop+" checkvalue is : " + globalCheckValue.get());
		} while ( current != statValue && loop++ < 10 );
		
		globalStatIndex.set(previous2);
		globalStatValue.set(0L);
		loop = 0;
		do {
			Thread.sleep(250);
			current = (int) globalCheckValue.get();
			System.out.println("loop"+loop+" checkvalue is : " + globalCheckValue.get());
		} while ( current != statValue && loop++ < 10 );
		globalTrigger.set(previous3);
		globalStatValue.set(previous1);
	}
	
	// @Test
	public void testStatManager() {
		
		GtaProcess gta = new GtaProcess();
		gta.setStat("MP0_H4CNF_BS_GEN", 131071);
		// gta.setStat("MP0_H4_MISSIONS", 28609);
		
	}
	
	// @Test
	public void testReadCut() {
		
		GtaProcess gta = new GtaProcess();
		
		for (int i = 0; i < 4; i++) {
			System.out.println("Cut " + i);
			System.out.println(
					"  - " +
					gta.globals().at(1705227).at(gta.localPlayer().getIndex(), 25).at(12).at(i, 1).get()
			);
			System.out.println(
					"  - " +
					gta.globals().at(1704127).at(823).at(56).at(i, 1).get()
			);
		}
		
	}
	
	// @Test
	public void testChangeCut() {
		
		GtaProcess gta = new GtaProcess();
		
		for (int i = 0; i < 3; i++) {
			// gta.globals().at(1705227).at(gta.localPlayer().getIndex()).at(12).at(i, 1).set(85L);
			gta.globals().at(1704127).at(823).at(56).at(i, 1).set(85L);
		}
		
	}
	
	// @Test
	public void test() {
		GtaProcess gta = new GtaProcess();
		
		int index = (int) gta.globals().at(1704127).at(1084).get();
		
		System.out.println( "index: " + index);
		System.out.println( "localplayer index: " + gta.localPlayer().getIndex());
		
		Global glGen = gta.globals().at(1706028).at(index, 53).at(5);
		long current = glGen.get();
		System.out.println("before: " + current);
		current = current | ((0x0F << 8) & 0xFF00);
		System.out.println("after: " + current);
		glGen.set(current);
		
	}
	
	// @Test
	public void testLocalPointer() {
		/*
		Ptr ptr = Ptr.ofProcess("GTA5.exe").findSig("48 8B 05 ? ? ? ? 8B CF 48 8B 0C C8 39 59 68");
		assertNotNull(ptr);
		ptr = ptr.add(3).rip().indirect64();
		Ptr found = null;
		for (int i = 0; i < 99; ptr.add(8), i++) {
			Ptr nextThread = ptr.copy().indirect64();
			String scriptName = 
					nextThread.copy().add(0xD0).readString(32);
			System.out.format("[%d] Script: %s\n", i, scriptName);
			if ("heist_island_planning".equals(scriptName)) {
				found = nextThread;
				break;
			}
		}
		if ( found == null ) {
			System.out.println("not found");
			return;
		}
		System.out.println(found);
		
		// Now indirect to local vars table
		Ptr locals = found.copy().add(0xB0);
		
		System.out.println( "Locals pointer: ");
		locals.dumpfrom(16);
		
		locals.indirect64();
		
		Ptr local129 = locals.copy().add( 1525 * 8 );
		local129.dumpfrom(200);
		System.out.println(local129);
		/* */
		
		/* */
		GtaProcess gta = new GtaProcess();
		Script script = gta.getScript("heist_island_planning");
		System.out.println( script.locals().at(1525).getInt() );
		
		script.locals().at(1525).setInt(2);
		/* */
	}
	
	
	// @Test
	public void testSetLootPosition() {
		GtaProcess gta = new GtaProcess();
		
		// Global_1706028[iParam0 /*53*/].f_5.f_10.f_5
		int iParam0 = gta.localPlayerIndex();
		
		int onMap[] = new int[4];
		
		int[] statJoaat = null;
		
		/* */
		for (int i = 1; i <= 4; i++) { // out
			long l =
			  gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(i).get();
			onMap[i-1] = (int) l;
			System.out.format("%d -> %d\n", i, l);
		}
		statJoaat = new int[] {
				-1690987375, 834429412, 611902845, 1712421353
		};
		for (int i = 5; i <= 8; i++) { 
			long l =
			  gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(i).get();
			System.out.format("%d -> %d\n", i, l);
			
			int statIndex = statJoaat[i-5];
			int statValue = onMap[i-5];
			System.out.format("Set stat %d to %s\n", statIndex, statValue);
			gta.getStatManager().setStat(statIndex, statValue);
		}
		/* */
		
		
		/* 9 - 12  => 13 - 16 */
		/* */
		for (int i = 9; i <= 12; i++) { // in
			long l =
			  gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(i).get();
			onMap[i-9] = (int) l;
			System.out.format("%d -> %d\n", i, l);
		}
		statJoaat = new int[] {
				-670391016, -404469783, -2365219, -1409842437
		};
		for (int i = 13; i <= 16; i++) { 
			long l =
			  gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(i).get();
			System.out.format("%d -> %d\n", i, l);
			
			int statIndex = statJoaat[i-13];
			int statValue = onMap[i-13];
			System.out.format("Set stat %d to %s\n", statIndex, statValue);
			gta.getStatManager().setStat(statIndex, statValue);
		}
		/* */
		
		int i = 17;
		int paintMap = (int) gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(i).get();
		System.out.format("Paint map (%d) -> %d\n", i, paintMap);
		
		i = 18;
		long paintScoped = gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(i).get();
		System.out.format("Paint scope (%d) -> %d\n", i, paintScoped);
		int paintIndex = 1775008747;
		gta.getStatManager().setStat(paintIndex, paintMap);
		
		
	}
	
	
	// @Test
	public void testSetAllPaintings() {
		GtaProcess gta = new GtaProcess();		
		int allPaintings = 0x7F;
		int iParam0 = gta.localPlayerIndex();

		// gta.getStatManager().setStat(1997903113, allPaintings);
		// gta.getStatManager().setStat(1775008747, allPaintings);
		/* */
		int paintMap = (int) gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(17).get();
		System.out.format("Paint map   -> %d\n", paintMap);
		paintMap = (int) gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(18).get();
		System.out.format("Paint scope -> %d\n", paintMap);
		
		int gold = (int) gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(12).get();
		System.out.println(gold);
		
		gold     = (int) gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(16).get();
		System.out.println(gold);
		/* */
	}
	
	// @Test
	public void testSetAllGold() {
		GtaProcess gta = new GtaProcess();
		
		// Global_1706028[iParam0 /*53*/].f_5.f_10.f_5
		int iParam0 = gta.localPlayerIndex();
		
		int onMap[] = new int[4];
		
		int[] statJoaat = null;
		
		/* */
		for (int i = 1; i <= 4; i++) { // out
			long l =
			  gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(i).get();
			onMap[i-1] = (int) l;
			System.out.format("%d -> %d\n", i, l);
			statJoaat = new int[] { -1646922924, -781945763, -1003353535, -1153023047 };
			int statValue = i == 4 ? 0x00FFFFFF: 0;// onMap[i-5];
			gta.getStatManager().setStat(statJoaat[i-1], statValue);
		}
		statJoaat = new int[] {
				-1690987375, 834429412, 611902845, 1712421353
		};
		for (int i = 5; i <= 8; i++) { 
			long l =
			  gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(i).get();
			System.out.format("%d -> %d\n", i, l);
			
			int statIndex = statJoaat[i-5];
			int statValue = i == 8 ? 0x00FFFFFF: 0;// onMap[i-5];
			System.out.format("Set stat %d to %s\n", statIndex, statValue);
			gta.getStatManager().setStat(statIndex, statValue);
		}
		/* */
		
		
		/* 9 - 12  => 13 - 16 */
		/* */
		for (int i = 9; i <= 12; i++) { // in
			long l =
			  gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(i).get();
			onMap[i-9] = (int) l;
			System.out.format("Was %d -> %d\n", i, l);
			statJoaat = new int[] { -399833087, 1093424111, 972322244, 803319022 };
			int statValue = i == 12 ? 0x00FFFFFF: 0;// onMap[i-5];
			System.out.format("Set %d -> %d\n", statJoaat[i-9], statValue);
			gta.getStatManager().setStat(statJoaat[i-9], statValue);
		}
		statJoaat = new int[] {
				-670391016, -404469783, -2365219, -1409842437
		};
		for (int i = 13; i <= 16; i++) { 
			long l =
			  gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(i).get();
			System.out.format("%d -> %d\n", i, l);
			
			int statIndex = statJoaat[i-13];
			int statValue = i == 16 ? 0x00FFFFFF : 0;// onMap[i-13];
			System.out.format("Set stat %d to %s\n", statIndex, statValue);
			gta.getStatManager().setStat(statIndex, statValue);
		}
		/* */
		
		int i = 17;
		int paintMap = (int) gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(i).get();
		System.out.format("Paint map (%d) -> %d\n", i, paintMap);
		
		i = 18;
		long paintScoped = gta.globals().at(1706028).at(iParam0, 53).at(5).at(10).at(i).get();
		System.out.format("Paint scope (%d) -> %d\n", i, paintScoped);
		int paintIndex = 1775008747;
		gta.getStatManager().setStat(paintIndex, paintMap);
		
		
	}
	
	
	
}
