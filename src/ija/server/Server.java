/**
 * Soubor obsahuje implementaci části serveru. 
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.server;

import ija.server.game.Game;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    /* komunikacni atributy */
    private ServerSocket servsock;

    /* atributy souvisejici se hrou */
    private ArrayList<Game> games = new ArrayList<Game>();

    /**
     * Počítadlo pro generování identifikátorů
     */
    private static int gameIdSequence = 0;
    private static int playerIdSequence = 0;

    public Server(int port) throws IOException {

	this.servsock = new ServerSocket(port);

    }

    public int nextGameId() {
	return Server.gameIdSequence++;
    }

   /* 
    public int nextPlayerId() {
	return Server.playerIdSequence++;
    }
    */
    
    public void start() throws IOException {
	System.out.println(" [Server ] Started. ");

	/* --------- zde bude nekonecny cyklus a "fork" za accpet ---------- */
	while (true) {
	    Socket sock = servsock.accept();

	    (new ClientThread(sock, this)).start();
	}
	
	//servsock.close();
    }


    public static void main(String[] args) {

	try {
	    Server serv = new Server(1337);

	    serv.start();

	} catch (IOException ex) {
	    System.err.println("[ Error ] Server failed. ");
	    System.err.println("[  ...  ] " + ex.getMessage());
	}

    }

    public void addGame(Game game) {
	this.games.add(game);
    }

    public ArrayList<Game> getGames() {
	return this.games;
    }

    Game getGame(int id) {
	
	
	for (Game game: games) {
	    
	    if (game.id() == id )
		return game;
	}
	
	
	return null;
    }

    /**
     * Odstraní hru ze seznamu rozehraných her.
     * @param gameId 
     */
    public void removeGame(int gameId) {
	
	for (int i=0; i < games.size(); ++i) {
	    
	    if (games.get(i).id() ==  gameId) {
		games.remove(i);
	    }
	}
	
	// jinak prazdna operace
    }


}
