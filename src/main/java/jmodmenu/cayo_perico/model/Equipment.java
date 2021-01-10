package jmodmenu.cayo_perico.model;

import lombok.Getter;

public enum Equipment {
	
	BOLT_CUTTERS(BoltCutters.class, "MPx_H4CNF_BOLTCUT"),
	GUARD_UNIFORM(GuardUniform.class, "MPx_H4CNF_UNIFORM"),
	GRAPPLING_EQUIPMENT(GrapplingEquipment.class, "MPx_H4CNF_GRAPPEL"),
	GUARD_TRUCK(GuardTruck.class, "MPx_H4CNF_TROJAN");

	@Getter
	Class<? extends MapItem> itemClass;
	
	@Getter
	String statName;
	
	Equipment(Class<? extends MapItem> itemClass, String statName) {
		this.itemClass = itemClass;
		this.statName = statName;
	}
}
