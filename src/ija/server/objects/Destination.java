/**
 * Objekt cíle. Chová se jako obyčejná cesta s tím rozdílem, že pokud libovolný hráč
 * vstoupí na cílové pole, nastal konec hry a hráč vyhrává.
 * 
 * Autoři: David Kovařík (xkovar66), Tomáš Bruckner (xbruck02)
 */

package ija.server.objects;

import ija.protocol.Protocol;

public class Destination extends ija.server.objects.MapObject {

    public Destination(String name) {
	super(name);
    }

    @Override
    public boolean canBeOpened() {
	return false;
    }

    @Override
    public boolean canSeize() {
	return true;
    }

    @Override
    public boolean open() {
	return false;
    }
    
    
    @Override
    public char repr(){
	return Protocol.DEST;
    }
    
}
