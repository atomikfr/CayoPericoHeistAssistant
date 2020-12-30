package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import jmodmenu.cayo_perico.model.H4CNF_BS_GEN;

public class MenuEquipment extends MenuAbstract implements H4CNF_BS_GEN {
	
	public MenuEquipment(MenuContext ctx) {
		super(ctx);
	}

	int equipmentMask;
	
	@Override
	void content(MenuManager menuManager) {
		if ( context.selectedPlayer() == null ) return;
		
		int MPx_H4CNF_BS_GEN = service.getScopedEquipment(playerIndex);
		equipmentMask = MPx_H4CNF_BS_GEN & 0x8FFF;
		Map <String, Integer> itemsConf = new LinkedHashMap<>();
		itemsConf.put(txt("equipment.grappling_equipment"), GRAPPLING_ALL);
		itemsConf.put(txt("equipment.guard_uniform"), UNIFORM_ALL);
		itemsConf.put(txt("equipment.bolt_cutters"), BOLT_CUTTER_ALL);
		itemsConf.put(txt("equipment.guard_truck"), SUPPLY_TRUCK);
		menuManager
		.addSpacer()
		.checkMaskItems(itemsConf, MPx_H4CNF_BS_GEN, this::maskingIfSelected );
	}
	
	private Consumer<Boolean> maskingIfSelected(int mask) {
		return b -> {
			if (b) {
				equipmentMask |= mask;
			} else {
				equipmentMask &= ~mask;
			}
		};
	}
	
	@Override
	protected void save() {
		service.addScopedEquipment(equipmentMask | CONTROL_TOWER | POWER_STATION); // add tower control and power-station
	}
	
}
