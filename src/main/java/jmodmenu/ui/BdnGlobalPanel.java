package jmodmenu.ui;

import java.util.concurrent.CompletableFuture;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jmodmenu.GtaProcess;
import jmodmenu.ui.BdnStocks.DataBuilder;

public class BdnGlobalPanel extends JPanel {

	BdnBuildTime bdnBuildTime;
	BdnStocks bdnStocks;
	
	public BdnGlobalPanel() {
		BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS); 
		setLayout(layout);
		bdnStocks = new BdnStocks();
		add( bdnStocks );
		bdnBuildTime = new BdnBuildTime();
		add( bdnBuildTime );
	}
	
	public BdnBuildTime getBdnBuildTime() {
		return bdnBuildTime;
	}
	
	public BdnStocks getBdnStocks() {
		return bdnStocks;
	}
	
	
	public static void main(String[] args) throws Exception {
		final CompletableFuture<BdnGlobalPanel> futur = new CompletableFuture<>();
		SwingUtilities.invokeLater( () -> {
			BdnGlobalPanel bndPanel = new BdnGlobalPanel();
			futur.complete(bndPanel);
			JFrame frame = new JFrame();
			frame.getContentPane().add(bndPanel);
			frame.pack();
			frame.setLocation(100, 100);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});
		
		BdnGlobalPanel bdn = futur.get();
		GtaProcess gta = new GtaProcess();
		int idx = gta.localPlayer().getIndex();
		int offsetMax = 23873; // NightClubMaxGoods
		int ttpNightclub = 23858; // Time To Produce
		DataBuilder stockBuilder = bdn.getBdnStocks().builder();
		BdnBuildTime.Builder timeBuilder = bdn.getBdnBuildTime().builder();
		int[] indexOrder = new int[] { 6, 0, 1, 2, 3, 4, 5 };
		
		bdn.getBdnBuildTime().whenAction( (i, time) -> {
			// boolean ok = gta.globals().at(262145).at(ttpNightclub+indexOrder[i]).set(time);
			boolean ok = true;
			System.out.format( "Write value [%d%]=%d: %s\n", i, time, ok);
		} );
		
		for ( int i = 0; i < 7; i++) {
			int stock = (int) gta.globals()
				.at(1590535).at(idx, 876).at(274).at(281).at(9).at(i).get();
			int max = (int) gta.globals()
				.at(262145).at(offsetMax+indexOrder[i]).get();
			stockBuilder.addStock(stock, max);
			int ttp = (int) gta.globals()
				.at(262145).at(ttpNightclub+indexOrder[i]).get();
			timeBuilder.addTime(ttp);
		}
	}
}
