/**
 * Grafická reprezentace cestu v mapě.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client.gui.objects;

import javax.swing.ImageIcon;

public class XPath extends XMapObject {
    public final ImageIcon IMG_PATH = new ImageIcon("dest-client/res/objects/field.png");

    public XPath() {
	super();
	setIcon(IMG_PATH);
    }

}
