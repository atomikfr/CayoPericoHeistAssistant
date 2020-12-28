package jmodmenu.cayo_perico.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jmodmenu.cayo_perico.model.BoltCutters;
import jmodmenu.cayo_perico.model.GrapplingEquipment;
import jmodmenu.cayo_perico.model.GuardUniform;

@SuppressWarnings("serial")
public class MapPanel extends JPanel {
	
	private BufferedImage map;
	List<MapIcon> icons = new LinkedList<>();
	
	Consumer<Graphics> onDraw;
	CalibrationReference ref;
	
	public MapPanel(String filename) {
		changeBackgroundImage(filename);
	    setPreferredSize( new Dimension(map.getWidth(), map.getHeight()) );
	}
	
	public void changeBackgroundImage(String filename) {
		try {                
           map = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(filename));
        } catch (IOException ex) {
             throw new RuntimeException("Impossible de charger la map " + filename, ex);
        }
	}
	
	public void onDraw(Consumer<Graphics> onDraw) {
		this.onDraw = onDraw;
	}
	
	public CalibrationReference calibrate(float mapX, float mapY) {
		ref = new CalibrationReference();
		return ref.calibrate(mapX, mapY);
	}
	
	public void setCalibrationReference(CalibrationReference ref) {
		this.ref = ref;
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(map, 0, 0, this);
        int size = 11;
        int offset = size / 2 + 1;
        icons.stream()
    	.forEach( icon -> {
    		g.setColor(icon.color);
    		Point p = ref.apply(icon.pos[0], icon.pos[1]);
    		g.fillOval(p.x-offset+icon.xoffset, p.y-offset+icon.yoffset, size, size);
    	} );
        if ( onDraw != null ) onDraw.accept(g);
    }

    
    public static void main(String[] args) {
    	
    	
		SwingUtilities.invokeLater( () -> {
			MapPanel panel = new MapPanel( "cayo_perico_map.png" );
			panel
				.calibrate(4052.4f, -4655.79f).to(193, 217)
				.calibrate(5478.63f, -5847.74f).to(814, 740);
			
			panel.onDraw( g -> {
				
				int x = panel.getWidth() - 150;
				
				g.setFont( new Font("verdana", Font.BOLD, 18) );
				
				g.setColor(Color.GRAY);
				g.drawString("Pinces", x, 20);
				
				g.setColor(Color.MAGENTA);
				g.drawString("Uniformes", x, 40);
				
				g.setColor(Color.BLUE);
				g.drawString("Grappins", x, 60);
			});
			
			/*
			for (int i = 0; i < 16; i++) {
				BoltCutters cutter = new BoltCutters(i);
				MapIcon icon = new MapIcon();
				icon.pos = cutter.position();
				panel.addIcon(icon);
			}
			*/
			
			// 18568
			// return "Global_1706028[iParam0 /*53*/].f_5.f_5";
			BoltCutters.fromGlobal(-1)
				.stream()
				.map( item -> {
					MapIcon icon = new MapIcon();
					icon.pos = item.position();
					icon.color = Color.GRAY;
					return icon;
				})
				.forEach( panel::addIcon );
			
			// "Global_1706028[iParam0 /*53*/].f_5.f_4";
			GuardUniform.fromGlobal(-1)
				.stream()
				.map( item -> {
					MapIcon icon = new MapIcon();
					icon.pos = item.position();
					icon.color = Color.MAGENTA;
					return icon;
				})
				.forEach( panel::addIcon );
			
			// Global_1706028[iParam0 /*53*/].f_5.f_3
			GrapplingEquipment.fromGlobal(-1)
				.stream()
				.map( item -> {
					MapIcon icon = new MapIcon();
					icon.pos = item.position();
					icon.color = Color.BLUE;
					return icon;
				})
				.forEach( panel::addIcon );
			
			
			
			JFrame frame = new JFrame();
			frame.getContentPane().add(panel);
			frame.pack();
			frame.setLocation(100, 100);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});
	}
    
    public MapPanel addIcon(MapIcon icon) {
    	icons.add(icon);
    	return this;
    }
}
