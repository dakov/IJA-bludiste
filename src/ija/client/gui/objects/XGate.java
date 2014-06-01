/**
 * Grafická reprezentace brány v mapě.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client.gui.objects;

import javax.swing.ImageIcon;

public class XGate extends XMapObject {
    
    private boolean opened;
    
    private final ImageIcon IMG_CLOSED = new ImageIcon("dest-client/res/objects/gate-closed.png");
    private final ImageIcon IMG_OPENED = new ImageIcon("dest-client/res/objects/gate-opened.png");
    
    public XGate(boolean opened) {
	
	super();
	
	this.opened = opened;
	
	if (opened) {
	    setIcon(IMG_OPENED);
	} else{
	    setIcon(IMG_CLOSED);
	}
	
    }
    
    @Override
    public void open() {
	opened = true;
	setIcon(IMG_OPENED);
    }
    
}
