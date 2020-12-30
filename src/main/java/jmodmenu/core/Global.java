package jmodmenu.core;

import jmodmenu.memory.Ptr;
import jmodmenu.memory.RefPtr;

public class Global extends RefPtr {
	
	// Ptr base;
	int index;
	
	private Global(Ptr ptr) { super(ptr); }
	
	public Global at(int index) {
		this.index += index;
		return this;
	}
	
	public Global at(int index, int size) {
		this.index += (index * size) + 1;
		return this;
	}
	
	public Global copy() {
		Global global = new Global(this.base);
		global.index = this.index;
		return global;
	}
	
	public static Global globalFrom(Ptr ptr) {
		Global global = new Global(ptr.copy());
		global.index = 0;
		return global;
	}
	
	public long get() {
		return ptr().readLong();
	}
	
	public Ptr ptr() {
		int a = (index >> 0x12 & 0x3F);
		int b = (index & 0x3FFFF);
		
		return base.copy()
			.add(a*8)
			.indirect64()
			.add(b*8);
	}

	public boolean set(Long value) {
		return ptr().writeLong(value);
	}
	
	public boolean setInt(int value) {
		return ptr().writeLong(Integer.toUnsignedLong(value));
	}
	
	public boolean setString(String str, int maxLength) {
		if ( str.length() > maxLength-1 ) {
			str = str.substring(0, maxLength-1);
		}
		return base.writeString(str);
	}
	
	@Override
	public String toString() {
		return String.format("Globals[%d] -> %s", index, ptr());
	}


	
}
