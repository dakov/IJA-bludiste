/**
 * Grafická reprezentace políčka v mapě.
 * Autoři: David Kovařík, Tomáš Bruckner
 */

package ija.client.gui.map;

import ija.client.gui.objects.*;
import ija.client.gui.humanoid.*;
import ija.server.map.Coords;
import ija.protocol.Protocol;
import java.awt.FlowLayout;
import javax.swing.JPanel;

public class XMapField extends JPanel {

    protected Coords coord;

    protected XMapObject obj;
    
    protected XHumanoid humanoid;
    
    protected XMap map;

    public XMapField(XMap map, Coords coord, char type) {

	this.map = map;
	this.coord = coord;

	switch (type) {

	    case Protocol.WALL:
		this.obj = new XWall();
		break;
	    case Protocol.CLOSED_GATE:
		this.obj = new XGate(false);
		break;
	    case Protocol.OPENED_GATE:
		this.obj = new XGate(true);
		break;
	    case Protocol.KEY:
		this.obj = new XKey(false);
		break;
	    case Protocol.DEST:
		this.obj = new XDest();
		break;

	    default:
		this.obj = new XPath();
		break;
	}
	this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); //aby nebyl zadny padding

	this.add(obj);
    }

    public Coords getCoords() {
	return coord;
    }
    
    /**
     * Položí libovolného humanoida na toto políčko
     * @param p 
     */
    void setHumanoid(XHumanoid p) {
	p.setCoords(coord);
	humanoid = p;
	
	removeAll();
	add(p);
	
	revalidate();
	repaint();
    }
    
    /**
     * Odebere humanoida z tohoto políčka.
     * @param p 
     */
    void unsetHumanoid(XHumanoid p) {
	p.setCoords(coord);
	humanoid = null;
	
	removeAll();
	
	add(obj);
	
	revalidate();
	repaint();
    }
    
    /**
     * @return Vrací instanci objektu, který je na tomto políčku umístěn.
     */
    public XMapObject getObject() {
	return obj;
    }
}
