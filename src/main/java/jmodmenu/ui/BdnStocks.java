package jmodmenu.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class BdnStocks extends JPanel {

	Dimension dim = new Dimension(150, 30*7);
	
	int[] data = new int[14]; // 7 stock * 2 (current / max)
	String[] labels = new String[] {
		"cargaison",
		"chasse",
		"sud-américain",
		"pharma",
		"bio",
		"copie",
		"monaie"
	};
	
	public BdnStocks() {
		setMaximumSize(dim);
		setMinimumSize(dim);
		setPreferredSize(dim);
		
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		
		for (int i = 0; i < labels.length; i++) {
			StockText text = new StockText(i);
			text.setText(labels[i]);
			add( text );
		}
		
	}
	
	private class StockText extends JTextField {
		int idx;
		private StockText(int i) {
			this.idx = i;
			setEditable(false);
			setOpaque(false);
		}
		@Override
		public void paint(Graphics g) {
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, getWidth(), getHeight());
			int percentWidth = 0;
			if ( data[idx*2+1] > 0 ) {
				percentWidth = data[idx*2] * getWidth() / data[idx*2+1];
				g.setColor(Color.GREEN);
				g.fillRect(0, 0, percentWidth, getHeight());
			}
			// super.paint(g);
			g.setColor(Color.BLACK);
			g.drawString(getText(), 2, 20);
			g.setColor(Color.WHITE);
			g.drawString(""+data[idx*2], 130, 20);
		}
		
	}
	
	public DataBuilder builder() {
		return new DataBuilder();
	}
	
	public class DataBuilder {
		int i = 0;
		public DataBuilder addStock(int current, int max) {
			data[i*2] = current;
			data[i*2+1] = max;
			repaint();
			i++;
			return this;
		}
	}
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater( () -> {
			BdnStocks stocks = new BdnStocks();
			stocks.builder()
				.addStock(50, 100)
				.addStock(20, 20);
			
			JFrame frame = new JFrame();
			frame.getContentPane().add(stocks);
			frame.pack();
			frame.setLocation(100, 100);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});
		
	}

}

