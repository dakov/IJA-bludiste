/**
 * Implemetace obsluhy hlídačů.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.server;

import ija.server.game.Game;
import ija.server.humanoid.Guard;

/**
 * Třída reprezentuje samostatné vlákno obsluhující všechny hlídače v konkrétní
 * hře.
 */
public class GuardsThread extends Thread {
    
    Game game;

    public GuardsThread(Game game) {
	this.game = game;
    }

    @Override
    public void run() {
	
	long ldelay = Math.round(game.getDelay() * 1000);

	while (game.isActive()) {
	    
	    
	    for (Guard g : game.getGuards() ) {
		
		game.moveGuard(g);
	    }
	    

	    try {
		sleep(ldelay);
	    } catch (InterruptedException ex) {
		break;
	    }
	}

    }

}
