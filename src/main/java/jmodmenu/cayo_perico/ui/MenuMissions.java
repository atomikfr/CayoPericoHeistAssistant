package jmodmenu.cayo_perico.ui;

import java.util.function.Consumer;

import jmodmenu.cayo_perico.model.H4_MISSIONS;

/**
 * Loads/Save MP0_H4_MISSIONS, applying a mask() to limit modifications. 
 * @see jmodmenu.cayo_perico.model.H4_MISSIONS
 */
public abstract class MenuMissions extends MenuAbstract implements H4_MISSIONS {

	int MP0_H4_MISSIONS;
	private int maskSet;
	
	MenuMissions(MenuContext ctx) {
		super(ctx);
		maskSet = mask();
	}
	
	protected abstract int mask();
	
	@Override
	protected void evaluateContext() {
		super.evaluateContext();
		MP0_H4_MISSIONS = service.getApproach(playerIndex) & maskSet;
	}
	
	@Override
	protected void save() {
		service.addApproach(MP0_H4_MISSIONS, maskSet);
	}
	
	protected Consumer<Boolean> maskApproachIfSelected(int mask) {
		return b -> {
			if (b) {
				MP0_H4_MISSIONS |= mask;
			} else {
				MP0_H4_MISSIONS &= ~mask;
			}
		};
	}
	
}
