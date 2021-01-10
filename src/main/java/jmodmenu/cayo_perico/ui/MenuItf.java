package jmodmenu.cayo_perico.ui;

public interface MenuItf {
	
	static Runnable NO_ACTION = () -> {};

	void show();
	void whenBack(Runnable backMenuAction);
	
}
