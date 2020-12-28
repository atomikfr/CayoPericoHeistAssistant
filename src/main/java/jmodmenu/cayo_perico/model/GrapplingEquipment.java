package jmodmenu.cayo_perico.model;

import java.util.LinkedList;
import java.util.List;

// Global_1706028[iParam0 /*53*/].f_5.f_3
public class GrapplingEquipment implements MapItem {
	
	public final static String NAME = "GRAPPLING_EQUIPMENT";
	
	int id;
	
	public GrapplingEquipment(int idx) {
		this.id = idx;
	}
	
	@Override
	public String name() {
		return NAME;
	}
	
	/**
	 * @see func_889
	 */
	@Override
	public float[] position() {
		int iParam0 = id;
		switch (iParam0)
		{
			case 0:
				return f(4896.678f, -4791.297f, 2.59f);
			
			case 1:
				return f(4529.568f, -4703.347f, 3.134f);
			
			case 2:
				return f(4493.661f, -4733.618f, 10.01f);
			
			case 3:
				return f(3895.499f, -4695.022f, 5.547f);
			
			case 4:
				return f(5103.035f, -4681.288f, 7.702f);
			
			case 5:
				return f(5130.433f, -4610.441f, 11.724f);
			
			case 6:
				return f(5109.232f, -4578.338f, 28.711f);
			
			case 7:
				return f(4879.964f, -4487.782f, 9.922f);
			
			case 8:
				return f(4901.395f, -5331.422f, 28.64f);
			
			case 9:
				return f(4862.909f, -5158.419f, 2.283f);
			
			case 10:
				return f(5124.89f, -5097.902f, 2.192f);
			
			case 11:
				return f(4867.519f, -4642.129f, 13.571f);
			
			case 12:
				return f(5568.438f, -5185.942f, 10.22f);
			
			case 13:
				return f(5406.297f, -5170.91f, 31.198f);
			
			case 14:
				return f(5265.993f, -5430.593f, 140.566f);
			
			case 15:
				return f(5611.286f, -5654.516f, 9.051f);
		}
		return f(0f, 0f, 0f);
	}
	
	public static List<GrapplingEquipment> fromGlobal(int g) {
		List<GrapplingEquipment> items = new LinkedList<>();
		MapItem.globalBitStream(16, g, 
			idx -> {
				GrapplingEquipment item = new GrapplingEquipment(idx);
				items.add(item);
			});
		return items;
	}
	

}
