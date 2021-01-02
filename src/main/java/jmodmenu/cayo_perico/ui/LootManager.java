package jmodmenu.cayo_perico.ui;

import java.awt.Color;
import java.util.function.Function;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import jmodmenu.I18n;
import lombok.Setter;

class LootManager {
	
	ComponentManager manager;
	JPanel panel;
	
	@Setter
	double zoomFactor = 1.0;
	
	LootManager(JPanel panel) {
		manager = new ComponentManager(panel);
		this.panel = panel;
	}
	
	LootManager set(String lootName, String lootValue, String addLootValue) {
		manager.initLocation((int)(panel.getPreferredSize().getWidth() - 220), 10);
		manager.clear()
			.addBox( lootName, lootValue )
			.addBox( I18n.txt("menu.additional_loot"), addLootValue );
		return this;
	}
	
	JCheckBox hardCheckBox;
	LootManager setHardMode(boolean hard) {
		// manager.
		ComponentStyle style = new ComponentStyle();
		style.background = new Color(0x3c1a1a);
		style.foreground = new Color(0xd84848);
		
		ComponentStyle styleBox = new ComponentStyle();
		styleBox.background = Color.WHITE;
		styleBox.foreground = Color.WHITE;
		styleBox.borderColor = Color.WHITE;
		
		String difficultyLabel = I18n.txt("menu.hard_"+hard);
		JCheckBox checkBox = new JCheckBox( difficultyLabel, hard);
		checkBox.setIcon(new CheckBoxIcon(styleBox));
		checkBox.setForeground( style.foreground );
		checkBox.setBackground( style.background);
		checkBox.setBorder( new LineBorder(style.foreground, 2) );
		checkBox.setBorderPainted(true);
		checkBox.setLocation((int)(665 * zoomFactor), (int)(815 * zoomFactor));
		checkBox.setSize((int)(390 * zoomFactor), (int)(55 * zoomFactor));
		
		checkBox.setEnabled(false); // only enable if a callback function is set
		
		manager.add(checkBox);
		hardCheckBox = checkBox;
		return this;
	}
	
	public void whenDifficultyToggle( Function<Boolean, Boolean> consumer ) {
		hardCheckBox.setEnabled(true);
		hardCheckBox.addActionListener( event -> {
			boolean result = consumer.apply( hardCheckBox.isSelected() );
			hardCheckBox.setSelected(result);
		});
	}
	
}
