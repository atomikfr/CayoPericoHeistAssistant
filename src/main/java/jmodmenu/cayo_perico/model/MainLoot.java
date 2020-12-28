package jmodmenu.cayo_perico.model;

public enum MainLoot {
	
	TEQUILA("tequila", 900000),
	NECKLACE("necklace", 1000000),
	BONDS("bonds", 1100000),
	DIAMOND("diamond", 1400000),
	FILES("files", 1100000),
	STATUT("statue", 1900000);
	
	
	String text;
	int value;
	
	MainLoot( String text, int value ) {
		this.text = text;
		this.value = value;
	}
	
	public String text() { return text; }
	public int value() { return value; }

}
