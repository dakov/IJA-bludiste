/**
 * Soubor obsahuje implementaci hlídače. 
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.server.humanoid;

import ija.server.map.MapField;
import java.util.HashMap;

public class Guard extends Humanoid {
    
    public static final int STEP = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    
    public boolean lastLeft = false;
    
    private HashMap<OrientedCoords, Boolean> database = new HashMap<>();
    
    
    public Guard(int id, MapField f){
	this.id = id;
	this.field = f;
    }
    
    // vraci nulu pokud se pohnul OK, -1 pokud se nemohl pohonout
    // 2 pokud chytil
    /**
     * Operace "step" specificka pro hlídače. Liší se pouze návratovými kódy.
     * @return Vrací -1, pokud krok nelze provést, 0 pokud se krok provedl
     * vpořádku a 1 - pokud krok vedl k zabití hráče.
     */
    public int guardStep() {

	MapField f = this.getNextField();

	if (f == null) {
	    return -1;
	}
	
	if (f.isSeizedByPlayer()) {
	    return 1;
	}

	if (f.seize(this)) {

	    this.field.leave();

	    this.field = f;

	    return 0;
	}

	return -1;
    }
    /**
     * Vypočte, jaký směrem by se měl hlídač otočit.
     * @return Vrací true pokud by se měl otočit doleva, false pokud doprava
     */    
    public boolean getTurnDirection() {
	OrientedCoords c = getOrientedCoords();
	
	if (database.containsKey(c)) { //klic existuje, vrat a uloz zpatkyy jeho negovanou hodnotu
	    Boolean val = (Boolean) database.get(c);
	    database.put(c, !val);
	    
	    return !val;
	}
	
	// klic neexistuje -> uloz a vrat true
	database.put(c, true);
	return true;
    }
    
    @Override
    public char repr() {
	return 'x';
    }
    
    
    @Override
    public void turnRight() {
	lastLeft = false;
	super.turnRight();
    }

    /**
     * Otočí humanoida vlevo
     */
    @Override
    public void turnLeft() {
	lastLeft = true;
	super.turnLeft();
    }

    public boolean wasLeftTurn(){
	return lastLeft;
    }
  
    
    
}
