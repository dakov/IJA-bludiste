/**
 * Objekt klíče. Je možné jej sebrat, čímž se hráči zvýší počet klíčů
 * Autoři: David Kovařík (xkovar66), Tomáš Bruckner (xbruck02)
 */

package ija.server.objects;

import ija.protocol.Protocol;

public class Key extends ija.server.objects.MapObject {
    
    protected boolean isTaken = false;
    
    public Key(String name) {
        super(name);
    }

    @Override
    public boolean canBeOpened() {
	return false;
    }

    @Override
    public boolean canSeize() {
	if(this.isTaken)
	    return true;
	
	return false;
    }

    @Override
    public boolean open() {
	return false;
    }
    
    
    @Override
    public boolean take() {
	
	if (this.isTaken)
	    return false;
	
	this.isTaken = true;	
	return true;
    }
    
    @Override
    public char repr(){
	
	if ( this.isTaken ) 
	    return Protocol.PATH;
	
	return Protocol.KEY;
    }
    
}
