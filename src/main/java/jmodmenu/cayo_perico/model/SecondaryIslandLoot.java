package jmodmenu.cayo_perico.model;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

// func_906
// Global_1706028[iParam0 /*53*/].f_5.f_10.f_1
// Global_1706028[iParam0 /*53*/].f_5.f_10.f_2
// Global_1706028[iParam0 /*53*/].f_5.f_10.f_3 
// Global_1706028[iParam0 /*53*/].f_5.f_10.f_4 
public class SecondaryIslandLoot extends SecondaryLoot {
	
	public SecondaryIslandLoot(LootType type, int idx) {
		super(type, idx);
	}
	
	@Override
	public float[] position() {
		int iParam0 = id;
		switch (iParam0)
		{
			case 0:
				return f(4447.822f, -4442.135f, 7.175f); // Airstrip
			
			case 1:
				return f(4448.821f, -4444.858f, 7.182f); // Airstrip
			
			case 2:
				return f(4435.688f, -4446.595f, 4.25f); // Airstrip
			
			case 3:
				return f(4438.332f, -4445.811f, 4.267f); // Airstrip
			
			case 4:
				return f(4506.064f, -4555.47f, 4.095f); // Airstrip
			
			case 5:
				return f(4503.678f, -4556.323f, 4.096f); // Airstrip
			
			case 6:
				return f(5064.635f, -4589.759f, 2.801f); // North Dock
			
			case 7:
				return f(5067.557f, -4590.849f, 2.795f); // North Dock
			
			case 8:
				return f(5092.602f, -4680.137f, 2.35f); // North Dock
			
			case 9:
				return f(5093.195f, -4683.354f, 2.35f); // North Dock
			
			case 10:
				return f(5091.02f, -4685.678f, 2.351f); // North Dock
			
			case 11:
				return f(5136.102f, -4613.863f, 2.401f); // North Dock
			
			case 12:
				return f(5131.834f, -4612.648f, 2.404f); // North Dock
			
			case 13:
				return f(5329.472f, -5272.372f, 33.13f); // fields
			
			case 14:
				return f(5328.096f, -5270.587f, 33.129f); // fields
			
			case 15:
				return f(5196.664f, -5133.933f, 3.284f); // fields
			
			case 16:
				return f(5196.121f, -5136.333f, 3.285f); // fields
			
			case 17:
				return f(5000.313f, -5163.344f, 2.697f); // Main Dock
			
			case 18:
				return f(5001.3f, -5165.434f, 2.697f); // Main Dock
			
			case 19:
				return f(4959.845f, -5107.064f, 2.911f); // Main Dock
			
			case 20:
				return f(4962.675f, -5106.771f, 2.913f); // Main Dock
			
			case 21:
				return f(4963.856f, -5109.32f, 2.912f); // Main Dock
			
			case 22:
				return f(4926.316f, -5244.514f, 2.461f); // Main Dock
			
			case 23:
				return f(4924.385f, -5245.882f, 2.461f); // Main Dock
		}
		return f(0f, 0f, 0f);
	}
	
	public static List<SecondaryIslandLoot> fromGlobal(LootType type, int global) {
		return MapItem.bitStream(24, global)
			.mapToObj(idx -> new SecondaryIslandLoot(type, idx))
			.collect(Collectors.toList());
	}
	
	public static List<MapItem> fromGlobals(int[] globals, BiFunction<LootType, Integer, List<MapItem>> fromGlobalFn, BiConsumer<LootType, Integer> foundByTypeCallback) {
		if ( globals.length < 4 ) {
			throw new InvalidParameterException("You should pass 4 globals. received : " + globals.length);
		}
		
		List<MapItem> items = new LinkedList<>();
		for (int i = 0; i < 4; i++) {
			LootType type = LootType.values()[i];
			List<MapItem> list = fromGlobalFn.apply(type, globals[i]);
			if (foundByTypeCallback != null) foundByTypeCallback.accept(type, list.size());
			items.addAll(list);
		}
		return items;
	}

}