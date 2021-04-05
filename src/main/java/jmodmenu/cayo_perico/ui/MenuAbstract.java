package jmodmenu.cayo_perico.ui;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import jmodmenu.cayo_perico.model.MapItem;
import jmodmenu.cayo_perico.service.CayoPericoMapService;
import jmodmenu.core.PlayerInfo;

public abstract class MenuAbstract implements MenuItf {
	
	protected MenuContext context;
	protected Runnable backMenuAction;
	protected boolean showSaveAction = true;
	
	protected CayoPericoMapService service;
	protected boolean isLocalPlayerSelected;
	protected int playerIndex;
	
	public MenuAbstract(MenuContext context) {
		this.context = context;
	}
	
	public void whenBack(Runnable backMenuAction) {
		this.backMenuAction = backMenuAction;
	}
	
	@Override
	public void show() {
		showContentFn(this::content);
	}
	
	protected void showContentFn(Consumer<MenuManager> contentFn) {
		evaluateContext();
		MenuManager menuManager = context.menuManager();
		menuManager.clear()
		.backTo(this::back);
		contentFn.accept(menuManager);
		if ( isLocalPlayerSelected && showSaveAction ) {
			menuManager.addSave( this::save );
		}
		context.repaint();
	}
	
	abstract void content(MenuManager menuManager);
	
	protected void evaluateContext() {
		playerIndex = context.selectedPlayer().getIndex();
		isLocalPlayerSelected =  playerIndex == context.service().getLocalPlayerIndex();
		service = context.service();
	}
	
	protected void back() {
		if ( backMenuAction != null ) backMenuAction.run();
	}

	protected void save() {}
	
}

interface MenuContext {
	CayoPericoMapService service();
	void repaint();
	MenuManager menuManager();
	PlayerInfo selectedPlayer();
	void refreshData();
	MapView currentView();
	void setView(MapView mapView);
	void setMapItems(List<MapItem> items);
}
