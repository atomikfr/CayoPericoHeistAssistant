package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.util.LinkedHashMap;
import java.util.Map;

public class MenuApproach extends MenuMissions {
	
	public MenuApproach(MenuContext ctx) {
		super(ctx);
	}
	
	@Override
	protected int mask() {
		return APPROACH_MASK;
	}
	
	@Override
	void content(MenuManager menuManager) {
		if ( context.selectedPlayer() == null ) return;

		int Pilot = 0x80;
		Map <String, Integer> itemsConf = new LinkedHashMap<>();
		itemsConf.put(txt("vehicles.submarine"), SUBMARINE);
		itemsConf.put(txt("vehicles.bomber"), BOMBER | PILOT);
		itemsConf.put(txt("vehicles.plane"), PLANE);
		itemsConf.put(txt("vehicles.copter"), HELICOPTER | Pilot);
		itemsConf.put(txt("vehicles.patrol_boat"), PATROL_BOAT);
		itemsConf.put(txt("vehicles.smuggler_boat"), SMUGGLER_BOAT);
		menuManager
		.addSpacer()
		.checkMaskItems(itemsConf, MP0_H4_MISSIONS, this::maskApproachIfSelected );
	}

	

}
