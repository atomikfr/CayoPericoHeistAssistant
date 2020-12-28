package jmodmenu.core;

import com.sun.jna.Memory;

import jmodmenu.memory.Ptr;

public class Script {
	
	public static int NAME_OFFSET = 0xD0;
	
	String name;
	Local locals;
	
	public static Script fromPointer(Ptr ptr) {
		Script script = new Script();
		
		Memory mem = ptr.getMemory(360);
		script.name = mem.getString(NAME_OFFSET, "UTF-8");
		Ptr localsPointer = ptr.copy().add(0xB0);
		// System.out.println("locals pointer: " + localsPointer);
		script.locals = new Local( localsPointer.indirect64() );
		
		return script;
	}
	
	public Local locals() {
		return locals.copy();
	}

}
