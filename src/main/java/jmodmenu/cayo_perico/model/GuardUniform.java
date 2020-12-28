package jmodmenu.cayo_perico.model;

import java.util.LinkedList;
import java.util.List;


public class GuardUniform implements MapItem {
	
	public final static String NAME = "GUARD_UNIFORM";
	
	int id;
	
	public GuardUniform(int id) {
		this.id = id;
	}
	
	@Override
	public String name() {
		return NAME;
	}
	
	/**
	 * @see func_883
	 */
	@Override
	public float[] position() {
		int iParam0 = id;
		switch (iParam0)
		{
			case 0:
				return f(5163.384f, -4995.56f, 11.682f);
			
			case 1:
				return f(4765.362f, -4778.338f, 2.781f);
			
			case 2:
				return f(4502.198f, -4523.357f, 3.396f);
			
			case 3:
				return f(4528.522f, -4536.311f, 6.558f);
			
			case 4:
				return f(5098.933f, -4609.03f, 1.369f);
			
			case 5:
				return f(5060.167f, -4589.7f, 1.9f);
			
			case 6:
				return f(5148.113f, -4616.131f, 1.387f);
			
			case 7:
				return f(5170.703f, -4675.337f, 1.439f);
			
			case 8:
				return f(5116.664f, -5130.588f, 1.143f);
			
			case 9:
				return f(5002.533f, -5125.336f, 1.955f);
			
			case 10:
				return f(4949.935f, -5321.81f, 7.085f);
			
			case 11:
				return f(5012.355f, -5203.456f, 1.516f);
			
			case 12:
				return f(4923.873f, -5273.464f, 4.65f);
			
			case 13:
				return f(5263.995f, -5435.548f, 64.881f);
			
			case 14:
				return f(4886.995f, -5454.333f, 29.731f);
			
			case 15:
				return f(5106.183f, -5524.682f, 53.239f);
			
		}
		return f(0f, 0f, 0f);
	}

	public static List<GuardUniform> fromGlobal(int g) {
		List<GuardUniform> items = new LinkedList<>();
		MapItem.globalBitStream(16, g, 
			idx -> {
				GuardUniform item = new GuardUniform(idx);
				items.add(item);
			});
		return items;
	}
}
