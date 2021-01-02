package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import jmodmenu.cayo_perico.model.LootType;
import jmodmenu.cayo_perico.model.MapItem;
import jmodmenu.cayo_perico.model.SecondaryLoot;
import jmodmenu.cayo_perico.service.LootDataProvider;

public class MenuScopeAddLoot extends MenuAbstract {
	
	Map<String, Integer> locations = new LinkedHashMap<>();
	{
		locations.put("airstrip",   0x0000003F);
		locations.put("north_dock", 0x00001FC0);
		locations.put("fields",     0x0001E000);
		locations.put("main_dock",  0x00FE0000);
		locations.put("compound",   0x000000FF);
	}
	
	Map<String, MapView> locationView = Map.of(
		"airstrip", MapView.AIRSTRIP,
		"north_dock", MapView.NORTH_DOCK,
		"fields", MapView.FIELDS,
		"main_dock", MapView.MAIN_DOCK,
		"compound", MapView.COMPOUND,
		"paintings", MapView.COMPOUND
	);
	
	MenuScopeAddLootCtx menuScopeAddLootCtx;
	MapView previousView;

	public MenuScopeAddLoot(MenuContext ctx, MenuScopeAddLootCtx menuScopeAddLootCtx ) {
		super(ctx);
		showSaveAction = false;
		this.menuScopeAddLootCtx = menuScopeAddLootCtx;
	}
	
	LootDataProvider lootDataProvider;
	String currentLocation;
	
	@Override
	protected void evaluateContext() {
		super.evaluateContext();
		lootDataProvider = menuScopeAddLootCtx.getLootDataProvider();
		currentLocation = menuScopeAddLootCtx.getCurrentLocation();
		
		if ( currentLocation == null ) {
			currentLocation = "airstrip";
			menuScopeAddLootCtx.setCurrentLocation(currentLocation);
		}
		if ( previousView == null ) previousView = context.currentView();
	}

	@Override
	void content(MenuManager menuManager) {
		boolean island = !"compound".equals(currentLocation);
		List<? extends SecondaryLoot> allLoots = island ? lootDataProvider.getSecondaryIslandLoot() : lootDataProvider.getSecondaryCompundLoot();
		
		int currentMask = locations.get(currentLocation);
		// System.out.format("current location[%s] mask[%08x]\n", currentLocation, currentMask);
		
		menuManager
		.addSpacer()
		.addLabel( txt("location."+currentLocation) )
		.addNavBar( navFunction(-1), navFunction(1) )
		.addChooserHeader();
		LootType[] types = {LootType.CASH, LootType.WEED, LootType.COCAINE, LootType.GOLD};
		int k = 0;
		
		for (int mask = 1, cpt = 0; mask != 0x80000000; mask = mask << 1, k++) {
			if ( (currentMask & mask) == 0 ) continue;
			cpt++;
			menuManager.addLootChooser(cpt, mask, allLoots, types, 
				changeLootCallbackFor(k, island)
			);
		}
		refreshViewCustomLoots();
	}
	
	@Override
	protected void back() {
		if ( previousView != null ) {
			context.setView(previousView);
			previousView = null;
		}
		super.back();
	}
	
	private BiConsumer<LootType, Boolean> changeLootCallbackFor(int idx, boolean island) {
		return (type, b) -> {
			// System.out.format("(CB) Change %d on %s(%s) to %s:%s\n", aIdx.get(), currentLocation, island, type.name(), b);
			lootDataProvider.unsetLootAtPosition(idx, island);
			if (b) lootDataProvider.addLoot(type, idx, island);
			refreshViewCustomLoots();
		};
	}
	
	private Runnable navFunction(int increment) {
		List<String> locationKeys = new ArrayList<>();
		locations.forEach((txt, m) -> locationKeys.add(txt));
		return () -> {
			int i = locationKeys.indexOf(currentLocation) + increment;
			if (i < 0 || i >= locationKeys.size()) { 
				showMenuCustomPaints(); 
				return; 
			}
			currentLocation = locationKeys.get(i);
			menuScopeAddLootCtx.setCurrentLocation(currentLocation);
			show();
		};
	}
	
	private void showMenuCustomPaints() {
		showContentFn(this::menuCustomPaints);
	}
	
	private void menuCustomPaints(MenuManager menuManager) {
		menuManager
		.addSpacer()
		.addLabel( txt("loots.paintings") )
		.addNavBar(
			() -> {
				currentLocation = "compound";
				menuScopeAddLootCtx.setCurrentLocation(currentLocation);
				show();
			}, 
			() -> {
				currentLocation = "airstrip";
				menuScopeAddLootCtx.setCurrentLocation(currentLocation);
				show();
			}
		);
		
		Function<Integer, Boolean> hasPaintAt = (idx) -> lootDataProvider.getSecondaryCompundLoot()
				.stream()
				.filter( loot -> loot.getIdx() == idx )
				.filter( loot -> loot.getType() == LootType.PAINTINGS )
				.findAny()
				.isPresent();
		for (int j = 0; j < 7; j++) {			
			String place = txt("location.paint_"+j);
			menuManager.addPaintLocation((j+1), place, hasPaintAt.apply(j), (idx, b) -> {});
		}
		context.setView(MapView.COMPOUND);
	}
	
	private void refreshViewCustomLoots() {
		List<MapItem> items = new LinkedList<>( /* service.getEquipment(playerIndex) */ );
		items.addAll( lootDataProvider.getSecondaryIslandLoot() );
		items.addAll( lootDataProvider.getSecondaryCompundLoot() );
		context.setMapItems(items);
		context.setView(locationView.get(currentLocation));
	}
	
}

interface MenuScopeAddLootCtx {
	LootDataProvider getLootDataProvider();
	String getCurrentLocation();
	void setCurrentLocation(String location);
}