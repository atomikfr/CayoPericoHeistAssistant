package jmodmenu.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BdnBuildTime extends JPanel {
	
	int[] timePerProduction = new int[7];
	List<JTextField> textFields = new ArrayList<>(8);
	BiConsumer<Integer, Integer> listener;
	
	public BdnBuildTime() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		Dimension preferred = new Dimension(150, 25);
		for (int i = 0; i < 7; i++) {
			JTextField field = new JTextField();
			field.setPreferredSize(preferred);
			field.addActionListener(this::fireAction);
			textFields.add( field );
			add( field );
		}
	}
	
	private void fireAction(ActionEvent event) {
		if ( listener != null ) {
			int i = textFields.indexOf( event.getSource() );
			if ( i < 0 ) {
				System.err.println("action origin not found :(");
				return;
			}
			listener.accept(i, timePerProduction[i]);
		}
	}
	
	public Builder builder() {
		return new Builder();
	}
	
	public class Builder {
		private Builder() {};
		int i = 0;
		public void addTime(int time) {
			textFields.get(i).setText(""+time);
			timePerProduction[i++] = time;
			// repaint();
		}
	}
	
	public void whenAction( BiConsumer<Integer, Integer> listener ) {
		this.listener = listener;
	}
	

}
