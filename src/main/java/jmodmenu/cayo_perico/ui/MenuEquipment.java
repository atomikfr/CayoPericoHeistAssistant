package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import jmodmenu.cayo_perico.model.BoltCutters;
import jmodmenu.cayo_perico.model.Equipment;
import jmodmenu.cayo_perico.model.GrapplingEquipment;
import jmodmenu.cayo_perico.model.GuardTruck;
import jmodmenu.cayo_perico.model.GuardUniform;
import jmodmenu.cayo_perico.model.H4CNF_BS_GEN;
import jmodmenu.cayo_perico.model.MapItem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MenuEquipment extends MenuAbstract implements H4CNF_BS_GEN {
	
	
	MenuEquipmentLocation me;
	List<MapItem> equipmentItems;
	
	public MenuEquipment(MenuContext ctx) {
		super(ctx);
		evaluateContext();
		equipmentItems = new LinkedList<>(service.getEquipment(playerIndex));
		me = new MenuEquipmentLocation(ctx, equipmentItems);
		me.whenBack(this::show);
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
		if ( isLocalPlayerSelected ) {
			menuManager.addSubMenu(txt("menu.customize"), me::show);
		}
		context.setMapItems(equipmentItems);
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
		
		Map<Equipment, Integer> original  = positions( service.getEquipment(playerIndex) );
		Map<Equipment, Integer> afterMenu = positions( equipmentItems );
		
		afterMenu.forEach( (equipment, position) -> {
			int originalPosition = original.get(equipment);
			log.debug("{} Original[{}] AfterMenu[{}]", equipment, originalPosition, position );
			if ( originalPosition != position ) service.setEquipmentPosition(equipment, position);
		});

	}
	
	protected static Map<Equipment, Integer> positions(List<MapItem> items) {
		int[] positions = new int[ Equipment.values().length ];
		AtomicInteger truckPosition = new AtomicInteger();
		
		Map<Class<? extends MapItem>, Equipment> toEquipment = Map.of(
				GuardUniform.class, Equipment.GUARD_UNIFORM,
				GrapplingEquipment.class, Equipment.GRAPPLING_EQUIPMENT,
				BoltCutters.class, Equipment.BOLT_CUTTERS
		);
		items.stream()
			// .filter(k::isInstance)
			.peek( item -> { if (item instanceof GuardTruck) {
				truckPosition.set(item.getId());
			} } )
			.filter( item -> toEquipment.containsKey(item.getClass()) )
			.forEach( item -> positions[toEquipment.get(item.getClass()).ordinal()] |= (1 << item.getId()) );
		Map<Equipment, Integer> result = new HashMap<>();
		toEquipment.forEach( (k,e) -> {
			result.put(e, positions[e.ordinal()]);
		});
		result.put(Equipment.GUARD_TRUCK, truckPosition.get());
		return result;
	}
	
}
