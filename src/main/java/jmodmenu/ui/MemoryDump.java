package jmodmenu.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MemoryDump {
	
	
	JTextArea memory;
	JTextField address;
	JComboBox<String> cb;
	
	public void init() {
		JFrame frame = new JFrame("MemoryDump");
		
		JPanel panel = new JPanel( new BorderLayout() );
		
		JPanel topPanel = new JPanel( new BorderLayout() );
		address = new JTextField();
		address.addActionListener( action -> {
			String adr = address.getText();
			if ( adr.length() == 0 ) return;
			String realAdr = "";
			for (int i = adr.length()-1; i >= 0; i--) {
				if ( adr.charAt(i) != ' ' ) realAdr += adr.charAt(i);
			}
			long addr = Long.decode("0x"+realAdr);
			memory.setText( memory.getText() + "\n" + addr );
		} );
		cb = new JComboBox<>();
		cb.setPreferredSize(new Dimension(150, -1));
		topPanel.add(address, BorderLayout.CENTER);
		topPanel.add(cb, BorderLayout.EAST);
		panel.add(topPanel, BorderLayout.NORTH);
		
		memory = new JTextArea();
		memory.setEditable(false);
		memory.setFont( new Font("courier", Font.PLAIN, 12) );
		memory.setText("Test text");
		panel.add(memory, BorderLayout.CENTER);
		
		frame.getContentPane().add(panel);
		frame.setSize(800, 400);
		frame.setLocation(100, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		MemoryDump memoryDump = new MemoryDump();
		SwingUtilities.invokeLater( memoryDump::init );
	}

}
