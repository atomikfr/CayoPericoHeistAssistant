package jmodmenu.cayo_perico.model;

import java.util.LinkedList;
import java.util.List;

public class BoltCutters implements MapItem {
	
	public final static String NAME = "BOLT_CUTTERS";
	
	int id;
	
	public BoltCutters() {
	}
	
	public BoltCutters(int id) {
		this.id = id;
	}
	
	public String name() {
		return NAME;
	}
	
	/**
	 * @see func_877
	 */
	@Override
	public float[] position() {
		int iParam0 = id;
		switch (iParam0)
		{
			case 0:
				return f(4534.709f, -4543.447f, 4.53f);
			
			case 1:
				return f(4523.664f, -4511.79f, 4.184f);
			
			case 2:
				return f(4076.849f, -4667.305f, 4.163f);
			
			case 3:
				return f(4505.6f, -4653.089f, 10.456f);
			
			case 4:
				return f(4805.025f, -4315.458f, 6.514f);
			
			case 5:
				return f(5180.197f, -4670.979f, 6.231f);
			
			case 6:
				return f(5098.02f, -4621.306f, 2.594f);
			
			case 7:
				return f(5070.327f, -4638.299f, 2.902f);
			
			case 8:
				return f(4955.743f, -5181.93f, 4.512f);
			
			case 9:
				return f(5216.539f, -5126.448f, 5.984f);
			
			case 10:
				return f(4879.029f, -5112.622f, 1.995f);
			
			case 11:
				return f(4901.985f, -5348.011f, 9.409f);
			
			case 12:
				return f(5362.94f, -5437.508f, 48.491f);
			
			case 13:
				return f(5466.287f, -5232.051f, 27.065f);
			
			case 14:
				return f(4754.99f, -5541.311f, 18.056f);
			
			case 15:
				return f(5326.267f, -5266.071f, 32.237f);
			
		}
		return f(0f, 0f, 0f);
	}
	
	
	public static List<BoltCutters> fromGlobal(int g) {
		List<BoltCutters> items = new LinkedList<>();
		MapItem.globalBitStream(16, g, 
			idx -> {
				BoltCutters item = new BoltCutters(idx);
				items.add(item);
			});
		return items;
	}
	

}
