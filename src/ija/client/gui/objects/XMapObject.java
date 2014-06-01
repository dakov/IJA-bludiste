/**
 * Společná implementace pro všechny "neživé" objekty v mapě.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client.gui.objects;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public abstract class XMapObject extends JLabel {
    
    public XMapObject(ImageIcon img) {
	super(img);
    }
    
    public XMapObject() {
	super();
    }
    
    // Tady uz nemaji objekty zadnou logiku, takze klidne muze mit kazdy metody
    // proto je lepsi je mit, kdyby server poslal nekonzistentni data, nebude
    // pokus napr. o otevreni zdi nijak zasadni
    
    
    /**
     * Otevře objekt. Je na konkrétních třídách, aby si chování přepsaly, implicitně
     * se jedná o prázdnou operaci -> objekt nejde otevřít, ale pokus o jeho otevření
     * nezpůsobí chybu.
     */
    public void open() {
	
    }
    
     /**
     * Sebere objekt. Je na konkrétních třídách, aby si chování přepsaly, implicitně
     * se jedná o prázdnou operaci -> objekt nelze sebrat, ale pokus o jeho sebrání
     * nezpůsobí chybu.
     */
    public void take() {
	
    }
    
}
