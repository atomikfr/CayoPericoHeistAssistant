package jmodmenu.cayo_perico.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import jmodmenu.I18n;
import jmodmenu.cayo_perico.model.LootType;
import jmodmenu.cayo_perico.model.SecondaryLoot;

class MenuManager {
	JPanel panel;
	int fieldId;
	
	ComponentManager manager;
	MenuManager(JPanel panel) {
		manager = new ComponentManager(panel);
		manager.initLocation(10, 350);
		this.panel = panel;
	}

	public MenuManager addSave(Runnable action) {
		manager.y += 10;
		manager.addButton(I18n.txt("menu.save"), action);
		return this;
	}

	MenuManager checkIndexItems(List<String> itemsConf, int value, Consumer<Integer> action) {
		if ( itemsConf.size() == 0 ) return this;
		final List<JCheckBox> boxes = new ArrayList<>();
		
		manager.y += 10;
		IntStream.range(0, itemsConf.size()).forEach( (idx) -> {
			String name = itemsConf.get(idx);
			AtomicReference<JCheckBox> boxReference = new AtomicReference<>();
			JCheckBox box = manager.addCheck(name.toUpperCase(), idx == value, b -> {
				if ( !b ) {
					boxReference.get().setSelected(true);
					panel.repaint();
				} else {
					for( JCheckBox other : boxes ) {
						if ( other == boxReference.get() ) continue;
						other.setSelected(false);
					}
					panel.repaint();
					action.accept(idx);
				}
			});
			boxReference.set(box);
			boxes.add(box);
		});
		panel.repaint();
		return this;
	}

	MenuManager checkMaskItems(Map<String, Integer> itemsConf, int value, Function<Integer, Consumer<Boolean>> actionBuilder) {
		return checkMaskItems(itemsConf, value, actionBuilder, null);
	}
	
	MenuManager checkMaskItems(Map<String, Integer> itemsConf, int value, Function<Integer, Consumer<Boolean>> actionBuilder, Consumer<JCheckBox> checkBoxCb) {
		itemsConf.forEach( (name, mask) -> {
			boolean selected = (value & mask) == mask; 
			JCheckBox box = manager.addCheck(name.toUpperCase(), selected, actionBuilder.apply(mask));
			if (checkBoxCb != null) checkBoxCb.accept(box);
		});
		panel.repaint();
		return this;
	}

	MenuManager addSubMenu(String title, Runnable action) {
		manager.y += 10;
		manager.addButton( title.toUpperCase() + "  >>", action );
		return this;
	}
	
	MenuManager addAction(String title, Runnable action) {
		manager.y += 10;
		manager.addButton( title.toUpperCase(), action );
		return this;
	}
	
	MenuManager addFields(Map<String, ?> itemsConf, List<Field> fields) {
		manager.y += 10;
		
		itemsConf
			.entrySet()
			.stream()
			.map( e -> {
				JTextField jTextField = manager.addField(e.getKey(), e.getValue().toString());
				Field field = new Field();
				field.component = jTextField;
				field.name = e.getKey();
				field.initialValue = e.getValue().toString();
				field.id = fieldId++;
				return field;
			})
			.forEach(fields::add);
		
		return this;
	}
	
	MenuManager backTo(Runnable action) {
		manager.addButton( "<< "+I18n.txt("menu.back"), action );
		return this;
	}
	
	MenuManager clear() {
		manager.clear();
		manager.x = 10;
		fieldId = 0;
		return this;
	}

	public MenuManager addSpacer() {		
		manager.y += 10;
		return this;
	}
	public MenuManager addLabel(String txt) {
		manager.addLabel(txt);
		return this;
	}
	
	public MenuManager addChooserHeader() {
		Arrays.asList("", 
			I18n.txt("loot_small.cash"),
			I18n.txt("loot_small.weed"), 
			I18n.txt("loot_small.cocaine"), 
			I18n.txt("loot_small.gold")
		)
		.stream()
		.forEach( txt -> {
			JLabel lbl = manager.addLabel(txt);
			lbl.setSize(40, 30);
			lbl.setHorizontalAlignment(SwingConstants.LEFT);
			manager.x += 40;
			manager.y -= 30;
		});
		manager.x = 10;
		manager.y += 30;
		return this;
	}

	public MenuManager addLootChooser(int cpt, int mask, List<? extends SecondaryLoot> allLoots, LootType[] types, BiConsumer<LootType, Boolean> callbackAction) {
		if ( mask == 0 ) throw new IllegalArgumentException("mask must be > 0");
		final List<JCheckBox> boxes = new ArrayList<>();
		
		JLabel lbl = manager.addLabel(""+cpt);
		lbl.setSize(40, 30);
		manager.x += 40;
		manager.y -= 30;
		
		int j = 0;
		int maskCopy = mask;
		for (j = 0; j < 32; j++, maskCopy = maskCopy >> 1) {
			if ( (maskCopy & 1) > 0 ) break;
		}
		final int idx = j;
		
		Function<LootType, Boolean> hasLootAt = (type) -> allLoots.stream()
			.filter( loot -> loot.getIdx() == idx )
			.filter( loot -> loot.getType() == type )
			.findAny()
			.isPresent();
		
		for(LootType type : types) {
			final LootChooserEntry entry = new LootChooserEntry();
			entry.cpt = cpt;
			entry.type = type;
			entry.box = manager.addCheck("", hasLootAt.apply(type), b -> {
				if ( !b ) {
					// entry.box.setSelected(true);
					callbackAction.accept(entry.type, b);
					panel.repaint();
				} else {
					for( JCheckBox other : boxes ) {
						if ( other == entry.box ) continue;
						other.setSelected(false);
					}
					callbackAction.accept(entry.type, b);
					panel.repaint();
				}
			});
			entry.box.setSize(40, 30);
			manager.x += 40;
			manager.y -= 30;
			boxes.add(entry.box);
		}
		manager.x = 10;
		manager.y += 30;
		return this;
	}
	
	public MenuManager addNavBar(Runnable prevCallback, Runnable nextCallback) {
		JButton btn1 = manager.addButton("< prec", prevCallback);
		btn1.setSize(80, 30);
		manager.x += 80;
		manager.y -=30;
		JLabel lbl = manager.addLabel("");
		lbl.setSize(40, 30);
		manager.x += 40;
		manager.y -= 30;
		JButton btn2 = manager.addButton("suiv >", nextCallback);
		btn2.setSize(80, 30);
		manager.x = 10;
		return this;
	}

	public MenuManager addPaintLocation(Integer cpt, String place, boolean selected, BiConsumer<Integer, Boolean> togglePaintCallback) {
		JLabel lbl = manager.addLabel(""+cpt);
		lbl.setSize(40, 30);
		manager.x += 40;
		manager.y -= 30;
		
		JCheckBox box = manager.addCheck(place, selected, b -> togglePaintCallback.accept(cpt, b) );
		box.setSize(160, 30);
		manager.x = 10;

		return this;
	}
}

class LootChooserEntry {
	JCheckBox box;
	LootType type;
	int cpt;
}

class Field {
	int id;
	JTextField component;
	String name;
	String initialValue;
	
	boolean hasChanged() {
		return !component.getText().equals(initialValue);
	}
	String value() {
		return component.getText();
	}
	int intValue() {
		if ( value().isEmpty() ) return 0;
		
		try {
			return Integer.parseInt(value().trim());
		} catch(NumberFormatException e) {
			return 0;
		}
	}
	public void set(String value) {
		component.setText(value);
	}
}
