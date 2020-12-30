package jmodmenu;

import java.util.function.Function;

import jmodmenu.core.Global;
import jmodmenu.core.NetPlayerManager;
import jmodmenu.core.PlayerInfo;
import jmodmenu.core.Script;
import jmodmenu.core.StatManager;
import jmodmenu.memory.Ptr;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GtaProcess {
	
	private final static String 
		processName = "GTA5.exe";
	
	private final static String
		SIG_GLOBALS = "48 8D 15 ? ? ? ? 4C 8B C0 E8 ? ? ? ? 48 85 FF 48 89 1D",
		SIG_NET_MANAGER = "48 8B 0D ? ? ? ? 8A D3 48 8B 01 FF 50 ? 4C 8B 07 48 8B CF",
		SIG_LOCAL_PLAYER = "48 8B 05 ? ? ? ? 48 8B 48 08 48 85 C9 74 52 8B 81",
		SIG_THREAD_LIST = "48 8B 05 ? ? ? ? 8B CF 48 8B 0C C8 39 59 68"
		;
	
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
		
		Function<String, Ptr> sigAdd3Rip = signature -> {
			Ptr ptr = basePointer.findSig(signature)
					.add(3)		
					.rip();
			if ( ptr == null ) log.error("Signature {} not found.", signature);
			return ptr;
		};
		
		globals = Global.globalFrom( sigAdd3Rip.apply(SIG_GLOBALS) );
		log.info("Globals at {}", globals);
		
		statManager = new StatManager(this::globals);
		
		netPlayerManager = NetPlayerManager.fromPointer(
			sigAdd3Rip.apply(SIG_NET_MANAGER).indirect64()
		);
		log.info("Net Player Manager at {}", netPlayerManager);
		
		playerInfo = PlayerInfo.fromPointer(
				sigAdd3Rip.apply(SIG_LOCAL_PLAYER)
				.indirect64() // *CPedFactory
				.add(8) // CPed*
				.indirect64() // CPed
				.add(0x10B8 + 16)  // padding to ... *CPlayerInfo +8 because of vTable +8 because of array length stored first (??)
				.indirect64() // CPlayerInfo
		);
		log.info( "localPlayerInfo at {}", playerInfo);
		
		threadList = sigAdd3Rip.apply(SIG_THREAD_LIST).indirect64();
		
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
	
	public int slotNumPlayer() {
		// heist_island_planning func_6
		return (int) globals().at(1312763).get();
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
			String scriptName =  nextThread.copy().add(Script.NAME_OFFSET).readString(32);
			// System.out.format("[%d] Script: %s\n", i, scriptName);
			if (name.equals(scriptName)) {
				return Script.fromPointer(nextThread);
			}
		}
		return null;
	}
	
	public void setStat(String statName, int statValue) {
		log.info( String.format("Setting stat [%s] to dec[%d] hex[%08x] %d", statName, statValue, statValue) );
		statManager.setStat(statName, statValue);
	}

	@Override
	public String toString() {
		return basePointer.toString();
	}
}
