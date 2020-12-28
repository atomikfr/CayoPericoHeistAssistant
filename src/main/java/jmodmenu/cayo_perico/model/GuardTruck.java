package jmodmenu.cayo_perico.model;

// Global_1706028[iParam0 /*53*/].f_5.f_34
public class GuardTruck implements MapItem {
	
	public final static String NAME = "GUARD_TRUCK";
	
	int id;
	
	public GuardTruck(int id) {
		this.id = id;
	}
	
	@Override
	public String name() {
		return NAME;
	}
	
	@Override
	public float[] position() {
		int iParam0 = id;
		switch (iParam0)
		{
			case 1:
				return f(4527.591f, -4526.633f, 3.211f);
			
			case 2:
				return f(5152.974f, -4619.487f, 1.752f);
			
			case 3:
				return f(5155.974f, -5132.699f, 1.312f);
			
			case 4:
				return f(4900.456f, -5210.057f, 1.512f);
			
			case 5:
				return f(4970.79f, -5695.73f, 18.888f);
			
			default:
		}
		return f(0f, 0f, 0f);
	}
	
	public static GuardTruck fromGlobal(int value) {
		return new GuardTruck(value);
	}

}
