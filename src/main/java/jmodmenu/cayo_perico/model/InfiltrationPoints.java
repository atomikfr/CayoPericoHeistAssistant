package jmodmenu.cayo_perico.model;

public class InfiltrationPoints implements MapItem {
	
	int id;
	
	/**
	 * @see func_563
	 */
	public String name() {
		int iParam0 = getStringIndex();
		switch (iParam0)
		{
			case 12:
				return "AIRSTRIP";
			
			case 13:
				return "PARACHUTING";
			
			case 14:
				return "WEST_BEACH";
			
			case 15:
				return "MAIN_DOCK";
			
			case 16:
				return "NORTH_DOCK";
			
			case 17:
				return "NORTH_DROP_ZONE";
			
			case 18:
				return "SOUTH_DROP_ZONE";
			
			case 19:
				return "DRAINAGE_TUNNEL";
		}
		return "";
	}
	
	/**
	 * @see func_876
	 */
	public int getStringIndex() {
		int iParam0 = id;
		switch (iParam0)
		{
			case 0:
				return 12;
			
			case 1:
				return 13;
			
			case 2:
				return 14;
			
			case 3:
				return 15;
			
			case 4:
				return 16;
			
			case 5:
				return 17;
			
			case 6:
				return 18;
			
			case 7:
				return 19;
		}
		return -1;
	}
	
	/**
	 * @see func_874
	 */
	public float[] position() {
		int iParam0 = id;
		switch (iParam0)
		{
			case 0:
				return f(4052.4f, -4655.79f, 3.18f);
			
			case 3:
				return f(4799.21f, -5157.77f, -4.34f);
			
			case 4:
				return f(5176.88f, -4752.2f, -4.11f);
			
			case 2:
				return f(4891.13f, -4925.02f, 9.31f);
			
			case 5:
				return f(4820.73f, -4302.71f, 4.24f);
			
			case 6:
				return f(5478.63f, -5847.74f, 19.54f);
			
			case 7:
				return f(5045.17f, -5817.24f, -12.7f);
			
			default:
		}
		return f(0f, 0f, 0f);
	}
	
	
	
	

}
