/**
 * Panel pro zobrazení statistiky hry
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GameStatsPanel extends JPanel {
    
    private JTextField delayField;
    private JTextField playersField;
    private JTextField guardsField;
    
    public GameStatsPanel() {
	super();
	
	initComponents();
    }

    private void initComponents() {
	
	JPanel statsPanel = new JPanel();
	
	JPanel labelPanel = new JPanel(new GridLayout(3, 1, 5, 5));
	
	labelPanel.add(new JLabel("Hráčů ve hře"));
	labelPanel.add(new JLabel("Prodleva"));
	labelPanel.add(new JLabel("Strážců"));
	
	JPanel fieldPanel = new JPanel(new GridLayout(3,1,5,5));
	
	
	playersField = new JTextField(10);
	playersField.setEnabled(false);
	fieldPanel.add(playersField);

	delayField = new JTextField(10);
	delayField.setEnabled(false);
	fieldPanel.add(delayField);
	
	guardsField = new JTextField(10);
	guardsField.setEnabled(false);
	fieldPanel.add(guardsField);
	
	statsPanel.add(labelPanel);
	statsPanel.add(fieldPanel);
	
	add(statsPanel, BorderLayout.CENTER);
    }
    
    /**
     * Zobrazí na panelu zadané hodnoty
     * @param delay Časový interval jednoho kroku
     * @param players Počet hráčů ve hře
     * @param guards Počet hlídačů
     */
    public void display(String delay, String players, String guards) {
	delayField.setText(delay);
	playersField.setText(players);
	guardsField.setText(guards);
    }
    
    /**
     * Zobrazí na panelu zadané hodnoty.
     * @param format Hodnoty ve tvaru hraci;delay;hlidaci
     */
    public void display(String format) {
	String[] parsed = format.split(";");
	
	String delay = parsed[2];
	
	if (delay.length() > 3) {
	    delay = delay.substring(0,2);
	}
	
	playersField.setText(parsed[1]);
	delayField.setText(delay);
	guardsField.setText(parsed[3]);
	
    }
    
    
}
