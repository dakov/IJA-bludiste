/**
 * Grafická reprezentace hlídače v mapě.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client.gui.humanoid;

import ija.server.map.Coords;
import ija.protocol.Protocol;

public class XGuard extends XHumanoid {
    
    private static final String BASE = "dest-client/res/humanoids/guard/";

    
    public XGuard(int id, Coords coord, int direction) {
	super(id, coord, direction, BASE);
    }
    
    public XGuard(int id, Coords coord) {
	this(id, coord, Protocol.NORTH);
    }
    
}
