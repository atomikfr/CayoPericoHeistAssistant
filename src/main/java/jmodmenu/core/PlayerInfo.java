package jmodmenu.core;

import jmodmenu.memory.Ptr;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerInfo  {
	
	int index;
	String name;

	public static PlayerInfo fromPointer(Ptr ptr) {
		PlayerInfo playerInfo = new PlayerInfo();
		playerInfo.name = ptr.copy().add(0x0084).readString(20);
		return playerInfo;
	}
	
	@Override
	public String toString() {
		return String.format("[%02d] %s", index, getName());
	}

}
