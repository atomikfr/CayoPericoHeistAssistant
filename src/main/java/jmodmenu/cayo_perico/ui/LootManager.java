package jmodmenu.cayo_perico.ui;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import jmodmenu.I18n;

class LootManager {
	ComponentManager manager;
	JPanel panel;
	
	LootManager(JPanel panel) {
		manager = new ComponentManager(panel);
		manager.initLocation(1074 - 220, 10);
		this.panel = panel;
	}
	
	LootManager set(String lootName, String lootValue, String addLootValue) {
		manager.clear()
			.addBox( lootName, lootValue)
			.addBox( I18n.txt("menu.additional_loot"), addLootValue);
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
		checkBox.setLocation(665, 815);
		checkBox.setSize(390, 55);
		
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
