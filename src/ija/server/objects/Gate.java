/**
 * Objekt brány. Brána může být zavřená nebo otevřená. Pro otevření je potřeba
 * klíč. Jednou otevřená brána zůstává otevřená.
 * 
 * Autoři: David Kovařík (xkovar66), Tomáš Bruckner (xbruck02)
 */

package ija.server.objects;

import ija.server.map.MapField;
import ija.protocol.Protocol;

public class Gate extends ija.server.objects.MapObject {
    
    private boolean isOpened = false;
    
    public Gate(String name) {
        super(name);
    }
    
    @Override
    public boolean canBeOpened() {
	
        if (this.isOpened) {
            return false;
	}
	
        return true;
    }
    
    @Override
    public boolean open() {
	
        if (this.isOpened) {
            return false;
        }

        this.isOpened = true;
        return true;
    }
    
    @Override
    public boolean canSeize() {
        if (this.isOpened) {
            return true;
        }

        return false;
    }
    
    @Override
    public boolean equals(Object obj) {
        
        if (obj instanceof Gate) {
            Gate tmp = (Gate) obj;
            return this.name.equals(tmp.name)  && this.isOpened == tmp.isOpened;
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        return this.name.hashCode();
    }
    
    @Override
    public char repr(){
	
	if ( this.isOpened ) 
	    return Protocol.OPENED_GATE;
	
	return Protocol.CLOSED_GATE;
    }

    
}

