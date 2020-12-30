package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.util.LinkedHashMap;
import java.util.Map;

import jmodmenu.cayo_perico.model.MainLoot;

public class MenuTools extends MenuMissions {

	public MenuTools(MenuContext ctx) {
		super(ctx);
	}
	
	@Override
	protected int mask() {
		return TOOLS_MASK;
	}
	
	@Override
	void content(MenuManager menuManager) {
		if ( context.selectedPlayer() == null ) return;

		// int Pilot = 0x80;
		String lootAccessMaterial = service.getMainLoot(playerIndex) == MainLoot.BONDS 
				? txt("tools.safe_code") : txt("tools.plasma_cutter");
		Map <String, Integer> itemsConf = new LinkedHashMap<>();
		itemsConf.put(txt("tools.demolition_charges"),  EXPL_CHARGES);
		itemsConf.put(txt("tools.acetylene_torch"), TORCH);
		itemsConf.put(lootAccessMaterial, SAFE_CODE); // same as PLASMA_CUTTER
		itemsConf.put(txt("tools.fingerprint"), FINGERPRINT);
		menuManager
		.addSpacer()
		.checkMaskItems(itemsConf, MP0_H4_MISSIONS, this::maskApproachIfSelected );
	}
	

}
