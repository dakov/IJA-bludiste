/**
 * Grafická reprezentace zeď v mapě.
 * Autoři: David Kovařík, Tomáš Bruckner
 */

package ija.client.gui.objects;

import javax.swing.ImageIcon;

public class XWall extends XMapObject {
    
    public final ImageIcon IMG_WALL = new ImageIcon("dest-client/res/objects/wall.png");

    public XWall() {
	super();
	setIcon(IMG_WALL);
    }

}
