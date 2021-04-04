package jmodmenu.cayo_perico.model;

public enum MainLoot {
	
	// value is only usefull in "simulated mode"
	// otherwise it takes value from a global.
	TEQUILA("tequila", 900000),
	NECKLACE("necklace", 1000000),
	BONDS("bonds", 1100000),
	DIAMOND("diamond", 1300000),
	FILES("files", 1100000),
	STATUE("statue", 1900000);
	
	String text;
	int value;
	
	MainLoot( String text, int value ) {
		this.text = text;
		this.value = value;
	}
	
	public String text() { return text; }
	public int value() { return value; }

}
