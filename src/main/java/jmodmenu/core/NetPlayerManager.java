package jmodmenu.core;

import java.util.LinkedList;
import java.util.List;

import jmodmenu.memory.Ptr;
import jmodmenu.memory.RefPtr;

public class NetPlayerManager extends RefPtr {
			
	NetPlayerManager(Ptr ptr) {
		super(ptr);
	}

	public static NetPlayerManager fromPointer(Ptr ptr) {
		NetPlayerManager netPlayerManager = new NetPlayerManager(ptr);
		return netPlayerManager;
	}
	
	public int getNumPlayers() {
		return base.copy().add(0x0118).readInt();
	}
	
	public List<PlayerInfo> getPlayers() {
		Ptr cNetGamePlayer = base.copy().add(0x0180);
		List<PlayerInfo> res = new LinkedList<>();
		for ( int i = 0; i < 32; i++, cNetGamePlayer.add(8) ) {
			long l = cNetGamePlayer.readLong();
			if ( l == 0 ) continue;
			// System.out.println("--- "+i+": 0x"+Long.toHexString(l));
			PlayerInfo playerInfo = PlayerInfo.fromPointer(
					cNetGamePlayer.copy()
					.indirect64()
					.add(0x00A0)
					.indirect64()
				);
			playerInfo.setIndex(i);
			res.add(playerInfo);
		}
		return res;
	}
}
