package jmodmenu;

import jmodmenu.core.Global;
import jmodmenu.core.NetPlayerManager;
import jmodmenu.core.PlayerInfo;
import jmodmenu.core.Script;
import jmodmenu.core.StatManager;
import jmodmenu.memory.Ptr;

public class GtaProcess {
	
	private final static String 
		processName = "GTA5.exe";
	
	// int pid = -1;

	private Ptr basePointer;
	private Global globals;
	private PlayerInfo playerInfo;
	private NetPlayerManager netPlayerManager;
	private StatManager statManager;
	private Ptr threadList; 
	
	public GtaProcess() {
		initPointers();
	}
	
	private void initPointers() {
		basePointer = Ptr.ofProcess(processName);
		
		globals = Global.globalFrom(
			basePointer.findSig("48 8D 15 ? ? ? ? 4C 8B C0 E8 ? ? ? ? 48 85 FF 48 89 1D")
				.add(3)		
				.rip()
		);
		System.out.println( "globals " + globals);
		
		statManager = new StatManager(this::globals);
		
		netPlayerManager = NetPlayerManager.fromPointer(
				basePointer.findSig("48 8B 0D ? ? ? ? 8A D3 48 8B 01 FF 50 ? 4C 8B 07 48 8B CF")
					.add(3)
					.rip() // CNetworkPlayerMgr**
					.indirect64()
			);
			System.out.println( "Net Player Manager " + netPlayerManager);
		
		playerInfo = PlayerInfo.fromPointer(
			basePointer.findSig("48 8B 05 ? ? ? ? 48 8B 48 08 48 85 C9 74 52 8B 81")
				.add(3)
				.rip() // **CPedFactory
				.indirect64() // *CPedFactory
				.add(8) // CPed*
				.indirect64() // CPed
				.add(0x10B8 + 16)  // padding to ... *CPlayerInfo +8 because of vTable +8 because of array length stored first (??)
				.indirect64() // CPlayerInfo
		);
		System.out.println( "localPlayerInfo " + playerInfo);
		
		threadList = basePointer.findSig("48 8B 05 ? ? ? ? 8B CF 48 8B 0C C8 39 59 68")
				.add(3).rip().indirect64();
		
		playerInfo.setIndex(localPlayerIndex());
	}
	
	public Global globals() {
		return globals.copy();
	}
	
	public PlayerInfo localPlayer() {
		return playerInfo;
	}
	
	public int localPlayerIndex() {
		// act_cinema : func_865 !iParam0 == Global_2440049
		return (int) globals().at(2440049).get();
	}
	
	public NetPlayerManager getNetPlayerManager() {
		return netPlayerManager;
	}
	public StatManager getStatManager() {
		return statManager;
	}
	public Script getScript(String name) {
		Ptr currentThread = threadList.copy();
		for (int i = 0; i < 64; currentThread.add(8), i++) {
			Ptr nextThread = currentThread.copy().indirect64();
			String scriptName = 
					nextThread.copy().add(Script.NAME_OFFSET).readString(32);
			// System.out.format("[%d] Script: %s\n", i, scriptName);
			if (name.equals(scriptName)) {
				return Script.fromPointer(nextThread);
			}
		}
		return null;
	}
	
	public void setStat(String statName, int statValue) {
		System.out.format("Setting stat [%s] to => %d\n", statName, statValue);
		statManager.setStat(statName, statValue);
	}

}
