package jmodmenu.core;

import jmodmenu.memory.Ptr;

public class Local {
	
	Ptr ptr;
	int index;
	
	public Local(Ptr ptr) {
		this.ptr = ptr;
		index = 0;
	}
	
	public Local at(int idx) {
		index += idx;
		return this;
	}
	
	public Local at(int idx, int size) {
		index += idx * size + 1;
		return this;
	}
	
	public Ptr ptr() {
		return ptr.copy().add(index*8);
	}
	
	public int getInt() {
		// System.out.format( "Get local %d from pointer %s -> %d\n", index, ptr, ptr.copy().add(index*8).readInt());
		return ptr().readInt();
	}

	public void setInt(int i) {
		ptr().writeInt(i);
	}
	
	public Local copy() {
		Local copy = new Local(ptr);
		copy.index = index;
		return copy;
	}


}
