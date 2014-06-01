/**
 * Soubor obsahuje implementaci orientovaných souřadnic v mapě. 
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.server.humanoid;

import ija.server.map.Coords;

/**
 * Orientovaná souřadnice je rozšířením standardních souřadnic mapě. Implementuje
 * také metody hashCode a equals, je tedy možné použít ji jako klíče v hash mapě.
 * Jedná se o trojici (řádek, sloupec, směr).
 */
public final class OrientedCoords extends Coords {

    int dir;

    public OrientedCoords(int row, int col, int dir) {
	super(row, col);
	this.dir = dir;
    }

    public OrientedCoords(Coords c,  int dir) {
	super(c.getRow(), c.getCol());
	this.dir = dir;
    }

    /**
     * Vrací hodnotu složky pro směr.
     * @return směr humanoida
     */
    public int getDir() {
	return dir;
    }
    
    
    @Override
    public int hashCode() {
	String map = "" + getRow() + ":" + getCol() + ":" + getDir();
	return map.hashCode();
	
    }

    @Override
    public boolean equals(Object obj) {
	
	if (obj instanceof OrientedCoords) {
            OrientedCoords tmp = (OrientedCoords) obj;
            
	    if (tmp.getRow() != getRow()) return false;
	    if (tmp.getCol() != getCol()) return false;
	    if (tmp.getDir() != getDir()) return false;
	    
	    return true;
        }
        return false;
    }

}
