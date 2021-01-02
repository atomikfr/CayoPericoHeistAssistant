package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jmodmenu.cayo_perico.model.LootType;
import jmodmenu.cayo_perico.model.MainLoot;

public class MenuHeist extends MenuAbstract {

	public MenuHeist(MenuContext context) {
		super(context);
	}
	
	List<Field> fields;
	MainLoot loot;
	
	@Override
	protected void evaluateContext() {
		super.evaluateContext();
		loot = service.getMainLoot(playerIndex);
	}
	
	@Override
	void content(MenuManager menuManager) {		
		fields = new LinkedList<>();
		int lootValue = service.getCurrentLootValue(playerIndex, loot);
		int bagSize = service.getMyBagSize();
		Map <String, Object> itemsConf = new LinkedHashMap<>();
		itemsConf.put(txt("loots."+loot.text()),  lootValue);
		for(LootType type : LootType.values()) {
			int value = service.getStackLootValue(playerIndex, type);
			itemsConf.put(txt("loots."+type.name().toLowerCase()), value);
		}
		itemsConf.put(txt("heist.bagsize"), bagSize);
		menuManager.addFields(itemsConf, fields);
	}
	
	@Override
	protected void save() {
		for(Field field : fields) {
			if ( !field.hasChanged() ) continue;
			if ( field.id == 0 ) service.setMainLootValue(loot, field.intValue());
			else if ( field.id == 6) service.setBagSize(field.intValue());
			else service.setStackLootValue(LootType.values()[field.id-1], field.intValue());
		}
	}

	
}
