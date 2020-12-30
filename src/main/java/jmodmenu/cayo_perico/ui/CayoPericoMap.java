package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jmodmenu.GtaProcess;
import jmodmenu.I18n;
import jmodmenu.cayo_perico.model.BoltCutters;
import jmodmenu.cayo_perico.model.GrapplingEquipment;
import jmodmenu.cayo_perico.model.GuardTruck;
import jmodmenu.cayo_perico.model.GuardUniform;
import jmodmenu.cayo_perico.model.LootType;
import jmodmenu.cayo_perico.model.MainLoot;
import jmodmenu.cayo_perico.model.MapItem;
import jmodmenu.cayo_perico.model.SecondaryLoot;
import jmodmenu.cayo_perico.service.CayoPericoGtaService;
import jmodmenu.cayo_perico.service.CayoPericoMapService;
import jmodmenu.cayo_perico.service.CayoPericoMockService;
import jmodmenu.cayo_perico.service.LootDataProvider;
import jmodmenu.core.PlayerInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CayoPericoMap {
	
	@Getter
	Map<String, Color> itemColors = Map.ofEntries(
		Map.entry(GuardUniform.NAME, Color.YELLOW),
		Map.entry(GrapplingEquipment.NAME, Color.MAGENTA),
		Map.entry(BoltCutters.NAME, Color.LIGHT_GRAY),
		Map.entry(GuardTruck.NAME, Color.PINK),
		
		Map.entry(LootType.GOLD.name(), Color.ORANGE),
		Map.entry(LootType.COCAINE.name(), Color.WHITE),
		Map.entry(LootType.WEED.name(), Color.GREEN),
		Map.entry(LootType.CASH.name(), Color.BLUE)
	);
	
	CayoPericoMapService service;
	PlayerInfo selectedPlayer;
	boolean isLocalPlayerSelected = false;
	MapView mapView;
	
	MapPanel panel;
	JComboBox<PlayerInfo> playerSelector;
	Consumer<PlayerInfo> whenPlayerSelected;
	MenuManager menuManager;
	LootManager lootManager;
	
	Runnable NO_ACTION = () -> {};
	
	public CayoPericoMap(CayoPericoMapService service) {
		this.service = service;
		panel = new MapPanel( MapView.ISLAND.imageFile );		
		setView( MapView.ISLAND );

		playerSelector = createPlayerSelector(null);
		panel.setLayout(null);
		menuManager = new MenuManager(panel);
		menuGeneral();
		lootManager = new LootManager(panel);
		
		JButton reload = new JButton( "reload" ); // "♻");
		reload.setLocation(620, 10);
		reload.setSize(80, 25);
		reload.addActionListener( event -> reload() );
		panel.add(reload);
		
		JButton reloadComputer = new JButton("reset");
		reloadComputer.setLocation(710, 10);
		reloadComputer.setSize(80, 25);
		reloadComputer.addActionListener( event -> service.restartSubmarineComputer() );
		panel.add(reloadComputer);
		
		panel.addMouseListener( new MyMouseAdapter() );
		
	}
	
	class MyMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			Point p = e.getPoint();
			Rectangle rect = null;
			if ( mapView == MapView.ISLAND ) {
				rect = new Rectangle(587, 668, 60, 55);
				if ( rect.contains(p) ) {
					setView(MapView.COMPOUND);
				}
				return;
			}
			if (mapView == MapView.COMPOUND ) {
				rect = new Rectangle(33, 27, 200, 207);
				if ( rect.contains(p) ) {
					setView(MapView.ISLAND);
				}
				return;
			}
		}
	}
	
	public void setView(MapView view) {
		if ( this.mapView == view ) return;

		panel.icons.clear();
		panel.setCalibrationReference(view.calibrationReference);
		panel.changeBackgroundImage(view.imageFile);
		this.mapView = view;
		
		if ( selectedPlayer != null ) {
			playerSelected(selectedPlayer);
		} else {
			panel.repaint();
		}
	}
	
	private JComboBox<PlayerInfo> createPlayerSelector( List<PlayerInfo> players ) {
		if (players == null) players = Collections.emptyList();
		JComboBox<PlayerInfo> res = new JComboBox<>( players.toArray(new PlayerInfo[] {}) );
		res.setLocation( 450, 10 );
		res.setSize(150, 25);
		res.addActionListener( event -> playerSelected((PlayerInfo) playerSelector.getSelectedItem()) );
		return res;
	}
	
	private void reload() {
		setPlayers( service.getPlayersInfo() );
	}
	
	private void menuGeneral() {
		menuManager.clear()
		.addSubMenu(txt("menu.scope_out"), this::menuReperage)
		.addSubMenu(txt("menu.equipment"), this::menuEquipement)
		.addSubMenu(txt("menu.approach"), this::menuApproche)
		.addSubMenu(txt("menu.tools"), this::menuMateriel)
		.addSubMenu(txt("menu.weapons"), this::menuArsenal)
		.addSubMenu(txt("menu.disturb"), this::menuPerturbations)
		.addSubMenu(txt("menu.cuts"), this::menuRepartition)
		.addSubMenu(txt("menu.heist"), this::menuBraquage);
		panel.repaint();
	}
	
	
	int requestScope = 0;
	LootDataProvider lootDataProvider;
	private void menuReperage() {
		menuManager.clear()
		.backTo(() -> {
			lootDataProvider = null;
			currentLocation = null;
			menuGeneral();
		});
		if ( selectedPlayer == null ) { panel.repaint(); return; }
		
		int playerIndex = selectedPlayer.getIndex();
		if ( lootDataProvider == null ) {
			lootDataProvider = new LootDataProvider(service, playerIndex);
			lootDataProvider.reload();
		}
		
		List<SecondaryLoot> loots = lootDataProvider.allSecondaryLoots();
		Map<LootType, Long> counts = loots.stream()
			.collect( Collectors.groupingBy(SecondaryLoot::getType, Collectors.counting()) );
		
		BiFunction<LootType, String, String> getLabel = (type, str) -> String.format("[%d] %s", Optional.ofNullable(counts.get(type)).orElse(0L), str);
		
		Map <String, Integer> itemsConf = new LinkedHashMap<>();
		itemsConf.put(getLabel.apply(LootType.CASH, txt("loots.cash")), 0x1);
		itemsConf.put(getLabel.apply(LootType.WEED, txt("loots.weed")), 0x2);
		itemsConf.put(getLabel.apply(LootType.COCAINE, txt("loots.cocaine")), 0x4);
		itemsConf.put(getLabel.apply(LootType.GOLD, txt("loots.gold")), 0x8);
		itemsConf.put(getLabel.apply(LootType.PAINTINGS, txt("loots.paintings")), 0x10);
		
		int scopedMask = 1;
		for( LootType type : LootType.values() ) {
			if ( service.hasScopedLoot(playerIndex, type) ) requestScope |= scopedMask;
			scopedMask = scopedMask << 1;
		}
		menuManager
		.addSubMenu(txt("menu.main_loot"), this::menuLootPrincipal)
		.addSpacer()
		.addLabel( txt("menu.additional_loot") )
		.checkMaskItems(itemsConf, requestScope, this::maskingIfSelectedScope);
		
		if ( isLocalPlayerSelected ) {
			menuManager
			.addSubMenu( txt("menu.customize"), this::menuCustomLoots )
			.addSave( () -> {
				lootDataProvider.saveChanges();
				MapItem.bitStream(5, requestScope)
				.mapToObj( idx -> LootType.values()[idx] )
				.forEach( type -> service.scopeLoot(type) );
			});
		}
		panel.repaint();
	}
	private Consumer<Boolean> maskingIfSelectedScope(int mask) {
		return b -> {
			if (b) {
				requestScope |= mask;
			} else {
				requestScope &= ~mask;
			}
		};
	}
	
	@Setter
	int selectedLoot;
	private void menuLootPrincipal() {
		menuManager.clear()
		.backTo( this::menuReperage );
		
		MainLoot loot = service.getMainLoot(selectedPlayer.getIndex());
		List <String> itemsConf = Stream.of( MainLoot.values() )
			.map( l -> txt("loots."+l.name().toLowerCase() ) )
			.collect( Collectors.toList() );
		menuManager
		.checkIndexItems(itemsConf, loot.ordinal(), this::setSelectedLoot );
		
		if ( isLocalPlayerSelected ) menuManager.addSave( () -> {
			service.setMainLoot( MainLoot.values()[selectedLoot] );
			playerSelected( selectedPlayer ); // refresh screen and data
		});
	}
	
	String currentLocation;
	private void menuCustomLoots() {
		Map<String, Integer> locations = new LinkedHashMap<>();
		locations.put("airstrip",   0x0000003F);
		locations.put("north_dock", 0x00001FC0);
		locations.put("fields",     0x0001E000);
		locations.put("main_dock",  0x00FE0000);
		locations.put("compound",   0x000000FF);

		if ( currentLocation == null ) currentLocation = "airstrip";
		boolean island = !"compound".equals(currentLocation);
		List<? extends SecondaryLoot> allLoots = island ? lootDataProvider.getSecondaryIslandLoot() : lootDataProvider.getSecondaryCompundLoot();
		
		int currentMask = locations.get(currentLocation);
		// System.out.format("current location[%s] mask[%08x]\n", currentLocation, currentMask);
		
		menuManager.clear()
		.backTo( this::menuReperage )
		.addSpacer()
		.addLabel( txt("location."+currentLocation) )
		.addNavBar(
			() -> {
				List<String> locationKeys = new ArrayList<>();
				locations.forEach((txt, m) -> locationKeys.add(txt));
				int i = locationKeys.indexOf(currentLocation) - 1;
				if (i < 0) {
					menuCustomPaints();
					return;
				};
				currentLocation = locationKeys.get(i);
				menuCustomLoots();
			}, 
			() -> {
				List<String> locationKeys = new ArrayList<>();
				locations.forEach((txt, m) -> locationKeys.add(txt));
				int i = locationKeys.indexOf(currentLocation) + 1;
				if (i >= locationKeys.size()) { 
					menuCustomPaints(); 
					return; 
				}
				currentLocation = locationKeys.get(i);
				menuCustomLoots();
			}
		)
		.addChooserHeader();
		LootType[] types = {LootType.CASH, LootType.WEED, LootType.COCAINE, LootType.GOLD};
		int k = 0;
		
		for (int mask = 1, cpt = 0; mask != 0x80000000; mask = mask << 1, k++) {
			if ( (currentMask & mask) == 0 ) {
				continue;
			}
			cpt++;
			AtomicInteger aIdx = new AtomicInteger(k);
			menuManager.addLootChooser(cpt, mask, allLoots, types, (type, b) -> {
				System.out.format("(CB) Change %d on %s(%s) to %s:%s\n", aIdx.get(), currentLocation, island, type.name(), b);
				lootDataProvider.unsetLootAtPosition(aIdx.get(), island);
				if (b) lootDataProvider.addLoot(type, aIdx.get(), island);
				refreshViewCustomLoots();
			});
		}
		refreshViewCustomLoots();
		// panel.repaint(); // done in refreshViewCustomLoots
	}
	
	
	private void refreshViewCustomLoots() {
		List<MapItem> items = new LinkedList<>( /* service.getEquipment(playerIndex) */ );
		if ( mapView == MapView.ISLAND ) {
			items.addAll( lootDataProvider.getSecondaryIslandLoot() );
		}
		if ( mapView == MapView.COMPOUND ) {
			items.addAll( lootDataProvider.getSecondaryCompundLoot() );
		}
		setMapItems(items);
	}
	
	private void menuCustomPaints() {
		menuManager.clear()
		.backTo(this::menuReperage)
		.addSpacer()
		.addLabel( txt("loots.paints") )
		.addNavBar(
			() -> {
				currentLocation = "compound";
				menuCustomLoots();
			}, 
			() -> {
				currentLocation = null;
				menuCustomLoots();
			}
		);
		
		Function<Integer, Boolean> hasPaintAt = (idx) -> lootDataProvider.getSecondaryCompundLoot()
				.stream()
				.filter( loot -> loot.getIdx() == idx )
				.filter( loot -> loot.getType() == LootType.PAINTINGS )
				.findAny()
				.isPresent();
		for (int j = 0; j < 7; j++) {			
			String place = txt("location.paint_"+j);
			menuManager.addPaintLocation((j+1), place, hasPaintAt.apply(j), (idx, b) -> {});
		}
		panel.repaint();

	}
	
	int equipmentMask;
	private void menuEquipement() {
		menuManager.clear()
		.backTo(this::menuGeneral);
		if ( selectedPlayer == null ) return;
		
		int MPx_H4CNF_BS_GEN = service.getScopedEquipment(selectedPlayer.getIndex());
		equipmentMask = MPx_H4CNF_BS_GEN & 0x8FFF;
		Map <String, Integer> itemsConf = new LinkedHashMap<>();
		itemsConf.put(txt("equipment.grappling_equipment"), 0xF);
		itemsConf.put(txt("equipment.guard_uniform"), 0xF0);
		itemsConf.put(txt("equipment.bolt_cutters"), 0xF00);
		itemsConf.put(txt("equipment.guard_truck"), 0x8000);
		menuManager
		.addSpacer()
		.checkMaskItems(itemsConf, MPx_H4CNF_BS_GEN, this::maskingIfSelected );
		if ( isLocalPlayerSelected ) menuManager.addSave( () -> {
			service.addScopedEquipment(equipmentMask | 0x17000); // add tower control and power-station
		});
		panel.repaint();
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
	
	// int equipmentMask;
	int approachMask;
	private void menuApproche() {
		menuManager.clear()
		.backTo(this::menuGeneral);
		if ( selectedPlayer == null ) return;
		
		int MP0_H4_MISSIONS = service.getApproach(selectedPlayer.getIndex());
		int maskSet = 0xFE;
		approachMask = MP0_H4_MISSIONS & maskSet;
		int Pilot = 0x80;
		Map <String, Integer> itemsConf = new LinkedHashMap<>();
		itemsConf.put(txt("vehicles.submarine"), 0x2);
		itemsConf.put(txt("vehicles.bomber"), 0x4 | Pilot);
		itemsConf.put(txt("vehicles.plane"), 0x8);
		itemsConf.put(txt("vehicles.copter"), 0x10 | Pilot);
		itemsConf.put(txt("vehicles.patrol_boat"), 0x20);
		itemsConf.put(txt("vehicles.smuggler_boat"), 0x40);
		menuManager
		.addSpacer()
		.checkMaskItems(itemsConf, MP0_H4_MISSIONS, this::maskApproachIfSelected );
		if ( isLocalPlayerSelected ) menuManager.addSave( () -> {
			service.addApproach(approachMask, maskSet);
		});
		panel.repaint();
	}
	
	private Consumer<Boolean> maskApproachIfSelected(int mask) {
		return b -> {
			if (b) {
				approachMask |= mask;
			} else {
				approachMask &= ~mask;
			}
		};
	}
	
	private void menuMateriel() {
		menuManager.clear()
			.backTo(this::menuGeneral);
		if ( selectedPlayer == null ) return;
	
		int MP0_H4_MISSIONS = service.getApproach(selectedPlayer.getIndex());
		int maskSet = 0xF00;
		approachMask = MP0_H4_MISSIONS & maskSet;
		// int Pilot = 0x80;
		String lootAccessMaterial = service.getMainLoot(selectedPlayer.getIndex()) == MainLoot.BONDS 
				? txt("tools.safe_code") : txt("tools.plasma_cutter");
		Map <String, Integer> itemsConf = new LinkedHashMap<>();
		itemsConf.put(txt("tools.demolition_charges"),  0x100);
		itemsConf.put(txt("tools.acetylene_torch"), 0x200);
		itemsConf.put(lootAccessMaterial, 0x400);
		itemsConf.put(txt("tools.fingerprint"), 0x800);
		menuManager
		.addSpacer()
		.checkMaskItems(itemsConf, MP0_H4_MISSIONS, this::maskApproachIfSelected );
		if ( isLocalPlayerSelected ) menuManager.addSave( () -> {
			service.addApproach(approachMask, maskSet);
		});
		panel.repaint();
	}
	
	private void menuPerturbations() {
		menuManager.clear()
			.backTo(this::menuGeneral);
		if ( selectedPlayer == null ) return;
	
		int MP0_H4_MISSIONS = service.getApproach(selectedPlayer.getIndex());
		int maskSet = 0xE000;
		approachMask = MP0_H4_MISSIONS & maskSet;
		Map <String, Integer> itemsConf = new LinkedHashMap<>();
		itemsConf.put(txt("disturb.weapons"),  0x2000);
		itemsConf.put(txt("disturb.armor"), 0x4000);
		itemsConf.put(txt("disturb.support"), 0x8000);
		menuManager
		.addSpacer()
		.checkMaskItems(itemsConf, MP0_H4_MISSIONS, this::maskApproachIfSelected );
		if ( isLocalPlayerSelected ) menuManager.addSave( () -> {
			service.addApproach(approachMask, 0xE000);
		} );
		panel.repaint();
	}
	
	int weaponIndex;
	private void menuArsenal() {
		menuManager.clear()
		.backTo(this::menuGeneral);
		if ( selectedPlayer == null ) return;
	
		int MP0_H4_MISSIONS = service.getApproach(selectedPlayer.getIndex());
		int MP0_H4CNF_WEAPONS = service.getWeapon(selectedPlayer.getIndex());
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
		.checkMaskItems(suppressorConf, MP0_H4_MISSIONS, this::maskApproachIfSelected );
		if ( isLocalPlayerSelected ) menuManager.addSave( () -> {
			int value = weaponIndex == 0 ? 0 : approachMask & 0x1000;
			service.setWeapon(weaponIndex);
			service.addApproach(value, 0x1000);
		});
		panel.repaint();
	}
	
	public void setWeaponIndex(int weaponIndex) {
		this.weaponIndex = weaponIndex;
	}
	
	private void menuRepartition() {
		AtomicReference<List<JTextField>> fieldsRef = new AtomicReference<List<JTextField>>();
		menuManager.clear()
		.backTo(this::menuGeneral)
		.addFields(service.getCuts(), fieldsRef::set)
		.addAction(txt("cuts.all_85"), () -> fieldsRef.get()
			.stream()
			.forEach( f -> f.setText("85"))
		);
		if ( isLocalPlayerSelected ) menuManager.addSave( () -> {
			Integer[] values = fieldsRef.get()
				.stream()
				.map( this::fieldValueAsInt )
				.collect(Collectors.toList())
				.toArray(new Integer[] {});
			service.setCuts(values);
		});
		panel.repaint();
	}
	
	private int fieldValueAsInt(JTextField f) {
		String txt = f.getText().toUpperCase().replace(".", ",").trim();
		if ( txt.isEmpty() ) return 0;
		
		try {
			return Integer.parseInt(txt);
		} catch(NumberFormatException e) {
			return 0;
		}
	}
	
	private void menuBraquage() {
		menuManager.clear()
		.backTo(this::menuGeneral);
		if ( selectedPlayer == null ) return;
		
		AtomicReference<List<JTextField>> fieldsRef = new AtomicReference<List<JTextField>>();
		MainLoot loot = service.getMainLoot(selectedPlayer.getIndex());
		int v = service.getCurrentLootValue(selectedPlayer.getIndex(), loot);
		int bagSize = service.getMyBagSize();
		Map <String, Integer> itemsConf = new LinkedHashMap<>();
		itemsConf.put(txt("heist.loot_value"),  v);
		itemsConf.put(txt("heist.bagsize"), bagSize);
		menuManager.addFields(itemsConf, fieldsRef::set);
		if ( isLocalPlayerSelected ) menuManager.addSave( () -> {
				int newLootValue = fieldValueAsInt(fieldsRef.get().get(0));
				int newBagSize = fieldValueAsInt(fieldsRef.get().get(1));
				service.setLootValue(loot, newLootValue);
				service.setBagSize(newBagSize);
			});
		panel.repaint();
	}
	
	private void playerSelected(PlayerInfo player) {
		int playerIndex = player.getIndex();
		selectedPlayer = player;
		
		int i = service.getLocalPlayerIndex();
		isLocalPlayerSelected = (i == playerIndex);
		List<MapItem> items = new LinkedList<>( service.getEquipment(playerIndex) );
		
		if( mapView == MapView.ISLAND ) items.addAll( service.getIslandLoot(playerIndex) );
		if( mapView == MapView.COMPOUND ) items.addAll( service.getCompoundLoot(playerIndex) );
		
		setMapItems(items);
		MainLoot mainLoot = service.getMainLoot(playerIndex);
		lootManager.set(
			txt("loots."+mainLoot.text()),
			String.format(Locale.US, "$%,d", mainLoot.value()),
			"n/a"
		)
		.setHardMode(service.isHardMode(playerIndex));
		if ( whenPlayerSelected != null ) whenPlayerSelected.accept( player );
		panel.repaint();
	}
	
	public void setMapItems(List<MapItem> items) {
		List<MapIcon> icons = new LinkedList<>();
		for(MapItem item : items) {
			MapIcon icon = new MapIcon();
			icon.pos = item.position();
			icon.color = Optional.ofNullable( itemColors.get(item.name()) )
				.orElse(Color.CYAN);
			icons.add(icon);
		}
		panel.icons = icons;
		panel.repaint();
	}
	
	public void setPlayers(List<PlayerInfo> players) {
		if ( playerSelector != null ) panel.remove(playerSelector);
		playerSelector = createPlayerSelector(players);
		panel.add(playerSelector);
		playerSelected((PlayerInfo) playerSelector.getSelectedItem());
	}
	
	public void whenPlayerSelected(Consumer<PlayerInfo> whenPlayerSelected) {
		this.whenPlayerSelected = whenPlayerSelected;
	}

	public static void main(String[] args) {
		
		log.info("Running Cayo Perico Assistant");
		
		boolean simulated = false;
		CayoPericoMapService service;
		
		String lang = Optional.ofNullable(System.getProperty("user.language"))
				.orElse("en")
				.toLowerCase();
		// lang = "en";
		jmodmenu.I18n.load(lang);
		log.info("Load language file {}", lang);
		
		/* */
		if (!simulated) {
			GtaProcess gta;
			try {
				gta = new GtaProcess();
			} catch (Exception e) {
				String message = I18n.txt("error.no_process");
				log.error("GTA process not found.", e);
				JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			log.info("Gta process found. Ptr: {}", gta);
			service =  new CayoPericoGtaService(gta);
		} else {
			log.warn("Running in SIMULATED MODE");
			service =  new CayoPericoMockService();
		}
		
    	List<PlayerInfo> players = service.getPlayersInfo();
    	SwingUtilities.invokeLater( () -> {
    		CayoPericoMap cayoPericoMap = new CayoPericoMap( service );
    		cayoPericoMap.setPlayers(players);
			JFrame frame = new JFrame("Cayo Perico Heist Assistant");
			frame.getContentPane().add(cayoPericoMap.panel);
			frame.pack();
			frame.setLocation(100, 100);
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
    	});
	}

}
