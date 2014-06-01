/**
 * Obsahuje definici políčka mapy.
 * 
 * Autoři: David Kovařík (xkovar66), Tomáš Bruckner(xbruck02)
 */
package ija.server.map;

import ija.server.objects.Key;
import ija.server.objects.MapObject;
import ija.server.objects.Wall;
import ija.server.objects.Gate;
import ija.server.objects.Destination;
import ija.server.humanoid.Humanoid;
import ija.server.humanoid.Player;
import ija.protocol.Protocol;

public class MapField {

    protected Coords coord;
    
    /** Objekt umístěný na tomto poli */
    protected MapObject obj;
    
    /** Humanoid, který právě obsazuje toto pole */
    protected Humanoid humanoid;
    
    /** Mapa, které toto pole přísluší */
    protected Map map;
    

    public MapField(Map map, Coords coord, char type) {

	this.map = map;
	this.coord = coord;
	
	switch (type) {
	    case Protocol.WALL:
		this.obj = new Wall("Wall" + coord);
		break;
	    case Protocol.CLOSED_GATE:
		this.obj = new Gate("Gate" + coord);
		break;
	    case Protocol.KEY:
		this.obj = new Key("Key" + coord);
		break;
	    case Protocol.DEST:
		this.obj = new Destination("Dest" + coord);
		break;
	}
    }

    /**
     * Obsazující humanoid poustil toto pole
     */
    public void leave() {
	this.humanoid = null;
    }

    /**
     * Test, zda může být objekt na tomto poli otevřen.
     * @return True, pokud může být objekt otevřen, jinak false.
     */
    public boolean canBeOpen() {
	if (this.obj != null) {
	    return this.obj.canBeOpened();
	}
	return false;
    }
    
    public boolean open() {

	if (this.canBeOpen()) {
	    return this.obj.open();
	}

	return false;
    }

    /**
     * Pokusí se sebrat objekt ležící na tomto poli
     * @return True, pokud se podařilo objekt sebrat, jinak false.
     */
    public boolean take() {

	if (this.obj != null) {
	    return this.obj.take();
	}

	return false;
    }
    
    /**
     * Vytvoří znakovou reprezentaci tohoto pole. Pokud je pole obsazeno humanoidem
     * má přednost ve vykreslení, jinak pokud na poli leží objekt, je vykreslen
     * tento objekt, jinak je pole cestou.
     * @return Znak představující reprezentaci objektu na políčku
     */
    public char getRepresentation() {
	char repr = '.';

	if (this.humanoid != null) { // pokud je na policku humanoid, ma prednost

	    return this.humanoid.repr();
	}

	if (this.obj == null) {
	    return Protocol.PATH;
	}

	return this.obj.repr();

    }
    
    public char getObjectRepresentation() {
	
	if (this.obj == null) {
	    return Protocol.PATH;
	}

	return this.obj.repr();
    }

    /**
     * Zjistí, zda je pole cestou - tedy na poli neleží žádný objekt.
     * @return True, pokud je pole cestou, jinak false.
     */
    public boolean isPath() {
	if (this.obj == null) {
	    return true;
	}

	return false;
    }

    public boolean seize(Humanoid head) {

	if (this.canSeize()) {
	    this.humanoid = head;
	    return true;
	}

	return false;
    }

    public boolean canSeize() {

	if (this.humanoid != null) {
	    return false;
	}

	if (this.obj != null) {
	    return this.obj.canSeize();
	}

	return true;
    }

    /**
     * @return Vrací souřadnice tohoto pole
     */
    public Coords getCoords() {
	return coord;
    }

    /**
     * Nalezene a vrátí instanci pole v relativní poloze vůči tomuto poli.
     * @param drow 
     * @param dcol
     * @return Instanci nalezeného pole.
     */
    public MapField getNeighbour(int drow, int dcol) {
	Coords c = this.getCoords().add(drow, dcol);

	return this.map.fieldAt(c);
    }
    
    /**
     * Test, zda je pole cílovým polem. Tedy zda na něm leží objekt cíle.
     * @return True, pokud je pole cílové, jinak false.
     */
    public boolean isDestination() {
	return this.obj instanceof Destination;
    }

    /**
     * Kontrola, zda je políčko obsazeno hráčem (ne hlídačem).
     * @return True/False
     */
    public boolean isSeizedByPlayer() {
	
	return humanoid != null && humanoid instanceof Player;
    }
    
    public Humanoid getHumanoid(){
	return humanoid;
    }

}
