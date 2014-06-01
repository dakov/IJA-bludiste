/**
 * Společné vlastnosti a chování pro "živé" objekty v mapě.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client.gui.humanoid;

import ija.server.map.Coords;
import ija.protocol.Protocol;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class XHumanoid extends JLabel {
    
    protected Coords coords;
    protected int id;
    
    protected ImageIcon imgNorth;
    protected ImageIcon imgSouth;
    protected ImageIcon imgWest;
    protected ImageIcon imgEAST; 

    
    protected String NORTH_PATH = "north.png";    
    protected String SOUTH_PATH = "south.png";    
    protected String EAST_PATH  = "east.png";    
    protected String WEST_PATH  = "west.png";  
    
    private int direction;
    
    
    public XHumanoid(int id, Coords coord, int direction, String pathBase) {
	
	this.coords = coord;
	this.id = id;
	
	imgNorth = new ImageIcon(pathBase + "north.png");
	imgSouth = new ImageIcon(pathBase + "south.png");
	imgEAST = new ImageIcon(pathBase + "east.png");
	imgWest = new ImageIcon(pathBase + "west.png");
	
	setDirection(direction);
    }
    
    /**
     * Nastaví humanoidovi souřadnice.
     * @param coords Nové souřadnice.
     */
    public void setCoords(Coords coords) {
	this.coords = coords;
    }
    
    /**
     * @return Vrací polohu humanoida.
     */
    public Coords getCoords() {
	return coords;
    }
    
    /**
     * Nastaví orientaci hráče.
     * @param ndir Nová orientace hráče.
     */
    public final void setDirection(int ndir) {
	
	switch(ndir) {
	    case Protocol.NORTH: setIcon(imgNorth); break;
	    case Protocol.SOUTH: setIcon(imgSouth); break;
	    case Protocol.EAST: setIcon(imgEAST); break;
	    case Protocol.WEST: setIcon(imgWest); break;
	}
	
	direction = ndir;
	
    }
    
    /**
     * @return Vrací orientaci hráče.
     */
    public int getDirection() {
	return direction;
    }
    
}
