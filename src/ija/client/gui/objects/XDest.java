/**
 * Grafická reprezentace cílu v mapě.
 * Autoři: David Kovařík, Tomáš Bruckner
 */

package ija.client.gui.objects;

import javax.swing.ImageIcon;

public class XDest extends XMapObject {
    
    public final ImageIcon WALL_IMG = new ImageIcon("dest-client/res/objects/dest.png");

    public XDest() {
	super();
	setIcon(WALL_IMG);
    }

}
