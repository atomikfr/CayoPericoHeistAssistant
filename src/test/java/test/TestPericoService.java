package test;

import org.junit.Test;

import jmodmenu.GtaProcess;
import jmodmenu.cayo_perico.model.LootType;
import jmodmenu.cayo_perico.service.CayoPericoGtaService;
import jmodmenu.cayo_perico.service.LootDataProvider;

public class TestPericoService {

	// @Test
	public void testPerico() throws Exception {
		GtaProcess gta = new GtaProcess();
		CayoPericoGtaService service = new CayoPericoGtaService(gta);
		
		LootDataProvider provider = new LootDataProvider(service, gta.localPlayerIndex());
		provider.reload();
		
		for (int i = 0; i < 24; i++) {
			provider.unsetLootAtPosition(i, true);
			provider.addLoot(LootType.WEED, i, true);
		}
		for (int i = 0; i < 8; i++) {
			provider.unsetLootAtPosition(i, false);
			provider.addLoot(LootType.WEED, i, false);
		}
		for (int i = 0; i < 7; i++) {
			provider.unsetPaintAtPosition(i);
		}
		provider.addLoot(LootType.PAINTINGS, 3, false);
		provider.addLoot(LootType.PAINTINGS, 4, false);

		service.addApproach(131071, 131071);
		service.setWeapon(2);
		service.addScopedEquipment(0x8FFF);
		provider.saveChanges();
		
		service.waitEmptyQueue();
		service.restartSubmarineComputer();
	}
}
