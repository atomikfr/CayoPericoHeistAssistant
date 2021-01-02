package jmodmenu.cayo_perico.model;

import java.util.List;
import java.util.stream.Collectors;

// func_899
// sur la map
// Global_1706028[iParam0 /*53*/].f_5.f_10.f_9, iParam1);
// Global_1706028[iParam0 /*53*/].f_5.f_10.f_10, iParam1);
// Global_1706028[iParam0 /*53*/].f_5.f_10.f_11, iParam1);
// Global_1706028[iParam0 /*53*/].f_5.f_10.f_12, iParam1);
// scoped
// func_900
//Global_1706028[iParam0 /*53*/].f_5.f_10.f_13, iParam1);
//Global_1706028[iParam0 /*53*/].f_5.f_10.f_14, iParam1);
//Global_1706028[iParam0 /*53*/].f_5.f_10.f_15, iParam1);
//Global_1706028[iParam0 /*53*/].f_5.f_10.f_16, iParam1);
public class SecondaryCompundLoot extends SecondaryLoot {
	
	public SecondaryCompundLoot(LootType type, int idx) {
		super(type, idx);
	}
	
	/**
	 * @see func_898
	 */
	@Override
	public float[] position() {
		int iParam0 = idx;
		switch (iParam0)
		{
			case 0:
				return f(5081.753f, -5754.63f, 15.764f);
			
			case 1:
				return f(5082.945f, -5758.447f, 15.765f); // SE
			
			case 2:
				return f(5029.346f, -5733.933f, 17.81f); // North
			
			case 3:
				return f(5029.777f, -5737.731f, 17.808f); 
			
			case 4:
				return f(5012.031f, -5788.325f, 17.773f);
			
			case 5:
				return f(5006.233f, -5785.789f, 17.771f);
			
			case 6:
				return f(4998.106f, -5752.379f, 14.783f);
			
			case 7:
				return f(5003.947f, -5748.77f, 14.787f);
		}
		return f(0f, 0f, 0f);
	}
	
	public static List<SecondaryCompundLoot> fromGlobal(LootType type, int global) {
		return MapItem.bitStream(8, global)
			.mapToObj(idx -> new SecondaryCompundLoot(type, idx))
			.collect(Collectors.toList());
	}
	
/*	
	public static List<MapItem> fromGlobals(int[] globals) {
		if ( globals.length < 4 ) {
			throw new InvalidParameterException("You should pass 4 globals. received : " + globals.length);
		}
		
		BiFunction<LootType, Integer, List<? extends SecondaryCompundLoot>> creator
			= SecondaryCompundLoot::fromGlobal;
		
		List<MapItem> items = new LinkedList<>();
		for (int i = 0; i < 4; i++) {
			LootType type = LootType.values()[i];
			System.out.println( type.name()+": " + globals[i]);
			MapItem.bit(8, globals[i], 
				idx -> {
					SecondaryCompundLoot item = new SecondaryCompundLoot(idx, type);
					items.add(item);
				});
		}
		
		return items;
	}*/

}