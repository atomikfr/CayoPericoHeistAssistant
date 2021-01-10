package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	
	int elementInStatQueue; 
	
	public  CayoPericoMap(CayoPericoMapService service) {
		this.service = service;
		
		panel = new MapPanel( MapView.ISLAND.imageFile );		
		setView( MapView.ISLAND );

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
		
		int controlX = 420;
		playerSelector = createPlayerSelector(null);
		
		JButton reload = new JButton( "reload" ); // "♻");
		reload.setLocation(controlX, 10);
		reload.setSize(80, 25);
		reload.addActionListener( event -> reload() );
		panel.add(reload);
		controlX += 90;
		
		JButton reloadComputer = new JButton("reset");
		reloadComputer.setLocation(controlX, 10);
		reloadComputer.setSize(80, 25);
		reloadComputer.addActionListener( event -> service.restartSubmarineComputer() );
		panel.add(reloadComputer);
		controlX += 80;
		
		URL loadingGif = getClass().getClassLoader().getResource("loading.gif");
		ImageIcon loadingIcon = new ImageIcon(loadingGif);
		loadingIcon.setImage( loadingIcon.getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT) );
		@SuppressWarnings("serial")
		JLabel lbl = new JLabel( loadingIcon ) {
			Font f = new Font("Arial", Font.BOLD, 18);
			protected void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.RED);
				g.setFont(f);
				String str = "" + elementInStatQueue;
				g.drawString(str, str.length() < 2 ? 26 : 22, 40);
			};
		};
		lbl.setOpaque(false);
		lbl.setBounds(60, 25, 64, 64);
		lbl.setVisible(false);
		panel.add(lbl);
		
		service.loadingListener( v -> {
			elementInStatQueue = v;
			lbl.setVisible(elementInStatQueue > 0);
			lbl.repaint();
		});
		
		panel.addMouseListener( new MyMouseAdapter() );
	}
	
	private void setMaxHeight(int height) {
		if ( panel.getBgDimension().getHeight() > height ) {
			double zoomFactor = (height-50.0) / panel.getBgDimension().getHeight();
			panel.setZoomFactor(zoomFactor);
			lootManager.setZoomFactor(zoomFactor);
		}
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
	
	Rectangle getScaledRectange(Rectangle rect) {
		return new Rectangle((int)(rect.x * panel.getZoomFactor()), (int)(rect.y * panel.getZoomFactor()), 
				(int)(rect.width * panel.getZoomFactor()), (int)(rect.height * panel.getZoomFactor()));
	}
	
	Rectangle getScaledRectange(int x, int y, int w, int h) {
		return new Rectangle((int)(x * panel.getZoomFactor()), (int)(y * panel.getZoomFactor()), 
				(int)(w * panel.getZoomFactor()), (int)(h * panel.getZoomFactor()));
	}
	
	class MyMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			Point p = e.getPoint();
			if ( mapView == MapView.ISLAND ) {
				// try to zoom in on each view
				for ( MapView other : MapView.values() ) {
					if (other == MapView.ISLAND) continue;
					if ( getScaledRectange(other.zoomIn).contains(p) ) {
						setView(other);
					}
				}

				return;
			}
			// on all other view try to zoom out
			if ( getScaledRectange(mapView.zoomOut).contains(p) ) {
				setView(MapView.ISLAND);
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
		res.setLocation( 250, 10 );
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
		
		if( mapView == MapView.COMPOUND ) items.addAll( service.getCompoundLoot(playerIndex) );
		else items.addAll( service.getIslandLoot(playerIndex) );
		
		setMapItems(items);
		MainLoot mainLoot = service.getMainLoot(playerIndex);
		
		List<SecondaryLoot> lootForValue = new LinkedList<>( service.getIslandLoot(playerIndex) );
		lootForValue.addAll( service.getCompoundLoot(playerIndex) );
		long additionalLootValue = lootForValue.stream()
			.collect( Collectors.groupingBy(SecondaryLoot::getType, Collectors.counting()) )
			.entrySet()
			.stream()
			.mapToLong( entry -> service.getStackLootValue(playerIndex, entry.getKey()) * entry.getValue() )
			.sum();
		
		boolean isHard = service.isHardMode(playerIndex);
		
		int mainLootValue = (int)(mainLoot.value() * (isHard ? 1.1 : 1.0));

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
		
		BiFunction<MapIcon, MapIcon, Double> dist = (a, b) -> 
			(a == null || b == null) ? 9999.9 : a.apply(panel.ref).distance(b.apply(panel.ref));
		
		
		for(MapItem item : items) {
			MapIcon icon = new MapIcon();
			icon.pos = item.position();
			icon.color = Optional.ofNullable( itemColors.get(item.name()) )
				.orElse(Color.CYAN);
			
			if ( log.isTraceEnabled() ) log.trace( String.format("Adding icon at pos %.2f %.2f", icon.pos[0], icon.pos[1]) );
			
			List<MapIcon> nearIcons = icons.stream()
				.filter( other -> {
					return ( dist.apply(other, icon) < 50.0 );
				})
				.collect(Collectors.toList());
			
			Supplier<MapIcon> findNearest = () -> nearIcons.stream()
				.min( Comparator.comparing(other ->  other.apply(panel.ref).distance(icon.apply(panel.ref))) )
				.orElse(null);

			MapIcon nearest = findNearest.get();
			double currentDist = dist.apply(icon, nearest);
			if ( log.isTraceEnabled() ) log.trace("Nearest object count[{}] min distance[{}]", nearIcons.size(), dist.apply(icon, nearest));
			
			boolean goDown = true;
			int xoffset = 0;
			int yoffset = 0;
			int loop = 0;
			while ( currentDist < 8.0 ) {
				if ( goDown ) {
					if ( nearest.pos[0] < icon.pos[0] )
						yoffset = 8;
					else yoffset = -8;
				} else {
					yoffset = 0;
					xoffset += 8;
				}
				goDown = !goDown;
				icon.offset(xoffset, yoffset);
				
				nearest = findNearest.get();
				currentDist = dist.apply(icon, nearest);
				if ( log.isTraceEnabled() ) log.trace( String.format("  Loop[%d] min is [%.2f]", loop, currentDist) );
				if ( loop++ > 10 ) {
					log.warn( "  stop solving near collision at loop {}", loop );
					break;
				}
			}
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
		
		final String VERSION = "0.10 Beta 3";
		boolean simulated = false;
		
		log.info("Running Cayo Perico Heist Assistant " + VERSION);
		
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
    		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    		DisplayMode mode = gd.getDisplayMode();
    		CayoPericoMap cayoPericoMap = new CayoPericoMap( service );
    		log.info("Create CayoPericoMap. Screen resolution [{}x{}]", mode.getWidth(), mode.getHeight());
    		cayoPericoMap.setMaxHeight( mode.getHeight() );
    		// cayoPericoMap.setMaxHeight( 800 );
    		cayoPericoMap.setPlayers(players);
			JFrame frame = new JFrame("Cayo Perico Heist Assistant " + VERSION);
			frame.getContentPane().add(cayoPericoMap.panel);
			frame.pack();
			frame.setLocation(50, 50);
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
    	});
	}



}
