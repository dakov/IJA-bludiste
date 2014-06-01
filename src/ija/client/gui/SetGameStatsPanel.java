/**
 * Panel pro nastavení atributů nové hry.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client.gui;

import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class SetGameStatsPanel extends JPanel {
    
    private JComboBox<String> mapNameField;
    private JSpinner delayField;
    private JSpinner guardsField;
    
    private String[] maps;
    
    // mformat = format map
    public SetGameStatsPanel(String mformat){
	super();
	
	maps = mformat.split(";");
	
	initComponents();
    }

    private void initComponents() {
	
	JPanel labelPanel = new JPanel(new GridLayout(4,1,5,10));
	JPanel fieldPanel = new JPanel(new GridLayout(4,1,5,5));
	
	labelPanel.add(new JLabel("Název mapy"));
	labelPanel.add(new JLabel("Prodleva"));
	labelPanel.add(new JLabel("Počet hlídačů"));
	
	
	mapNameField = new JComboBox<>(maps);
	fieldPanel.add(mapNameField);
	
	delayField = new JSpinner(new SpinnerNumberModel(0.5, 0.5, 5, 0.1));
	fieldPanel.add(delayField);
	
	guardsField = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1));
	fieldPanel.add(guardsField);
	
	add(labelPanel);
	add(fieldPanel);
    }

    /**
     * @return Hodnota jména hry.
     */
    public String getNameValue() {
	int index = mapNameField.getSelectedIndex();
	
	if (index < maps.length)
	    return maps[index];
	
	return null;
    }

    /**
     * @return Hodnotu zpoždění jednoho kroku.
     */
    public double getDelayValue() {
	return Double.valueOf("" + delayField.getValue());
    }

    /**
     * @return Vrací nastavený počet hlídačů.
     */
    public int getGuardCountValue() {
	return Integer.valueOf("" + guardsField.getValue());
    }
    
}
