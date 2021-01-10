package jmodmenu.cayo_perico.model;

import java.util.LinkedList;
import java.util.List;

public class Paintings extends SecondaryCompundLoot {

	
	public Paintings(int idx) {
		super( LootType.PAINTINGS, idx );
	}
	
	@Override
	public float[] position() {
		int iParam0 = id;
		switch (iParam0)
		{
			case 0:
				return f(5081.14f, -5758.794f, 15.981f);
			
			case 1:
				return f(5027.032f, -5738.977f, 18.027f);
			
			case 2:
				return f(5008.773f, -5783.208f, 17.99f);
			
			case 3:
				return f(5014.587f, -5751.069f, 29.006f);
			
			case 4:
				return f(5004.83f, -5755.521f, 29.006f);
			
			case 5:
				return f(4995.553f, -5748.032f, 15.002f);
			
			case 6:
				return f(4997.112f, -5745.52f, 15.001f);
			
		}
		return f(0f, 0f, 0f);
	}
	
	public static List<Paintings> fromGlobal(int global) {
		List<Paintings> items = new LinkedList<>();
		MapItem.globalBitStream(7, global, 
			idx -> {
				Paintings item = new Paintings(idx);
				items.add(item);
			});
		return items;
	}

}
