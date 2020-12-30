package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
import jmodmenu.core.PlayerInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CayoPericoMap implements MenuContext {
	
	@Getter
	Map<String, Color> itemColors = Map.ofEntries(
		Map.entry(GuardUniform.NAME, Color.YELLOW),
		Map.entry(GrapplingEquipment.NAME, Color.MAGENTA),
		Map.entry(BoltCutters.NAME, Color.GRAY),
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
	
	MenuScopeOut menuScopeOut;
	MenuEquipment menuEquipment;
	MenuApproach menuApproach;
	MenuTools menuTools;
	MenuWeapons menuWeapons;
	MenuDisturb menuDisturb;
	MenuCuts menuCuts;
	MenuHeist menuHeist;
	
	public  CayoPericoMap(CayoPericoMapService service) {
		this.service = service;
		panel = new MapPanel( MapView.ISLAND.imageFile );		
		setView( MapView.ISLAND );

		playerSelector = createPlayerSelector(null);
		panel.setLayout(null);
		menuManager = new MenuManager(panel);

		menuScopeOut = subMenu(MenuScopeOut.class);
		menuEquipment = subMenu(MenuEquipment.class);
		menuApproach = subMenu(MenuApproach.class);
		menuTools = subMenu(MenuTools.class);
		menuWeapons = subMenu(MenuWeapons.class);
		menuDisturb = subMenu(MenuDisturb.class);
		menuCuts = subMenu(MenuCuts.class);
		menuHeist = subMenu(MenuHeist.class);
		
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
	
	<T extends MenuAbstract> T subMenu(Class<T> klass) {
		T abs;
		try {
			abs = klass.getConstructor(MenuContext.class).newInstance(this);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("Unable to create menu " + klass.getName(), e);
		}
		abs.whenBack(this::menuGeneral);
		return abs;
	}
	
	@Override
	public MapView currentView() {
		return mapView;
	}
	@Override
	public MenuManager menuManager() {
		return menuManager;
	}
	@Override
	public void refreshData() {
		playerSelected(selectedPlayer); // trigger a refresh
	}
	@Override
	public PlayerInfo selectedPlayer() {
		return selectedPlayer;
	}
	@Override
	public CayoPericoMapService service() {
		return service;
	}
	@Override
	public void repaint() {
		panel.repaint();
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
		menuManager.clear();

		BiConsumer<String, MenuItf> adder = (titleKey, menu) -> 
			menuManager.addSubMenu( txt(titleKey), changeMenuFn(menu) );
		
		adder.accept("menu.scope_out", menuScopeOut);
		adder.accept("menu.equipment", menuEquipment);
		adder.accept("menu.approach", menuApproach);
		adder.accept("menu.tools", menuTools);
		adder.accept("menu.weapons", menuWeapons);
		adder.accept("menu.disturb", menuDisturb);
		adder.accept("menu.cuts", menuCuts);
		adder.accept("menu.heist", menuHeist);
		panel.repaint();
	}
	
	private Runnable changeMenuFn(MenuItf menu) {
		return () -> {
			menu.show();
			panel.repaint();
		};
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
		
		long additionalLootValue = items.stream()
			.filter( item -> item instanceof SecondaryLoot )
			.map( SecondaryLoot.class::cast )
			.collect( Collectors.groupingBy(SecondaryLoot::getType, Collectors.counting()) )
			.entrySet()
			.stream()
			.mapToLong( entry -> service.getStackLootValue(playerIndex, entry.getKey()) * entry.getValue() )
			.sum();
		
		boolean isHard = service.isHardMode(playerIndex);
		
		int mainLootValue = (int)(mainLoot.value() * (isHard ? 1.0 : 1.1));

		lootManager.set(
			txt("loots."+mainLoot.text()),
			String.format(Locale.US, "$%,d", mainLootValue),
			String.format(Locale.US, "$%,d", additionalLootValue)
		)
		.setHardMode(isHard);
		
		if ( isLocalPlayerSelected ) lootManager.whenDifficultyToggle( hardActivated -> {
			int option = JOptionPane.showConfirmDialog(panel, 
				txt("menu.confirm_difficulty_toggle"),
				txt("menu.hard_"+hardActivated),
				JOptionPane.YES_NO_OPTION
			);
			if ( option == JOptionPane.YES_OPTION ) {
				service.setHardMode( hardActivated );
				return hardActivated;
			} else {
				return !hardActivated;
			}
		});
		
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
