package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import jmodmenu.cayo_perico.model.LootType;
import jmodmenu.cayo_perico.model.MapItem;
import jmodmenu.cayo_perico.model.SecondaryLoot;
import jmodmenu.cayo_perico.service.LootDataProvider;
import lombok.Getter;
import lombok.Setter;

public class MenuScopeOut extends MenuAbstract implements MenuScopeAddLootCtx {
	
	private final static int 
		CASH_MASK = 0x01,
		WEED_MASK = 0x02,
		COKE_MASK = 0x04,
		GOLD_MASK = 0x08,
		PAINT_MASK = 0x10;

	int requestScope = 0;
	@Getter
	LootDataProvider lootDataProvider;
	@Getter @Setter
	String currentLocation;
	
	MenuScopeMainLoot menuScopeMainLoot;
	MenuScopeAddLoot menuScopeAddLoot;
	
	public MenuScopeOut(MenuContext ctx) {
		super(ctx);
		menuScopeMainLoot = new MenuScopeMainLoot(ctx);
		menuScopeMainLoot.whenBack(this::show);
		menuScopeAddLoot = new MenuScopeAddLoot(ctx, this);
		menuScopeAddLoot.whenBack(this::show);
	}
	
	@Override
	protected void back() {
		lootDataProvider = null;
		currentLocation = null;
		context.refreshData();
		super.back();
	}
	
	@Override
	protected void save() {
		lootDataProvider.saveChanges();
	}
	
	@Override
	void content(MenuManager menuManager) {
		if ( context.selectedPlayer() == null ) { return; }
		
		// int playerIndex = selectedPlayer.getIndex();
		if ( lootDataProvider == null ) {
			lootDataProvider = new LootDataProvider(context.service(), playerIndex);
			lootDataProvider.reload();
		}
		
		List<SecondaryLoot> loots = lootDataProvider.allSecondaryLoots();
		Map<LootType, Long> counts = SecondaryLoot.countByType(loots);
		
		Function<LootType, String> getLabel = 
			(type) -> {
				long count = Optional.ofNullable(counts.get(type)).orElse(0L);
				String label = txt("loots."+type.name().toLowerCase());
				return String.format("[%d] %s", count, label);
			};
		
		Map <String, Integer> itemsConf = new LinkedHashMap<>();
		itemsConf.put(getLabel.apply(LootType.CASH), CASH_MASK);
		itemsConf.put(getLabel.apply(LootType.WEED), WEED_MASK);
		itemsConf.put(getLabel.apply(LootType.COCAINE), COKE_MASK);
		itemsConf.put(getLabel.apply(LootType.GOLD), GOLD_MASK);
		itemsConf.put(getLabel.apply(LootType.PAINTINGS), PAINT_MASK);
		
		int scopedMask = 1;
		for( LootType type : LootType.values() ) {
			if ( lootDataProvider.scoped(type) || lootDataProvider.hasRequestedScope(type) ) requestScope |= scopedMask;
			// if ( context.service().hasScopedLoot(playerIndex, type) ) requestScope |= scopedMask;
			scopedMask = scopedMask << 1;
		}
		menuManager
		.addSubMenu(txt("menu.main_loot"), menuScopeMainLoot::show)
		.addSpacer()
		.addLabel( txt("menu.additional_loot") )
		.checkMaskItems(itemsConf, requestScope, this::maskingIfSelectedScope);
		
		if ( isLocalPlayerSelected ) {
			menuManager
			.addSubMenu( txt("menu.customize"), menuScopeAddLoot::show );
		}
	}
	private Consumer<Boolean> maskingIfSelectedScope(int mask) {
		return b -> {
			LootType type;
			if ( mask == CASH_MASK ) type = LootType.CASH;
			else if ( mask == WEED_MASK ) type = LootType.WEED;
			else if ( mask == COKE_MASK ) type = LootType.COCAINE;
			else if ( mask == GOLD_MASK ) type = LootType.GOLD;
			else type = LootType.PAINTINGS;
			lootDataProvider.requestScoping(type, b);
			if (b) {
				requestScope |= mask;
			} else {
				requestScope &= ~mask;
			}
		};
	}
	
}
