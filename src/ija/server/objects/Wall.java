/**
 * Autoři: David Kovařík (xkovar66), Tomáš Bruckner (xbruck02)
 */
package ija.server.objects;

import ija.protocol.Protocol;

public class Wall extends ija.server.objects.MapObject	 {
    
    public Wall(String name) {
        super(name);
    }

    @Override
    public boolean canBeOpened() {
	return false;
    }
    
    @Override
    public boolean canSeize() {
        return false;
    }

    @Override
    public boolean open() {
        return false;
    }
    
    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Wall) {
            Wall tmp = (Wall) obj;
            return this.name.equals(tmp.name);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public char repr(){
	return Protocol.WALL;
    }
    
}