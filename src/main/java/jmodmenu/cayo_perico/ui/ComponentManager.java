package jmodmenu.cayo_perico.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import lombok.Getter;

public class ComponentManager {
	
	@Getter
	Style styles = new Style();

	List<Component> components = new LinkedList<>();
	int INIT_Y = 350;
	int x = 10;
	int y = INIT_Y;
	
	JPanel panel;
	
	public ComponentManager(JPanel panel) {
		this.panel = panel;
	}
	
	public void initLocation(int x, int y) {
		this.x = x;
		this.INIT_Y = this.y = y;
	}
	
	public JButton addButton(String title, Runnable action) {
		ComponentStyle style = styles.button;
		JButton button = new JButton(title);
		button.setForeground( style.foreground );
		button.setBackground( style.background );
		button.setBorder( new LineBorder(style.borderColor) );
		button.setLocation(x, y);
		button.setSize(200, 30);
		y += 30;
		button.addActionListener( event -> action.run() );
		add(button);
		return button;
	}
	
	private void add(Component component) {
		components.add(component);
		panel.add(component);
	}
	
	public JCheckBox addCheck(String text, boolean selected, Consumer<Boolean> action) {
		ComponentStyle style = styles.checkbox;
		JCheckBox checkBox = new JCheckBox(text, selected);
		checkBox.setIcon(new CheckBoxIcon(styles.checkbox));
		checkBox.setForeground( style.foreground );
		checkBox.setBackground( style.background );
		checkBox.setBorder( new LineBorder(style.borderColor) );
		checkBox.setLocation(x, y);
		checkBox.setSize(200, 30);
		y += 30;
		checkBox.addActionListener( event -> action.accept(checkBox.isSelected()) );
		add(checkBox);
		return checkBox;
	}
	
	public JTextField addField(String name, String value) {
		ComponentStyle style = styles.boxValue;
		JLabel label = new JLabel(name + " : ");
		label.setOpaque(true);
		label.setForeground( style.foreground );
		label.setBackground( style.background );
		label.setLocation(x, y);
		label.setSize(130, 30);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		add(label);
		int saveX = x;
		x += 130;
		
		JTextField field = new JTextField(value);
		field.setForeground( style.foreground );
		field.setBackground( style.background );
		field.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(2, 5, 2, 5),
				BorderFactory.createCompoundBorder(
					new LineBorder(style.borderColor),
					BorderFactory.createEmptyBorder(0, 5, 0, 5)
				)
			)
		);
		field.setLocation(x, y);
		field.setSize(70, 30);
		field.setHorizontalAlignment(SwingConstants.CENTER);
		add(field);
		x = saveX;
		y += 30;
		return field;
	}
	
	ComponentManager addBox(String title, String text) {
		ComponentStyle styleTitle = styles.boxTitle;
		JLabel label = new JLabel(title.toUpperCase());
		label.setOpaque(true);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setLocation(x, y);
		label.setSize(200, 30);
		label.setForeground(styleTitle.foreground);
		label.setBackground(styleTitle.background);
		label.setBorder( new LineBorder(styleTitle.borderColor) );
		y += 30;
		add(label);
		
		ComponentStyle styleContent = styles.boxValue;
		JLabel content = new JLabel(text);
		content.setOpaque(true);
		content.setHorizontalAlignment(SwingConstants.CENTER);
		content.setLocation(x, y);
		content.setSize(200, 45);
		content.setForeground(styleContent.foreground);
		content.setBackground(styleContent.background);
		content.setBorder( new LineBorder(styleContent.borderColor) );
		content.setFont( styleContent.font );
		y += 65;
		add(content);
		
		return this;
	}
	
	ComponentManager clear() {
		components.stream().forEach( component -> {
			component.setVisible(false);
			panel.remove(component);
		});
		components.clear();
		y = INIT_Y;
		return this;
	}

	public JLabel addLabel(String txt) {
		ComponentStyle style = styles.boxValue;
		JLabel label = new JLabel(txt);
		label.setOpaque(true);
		label.setForeground( style.foreground );
		label.setBackground( style.background );
		label.setLocation(x, y);
		label.setSize(200, 30);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		y += 30;
		add(label);
		return label;
	}
	
}

class CheckBoxIcon implements Icon {
	ComponentStyle style;
	CheckBoxIcon(ComponentStyle style) {
		this.style = style;
	}
    public void paintIcon(Component component, Graphics g, int x, int y) {
        AbstractButton abstractButton = (AbstractButton)component;
        ButtonModel buttonModel = abstractButton.getModel();

        if(buttonModel.isSelected()) {
        	g.setColor(style.borderColor);
        	g.fillRect(x+5, y, 13, 13);
        } else {
        	g.setColor(style.foreground);
        	g.drawRect(x+5, y, 13, 13);
        }
    }
    public int getIconWidth() {
        return 23;
    }
    public int getIconHeight() {
        return 13;
    }
}


class Style {
	ComponentStyle button;
	ComponentStyle checkbox;
	ComponentStyle boxTitle;
	ComponentStyle boxValue;
	
	Style() {
		button = new ComponentStyle();
		button.foreground = Color.WHITE; // new Color( 0xc73939 );
		button.background = new Color( 0x151111 ); // new Color( 0x3a1717 );
		button.borderColor = new Color( 0x802727 );
		
		checkbox = button;
		
		boxTitle = new ComponentStyle();
		boxTitle.foreground = Color.BLACK;
		boxTitle.background = new Color(0xde5151);
		boxTitle.borderColor = new Color(0xde5151);
		
		boxValue = new ComponentStyle();
		boxValue.foreground = Color.WHITE;
		boxValue.background = new Color( 0x151111 );
		boxValue.borderColor = boxTitle.borderColor;
		boxValue.font = new Font("verdana", Font.PLAIN, 20);
	}
}

class ComponentStyle {
	Color foreground;
	Color background;
	Color borderColor;
	Font font;
}

