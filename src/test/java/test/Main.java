package test;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFO;

import jmodmenu.GtaProcess;
import jmodmenu.Utils;
import jmodmenu.core.Global;
import jmodmenu.core.PlayerInfo;
import jmodmenu.memory.Ptr;



public class Main {

	@Test
    public void testOsVersion() {
        OSVERSIONINFO lpVersionInfo = new OSVERSIONINFO();
        Kernel32.INSTANCE.GetVersionEx(lpVersionInfo);
        System.out.println("Operating system: "
                + lpVersionInfo.dwMajorVersion.longValue() + "." + lpVersionInfo.dwMinorVersion.longValue()
                + " (" + lpVersionInfo.dwBuildNumber + ")"
                + " [" + Native.toString(lpVersionInfo.szCSDVersion) + "]");
        // junit.textui.TestRunner.run(Kernel32Test.class);
    }
    
	@Test
	public void testGetProcesses() {
		ProcessHandle.allProcesses()
			.forEach( p -> System.out.println( p.info().command().orElse("") + " " + p.pid()) );
		
	}
    
	// @Test
	public void testModeulHandle() {
		assertNotNull( Kernel32.INSTANCE.GetModuleHandle("GTA5.exe") );
	}
	
	// @Test
	public void testProcessGta() {
		new GtaProcess();
	}
	
	// @Test
	public void testReadGtaMemory() {
		GtaProcess gta = new GtaProcess();
		// Global_1590535[iParam0 /*876*/].f_274.f_183[iVar0 /*12*/]
		System.out.println( gta.globals().at(1590535).get() );
		// gta.readMemory( (1590535+1) + (876*0) + 274 + (183+1) );
	}
	
	@Test
	public void testParseByte() {
		System.out.println( Byte.parseByte("0xA0") );
	}
	
	// @Test
	public void testNetworkPlayerManager() {
		Ptr ptr = Ptr.ofProcess("GTA5.exe");
		ptr = ptr.findSig("48 8B 0D ? ? ? ? 8A D3 48 8B 01 FF 50 ? 4C 8B 07 48 8B CF")
				.add(3)
				.rip() // CNetworkPlayerMgr**
				.indirect64()  // CNetworkPlayerMgr*
				/*
				.indirect64()  // CNetworkPlayerMgr
				.add(8) 
				.indirect64(); // netObject ?
				*/
				;
		System.out.println(ptr);
		ptr.dumpfrom(300);
		
	}
	
	// @Test
	public void testFindPlayerInfo() {
		Ptr ptr = Ptr.ofProcess("GTA5.exe");
		ptr = ptr.findSig("48 8B 05 ? ? ? ? 48 8B 48 08 48 85 C9 74 52 8B 81")
			.add(3)
			.rip() // **CPedFactory
			.indirect64() // *CPedFactory
			.add(8) // CPed*
			.indirect64() // CPed
			.add(0x10B8 + 16)  // padding to ... *CPlayerInfo +8 because of vTable +8 because of array length stored first (??)
			.indirect64() // CPlayerInfo
			.add(8) // vTable
			.add(0x007C)
			;
		
		ptr.dumpfrom(16);
		System.out.println();
		
		String name = ptr.readString(16);
		System.out.println("Found player name: " + name);
		
		/* */
		boolean ok = ptr.writeString("RandomPlayer");
		System.out.println( "Write operation " + (ok ? "succeed" : "failed") );
		ptr.dumpfrom(16);
		System.out.println( ptr );
		/* */
		
		// restore playerName
		ptr.writeString(name);
	}
	
	// @Test
	public void testFindZoneNameInfo() {
		Ptr ptr = Ptr.ofProcess("GTA5.exe");
		ptr = ptr.findSig("00 88 1D ? ? ? ? 88 1D ? ? EB 01 89 1D ? ? EB 01")
			.add(3)
			.rip() 
			;
		
		ptr.dumpfrom(200);
		
		System.out.println();
	}
	
	// @Test
	public void testFindStreetNameInfo() {
		Ptr ptr = Ptr.ofProcess("GTA5.exe");
		ptr = ptr.findSig("EB 88 15 ? ? ? ? 88 15 ? ? C5 00 89 15 ? ? C5 00")
			.add(3)
			.rip()
			;
		
		ptr.dumpfrom(200);
		
		System.out.println();
	}
	
	// @Test
	public void testFindCarNameInfo() {
		Ptr ptr = Ptr.ofProcess("GTA5.exe");
		ptr = ptr.findSig("48 8D 0D ? ? ? ? 48 83 C4 28 E9 ? ? 48 00 48 8B C4")
			.add(3)
			.rip() 
			;
		
		ptr.dumpfrom(200);
		
		System.out.println();
	}
	
	// @Test
	public void testWorldPtr() {
		Ptr ptr = Ptr.ofProcess("GTA5.exe");
		ptr = ptr.findSig("48 8B 05 ? ? ? ? 45 ? ? ? ? 48 8B 48 08 48 85 C9 74 07")
			.add(3)
			.rip() 
			;
		
		ptr.dumpfrom(200);
		
		System.out.println();
	}
	
	@Test
	public void testPrint() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
		PrintWriter out = new PrintWriter( bos, true, Charset.forName("ISO-8859-1") );
		out.print("Atomik_again");
		out.flush();
		bos.write(0);
		byte[] buffer = bos.toByteArray();
		for (int i = 0; i < buffer.length; i++) {
			System.out.print( String.format("%02x", buffer[i]) + " ");
		}
	}
	
	// @Test
	public void testFindFloat() {
		Ptr ptr = Ptr.ofProcess("GTA5.exe");
		Global g = Global.globalFrom( ptr.findSig("48 8D 15 ? ? ? ? 4C 8B C0 E8 ? ? ? ? 48 85 FF 48 89 1D")
			.add(3)		
			.rip()
			.copy()
		);
		
		
		Ptr divider = g.at(262145)
				.at(23865)
				.ptr();
		
		float f = divider.readFloat();
		
		System.out.format("Production divider : %.4f", f);
		
		// boolean succeed = divider.writeFloat(0.005f);
		// System.out.println( succeed );
	}
	
	
	// @Test
	public void testGtaProcess() {
		GtaProcess gta = new GtaProcess();
		int i = 1;
		for ( PlayerInfo playerInfo : gta.getNetPlayerManager().getPlayers() ) {
			System.out.format("***** %d:(%d) %s\n", i++, playerInfo.getIndex(), playerInfo.getName());
			// Global globals = gta.globals();
			for (int k = 0; k < 7; k++) {
				long stock = gta.globals()
						.at(1590535)
						.at(playerInfo.getIndex(), 876)
						.at(274)
						.at(281)
						.at(9)
						.at(k)
						.get();
				System.out.println("  "+k+": " + stock);
			}
		}
	}
	
	// @Test
	public void testAllSlotNightclubStock() {
		Ptr ptr = Ptr.ofProcess("GTA5.exe");
		ptr = ptr.findSig("48 8D 15 ? ? ? ? 4C 8B C0 E8 ? ? ? ? 48 85 FF 48 89 1D")
			.add(3)		
			.rip();
		ptr.dumpAround(10);			
		System.out.println(ptr);
		Ptr globals = ptr.copy();
		
		
		for (int i = 0; i < 32; i++) {
			System.out.println("---- Joueur " + i + " -----");
			for ( int j = 0; j < 7; j++ ) {
				long stock = Global.globalFrom(globals)
					.at(1590535)
					.at(i, 876)
					.at(274)
					.at(281)
					.at(9)
					.at(j)
					.get();
				System.out.println("  "+j+": " + stock);
			}
			System.out.println();
		}
	}
	
	@Test
	public void testJoaat() {
		System.out.format("%s\t0x%08x\n", "-1", Integer.MIN_VALUE);
		System.out.format("%s\t0x%08x\n", "-1", Integer.MIN_VALUE >> 1);
		System.out.format("%s\t0x%08x\n", "-1", Integer.rotateRight(Integer.MIN_VALUE, 1) );
		
		System.out.format("%s\t0x%08x\n", "-1", 1);
		System.out.format("%s\t0x%08x\n", "-1", 1 >> 1);
		System.out.format("%s\t0x%08x\n", "-1", Integer.rotateRight(1, 1) );
		
		
		Arrays.asList( 
			"MP0_LAP_DANCED_BOUGHT", 
			"MP1_LAP_DANCED_BOUGHT"
		)
		.stream()
		.forEach( str -> {
			int hash = Utils.joaat(str);
			System.out.format("%s\t0x%08x\n", str, hash);
		});
	}
	

}
