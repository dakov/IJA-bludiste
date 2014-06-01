/**
 * Grafická reprezentace klíče v mapě.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client.gui.objects;

import javax.swing.ImageIcon;

public class XKey extends XMapObject {
    
    private boolean taken;
    
    private final ImageIcon IMG_PRESENT = new ImageIcon("dest-client/res/objects/key.png");
    private final ImageIcon IMG_TAKEN   = new ImageIcon("dest-client/res/objects/field.png");
    
    public XKey(boolean taken) {
	
	super();
	
	this.taken = taken;
	
	if (taken) {
	    setIcon(IMG_TAKEN);
	} else{
	    setIcon(IMG_PRESENT);
	}
	
    }
    
    @Override
    public void take() {
	taken = true;
	setIcon(IMG_TAKEN);
    }
    
}
