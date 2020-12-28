package jmodmenu;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

import jmodmenu.cayo_perico.model.LootType;
import jmodmenu.cayo_perico.model.MapItem;
import jmodmenu.cayo_perico.model.SecondaryCompundLoot;

public class Utils {
	
	public static int joaat(String str) {
		int hash = 0;
		int length = str.length();
		for (int i = 0; i < length; i++)
		{
		    hash += joaat_to_lower( str.charAt(i) );
		    hash += (hash << 10) & 0xFFFFFC00;
		    hash ^= (hash >> 6) & 0x03FFFFFF;
		}
		hash += (hash << 3) & 0xFFFFFFFC;
		hash ^= (hash >> 11) & 0x001FFFFF;
		hash += (hash << 15) & 0xFFFF8000;
		return hash;
	}
	
	private static int joaat_to_lower(char c) {
		return (c >= 'A' && c <= 'Z') ? c + ('a' - 'A') : c;
	}
	


}
