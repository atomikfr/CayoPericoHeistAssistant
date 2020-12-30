package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.util.LinkedHashMap;
import java.util.Map;

public class MenuDisturb extends MenuMissions {

	public MenuDisturb(MenuContext ctx) {
		super(ctx);
	}

	@Override
	protected int mask() {
		return DISRUPTION_MASK;
	}
	
	@Override
	void content(MenuManager menuManager) {
		Map <String, Integer> itemsConf = new LinkedHashMap<>();
		itemsConf.put(txt("disturb.weapons"),  DIS_WEAPONS);
		itemsConf.put(txt("disturb.armor"), DIS_ARMOR);
		itemsConf.put(txt("disturb.support"), DIS_SUPPORT);
		menuManager
		.addSpacer()
		.checkMaskItems(itemsConf, MP0_H4_MISSIONS, this::maskApproachIfSelected );
	}
	
	
}
