package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jmodmenu.cayo_perico.model.MainLoot;
import lombok.Setter;

public class MenuScopeMainLoot extends MenuAbstract {

	@Setter
	int selectedLoot;
	
	public MenuScopeMainLoot(MenuContext ctx) {
		super(ctx);
		// showSaveAction = false;
	}
	
	@Override
	void content(MenuManager menuManager) {
		MainLoot loot = service.getMainLoot(playerIndex);
		List <String> itemsConf = Stream.of( MainLoot.values() )
			.map( l -> txt("loots."+l.name().toLowerCase() ) )
			.collect( Collectors.toList() );
		menuManager
		.checkIndexItems(itemsConf, loot.ordinal(), this::setSelectedLoot );
	}
	
	@Override
	protected void save() {
		service.setMainLoot( MainLoot.values()[selectedLoot] );
		context.refreshData();
	}
}
