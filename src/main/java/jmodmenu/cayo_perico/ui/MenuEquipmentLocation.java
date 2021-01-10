package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jmodmenu.cayo_perico.model.Equipment;
import jmodmenu.cayo_perico.model.GuardTruck;
import jmodmenu.cayo_perico.model.H4CNF_BS_GEN;
import jmodmenu.cayo_perico.model.MapItem;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MenuEquipmentLocation extends MenuAbstract implements H4CNF_BS_GEN {
	
	@Setter
	Equipment equipment = Equipment.BOLT_CUTTERS;
	
	List<MapItem> equipmentItems;
	Integer guardTruckLocation;

	public MenuEquipmentLocation(MenuContext context, List<MapItem> equipmentItems) {
		super(context);
		showSaveAction = false;
		this.equipmentItems = equipmentItems;
		guardTruckLocation = equipmentItems.stream()
				.filter(item -> item instanceof GuardTruck)
				.findFirst()
				.map(MapItem::getId)
				.orElse(0);
	}
	
	@Override
	void content(MenuManager menuManager) {
		menuManager
		.addSpacer()
		.addNavBar( navActionFn(-1), navActionFn(1) )
		.addLabel( txt("equipment."+equipment.name().toLowerCase()).toUpperCase() );
		
		if ( equipment == Equipment.GUARD_TRUCK ) {
			menuManager.checkIndexItems(Arrays.asList("1", "2", "3", "4", "5"), guardTruckLocation, this::setTruckLocation);
		} else {
			List<String> currentLocations = equipmentItems.stream()
				.filter(item -> item.getClass() == equipment.getItemClass())
				.map(MapItem::getId)
				.map(id -> ((char)(id / 4 + 'A')) + "" + ((char)(id % 4 + '1')) )
				.collect(Collectors.toList());
			log.debug("Current locations of {}: {}", equipment, currentLocations);
			menuManager.addGroupLocationChooser( currentLocations, this::setEquipmentAt );
		}
		showSelectedItems();
	}
	
	protected void showSelectedItems() {
		List<MapItem> items = equipmentItems.stream()
				.filter(item -> item.getClass() == equipment.getItemClass())
				.collect(Collectors.toList());
		context.setMapItems(items);		
	}
	
	public Runnable navActionFn(int inc) {
		return () -> {
			int i = equipment.ordinal() + inc;
			if ( i >= Equipment.values().length ) i = 0;
			if ( i < 0 ) i = Equipment.values().length - 1;
			equipment = Equipment.values()[i];
			show();
		};
	}
	
	public void setEquipmentAt(String location) {
		int groupId = (location.charAt(0) - 'A');
		int idx = groupId*4 + (location.charAt(1) - '1');
		log.debug("Set {} at location {}, idx[{}] groupId[{}]", equipment.name(), location, idx, groupId);
		MapItem newItem;
		try {
			newItem = equipment.getItemClass()
				.getDeclaredConstructor(int.class)
				.newInstance(idx);
		} catch (Exception e) {
			log.error("Unable to create a {} with index {}", equipment.getClass().getName(), idx, e);
			return;
		}
		List<MapItem> items = equipmentItems.stream()
				.filter(item -> item.getClass() == equipment.getItemClass())
				.filter(item -> item.getId() / 4 == groupId)
				.collect(Collectors.toList());
		equipmentItems.removeAll(items);
		equipmentItems.add(newItem);
		showSelectedItems();
	}
	
	public void setTruckLocation(int i) {
		List<MapItem> items = equipmentItems.stream()
				.filter(item -> item instanceof GuardTruck)
				.collect(Collectors.toList());
		equipmentItems.removeAll(items);
		equipmentItems.add(new GuardTruck(i+1));
		showSelectedItems();
	}

}
