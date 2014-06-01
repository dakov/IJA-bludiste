/**
 * Obsahuje definici hráče v mapě. Oproti jiným humanoidům obsahuje některé dodatečné metody.
 * 
 * Autoři: David Kovařík (xkovar66), Tomáš Bruckner (xbruck02)
 */
package ija.server.humanoid;

import ija.server.map.MapField;
import ija.protocol.Protocol;
import java.io.PrintWriter;
import java.util.Calendar;

public class Player extends Humanoid {
    
    private int keys = 0; 
    private boolean isAlive = true;
    
    Calendar cal = Calendar.getInstance();
    
    
    long startTime;
    long endTime;
    
    int steps = 0;
    
    
    /** Socket na kterém poslouchá klient reprezentující tohoto hráče */
    private PrintWriter sock;
    
    public Player(int id, PrintWriter sock, MapField f){
	this.id = id;
	this.field = f;
	
	startTime = Calendar.getInstance().getTimeInMillis();
	
	this.sock = sock;
    }

    public int getKeys() {
	return this.keys;
    }
    
    public MapField getField() {
	return this.field;
    }
    
    public void addKey() {
	this.keys++;
    }
    
    public void removeKey() {
	this.keys--;
    }
    
    public void send(String msg) {
	sock.println(msg);
    }
    
    public boolean open() {
	
	MapField f = this.getNextField();
	
	if ( f == null ) {
	    return false;
	}
	
	boolean code = false;
	
	if (this.keys > 0)
	   code = f.open();
	
	if (code)
	    this.removeKey();
	
	return code;
    }
    
    public boolean take() {
	
	MapField f = this.getNextField();
	
	if ( f == null ) {
	    return false;
	}
	
	boolean code;
	
	code = f.take();
	
	if (code)
	    this.addKey();
	
	return code;
    }
    
    public void kill() {
	this.isAlive = false;
    }
    
    public boolean isAlive() {
	return isAlive;
    }
    
    public int keys() {
	return this.keys;
    }
    
    public long getPlayTime() {
	
	if (endTime == 0) {
	    endTime =  Calendar.getInstance().getTimeInMillis();
	}
	
	
	return endTime - startTime;
    }
    
    public void incSteps() {
	steps++;
    }
    
    public int getSteps() {
	return steps;
    }
    
    @Override
    public MapField step() {
	
	incSteps();
	return super.step();
    }

    @Override
    public char repr() {
	char ch;
	
	switch( this.getDirection() ){
	    case Protocol.NORTH: ch = '^'; break;
	    case Protocol.SOUTH: ch = 'v'; break;
	    case Protocol.EAST: ch = '>'; break;
	    case Protocol.WEST: ch = '<'; break;
	    default: ch = '?'; break;
	}
	
	return ch;
    }
    

    
}
