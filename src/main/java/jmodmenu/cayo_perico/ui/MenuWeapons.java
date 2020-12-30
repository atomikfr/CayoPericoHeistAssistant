package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.util.List;
import java.util.Map;

import lombok.Setter;

public class MenuWeapons extends MenuAbstract {

	@Setter
	int weaponIndex;
	
	@Setter
	Boolean suppressors;
	
	public MenuWeapons(MenuContext ctx) {
		super(ctx);
	}
	
	@Override
	void content(MenuManager menuManager) {
		int MP0_H4_MISSIONS = service.getApproach(playerIndex);
		int MP0_H4CNF_WEAPONS = service.getWeapon(playerIndex);
		List <String> itemsConf = List.of(
			"---",
			txt("weapons.shotgun"),
			txt("weapons.bullpup_rifle"),
			txt("weapons.sniper"),
			txt("weapons.smg"),
			txt("weapons.assault_riffle")
		);
		Map <String, Integer> suppressorConf = Map.of(
			txt("weapons.suppressors"),  0x1000
		);
		menuManager
		.checkIndexItems(itemsConf, MP0_H4CNF_WEAPONS, this::setWeaponIndex )
		.checkMaskItems(suppressorConf, MP0_H4_MISSIONS, i -> this::setSuppressors );

	}
	
	@Override
	protected void save() {
		service.setWeapon(weaponIndex);
		int value = weaponIndex == 0 || suppressors ? 0x1000 : 0;
		service.addApproach(value, 0x1000);
	}
	
}
