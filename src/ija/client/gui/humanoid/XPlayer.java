/**
 * Grafická reprezentace hráče v mapě.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client.gui.humanoid;

import ija.server.map.Coords;
import ija.protocol.Protocol;

public class XPlayer extends XHumanoid {
    
    private static final String BASE = "dest-client/res/humanoids/player";    
    
    
    public XPlayer(int id, Coords coord, int direction) {
	super(id, coord, direction, BASE + id + "/");
    }
    
    public XPlayer(int id, Coords coord) {
	this(id, coord, Protocol.NORTH);
    }
    
    
}
