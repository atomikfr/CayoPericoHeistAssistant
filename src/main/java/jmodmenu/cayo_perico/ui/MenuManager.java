package jmodmenu.cayo_perico.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jmodmenu.I18n;

class MenuManager {
	JPanel panel;
	
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
		IntStream.range(0, itemsConf.size() - 1).forEach( (idx) -> {
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
		manager.y += 10;
		itemsConf.forEach( (name, mask) -> {
			boolean selected = (value & mask) == mask; 
			manager.addCheck(name.toUpperCase(), selected, actionBuilder.apply(mask));
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
	
	MenuManager addFields(Map<String, Integer> itemsConf, Consumer<List<JTextField>> fieldsCallback) {
		manager.y += 10;
		
		List<JTextField> fields = itemsConf
			.entrySet()
			.stream()
			.map( e -> manager.addField(e.getKey(), e.getValue().toString()) )
			.collect(Collectors.toList());
		
		if (fieldsCallback != null) fieldsCallback.accept( fields );
		
		return this;
	}
	
	MenuManager backTo(Runnable action) {
		manager.addButton( "<< "+I18n.txt("menu.back"), action );
		return this;
	}
	
	MenuManager clear() {
		manager.clear();
		return this;
	}
}