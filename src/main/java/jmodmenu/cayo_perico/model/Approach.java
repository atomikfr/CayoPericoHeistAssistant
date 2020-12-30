package jmodmenu.cayo_perico.model;

import java.util.List;

public enum Approach {
	
	SUBMARINE,
	BOMBER,
	PLANE,
	HELICOPTER,
	PATROL_BOAT,
	SMUGGLER_BOAT;
	
	public static int mask( List<Approach> approaches ) {
		int res = 0;
		
		if ( approaches.contains(SUBMARINE) )     res |= 0x1;
		if ( approaches.contains(BOMBER) )        res |= 0x2;
		if ( approaches.contains(PLANE) )         res |= 0x4;
		if ( approaches.contains(HELICOPTER) )    res |= 0x8;
		if ( approaches.contains(PATROL_BOAT) )   res |= 0x10;
		if ( approaches.contains(SMUGGLER_BOAT) ) res |= 0x20;
		if ( approaches.contains(SUBMARINE) )     res |= 0x40;
		
		return res;
	}

}
