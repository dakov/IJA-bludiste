/**
 * Obsluha požadavků jednoho klienta.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.server;

import ija.server.game.Game;
import ija.server.humanoid.Player;
import ija.server.map.Coords;
import ija.server.map.MapProcessingException;
import ija.protocol.Protocol;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Třída provádí obsluhu klientských požadavků v samostatném vlákně.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
class ClientThread extends Thread {
    
    /**
     * Hřáč je identifikován socketem, přes který komunikuje.
     */
    Socket sock;
    Server server;

    private PrintWriter pwout;
    private BufferedReader pwin;

    /** Instance hrané hry */
    private Game game;

    /** Vlákno obsluhující příkaz GO */
    private Thread goThread;

    private int gameId;
    private int playerId;

    public ClientThread(Socket sock, Server server) {

	this.sock = sock;
	this.server = server;

    }

    @Override
    public void run() {

	try {
	    print("Accepted " + sock);

	    InputStream istream = sock.getInputStream();
	    OutputStream ostream = sock.getOutputStream();

	    pwout = new PrintWriter(ostream, true);
	    pwin = new BufferedReader(new InputStreamReader(istream));


	    /* ------------------ POSLE SEZNAM HER ----------------- */
	    String message = createGameList(); //vytvori seznam her
	    send(message); //posle seznam her
	    
	    /* ------------------ POSLE SEZNAM MAP ----------------- */
	    
	    message =  createMapList();
	    send(message);
	    
	    
	    /* ------------- */
	    int newId = server.nextGameId();
	    int desiredGame;

	    // vytvareni / vyber hry musi byt v cyklu protoze uzivatel muze chtit
	    // vytvorit nekolik neexistujicich map -> byla by zvlastni mu to nedovolit vicekrat
	    while (true) {
		// zpracuje odpoved => pripoji hrace k existujici / zalozi novou
		message = readline();

		if (message == null) { // tusim ze v pripade nasilneho ukonceni
		    error("No game selected");
		    return;
		}

		if (message.startsWith(Protocol.CREATE_GAME_REQUEST)) {
		    String msg = message.substring(Protocol.CREATE_GAME_REQUEST.length() + 1);
		    String[] args = msg.split(" ");

		    try {
			Game game = new Game(newId, args[0], Double.valueOf(args[1]), Integer.valueOf(args[2]));

			server.getGames().add(game);

			send(Protocol.CREATE_GAME_SUCCESS + " " + newId);

		    } catch (MapProcessingException ex) {
			error(ex.getMessage());

			send(Protocol.CREATE_GAME_FAILURE);
			continue;
		    }

		    desiredGame = newId;

		    // Jelikoz jsem v ramci vytvareni hry provedl o jeden read navic,
		    // musim to pred dalsim behem programu dorovnat -> dalsi readline()
		    readline();

		    break;

		}
		// TODO: potreba testovat na obsazenost hry!!

		// pokud nezada o vytvoreni nove mapy, ale vybira existujici,
		// tak rovnou skonci
		desiredGame = Integer.valueOf(message.substring(Protocol.JOIN_GAME_REQUEST.length() + 1));
		break;

	    }

	    gameId = desiredGame;

	    game = server.getGame(desiredGame);

	    if (game == null) {
		throw new RuntimeException("TODO: predelat - neexistujici hra");
	    }

	    /* ---------------- VYGENERUJE UZIVATELI ID ------------- */
	    // hraci se ulozi vystupni soket, aby jej bylo mozne "kontaktovat"
	    playerId = game.addPlayer(pwout);
	    Player player = game.getPlayer(playerId);

	    if (playerId < 0) {
		send(Protocol.GENERAL_FAILURE);
		return;
	    }

	    // povedlo se pripojit jej do hry?
	    send(Protocol.JOIN_GAME_SUCCESS + " " + playerId);

	    //informuj vsechny ostatni, ze pribyl novy hrac
	    game.broadcastExcept(playerId, Protocol.JOINED
		    + " " + playerId
		    + " " + game.getPlayer(playerId).getCoords());

	    // ted posle persistentni cast mapy -> pouze objekty a jejich stav
	    String mapRepr = game.getMap().serializePersistent();
	    send(mapRepr);

	    // posle promenlivou cast hry
	    // pozice vsech hracu:
	    String pos = game.getPlayersConf();
	    send(pos);
	    
	    //posli guardy
	    pos = game.getGuardsConf();
	    send(pos);
	    
	    

	    while (true) {

		String msg = readline();

		//if (msg == null || msg.equals("close")) {
		if (msg == null) {
		    break;
		}

		if (msg.equals("")) {
		    continue;
		}
		
		
		String[] command = parseCommand(msg);
		
		if (!player.isAlive() && !command[0].equals(Protocol.LEAVE_GAME_REQUEST)){
		    send(Protocol.YOU_ARE_DEAD);
		    continue;
		}


		//hra je už dohrana
		if (!game.isActive() && !command[0].equals(Protocol.LEAVE_GAME_REQUEST)) {
		    performRefuseCommand();
		    continue;
		}

		/* Prvni "slovo" zpravy rozhoduje o obsluze */
		switch (command[0]) {
		    case Protocol.KEYS:
			performKeys();
			break;
		    case Protocol.GO:
			performGo();
			break;
		    case Protocol.STOP:
			performStop();
			break;
		    case Protocol.STEP:
			performStep();
			break;
		    // vyzaduji broadcast
		    case Protocol.TAKE:
			performTake();
			break;
		    case Protocol.OPEN:
			performOpen();
			break;
		    case Protocol.TURN_LEFT:
			performTurn(true);
			break;
		    case Protocol.TURN_RIGHT:
			performTurn(false);
			break;
		    case Protocol.LEAVE_GAME_REQUEST:
			performLeave();
			break;
		    default:
			performUnknown();
			break;
		}

	    }

	} catch (IOException ex) {
	    error("Unable to perform I/O operation");
	}

    }

    /**
     * Přečte jeden řádek ze socketu.
     * @return Načtený řádek.
     * @throws IOException 
     */
    public String readline() throws IOException {
	String message = this.pwin.readLine();
	//print(message);
	return message;
    }

    /**
     * Zašle zprávu jako jeden řádek.
     * @param message
     * @throws IOException 
     */
    public void send(String message) throws IOException {
	this.pwout.println(message);
	//print("Sent: " + message);
    }
    
    /**
     * Projde všechny položky adresáře "examples" a vytvoří z něj seznam map.
     * @return Seznam map
     */
    public String createMapList(){
	
	String res = "";
	
	File folder = new File("dest-server/maps");
	
	File[] files = folder.listFiles();
	
	for(File f: files){
	    res += f.getName() + ";";
	}
	
	return res;	
    }

    /**
     * Projde všechny aktuálně aktuálně hrané hry a vytvoří z nich seznam pro poslání
     * klientovi. Seznam zahrnuje také atributy hry - počet hlídačů, zpoždění, ...
     * @return Seznam her
     */
    public String createGameList() {

	String repr = Protocol.GAME_LIST_REQUEST + " " + server.getGames().size() + ">";

	for (Game game : server.getGames()) {
	    repr += "[";

	    repr += game.id() + ":" + game.name()
		    + ";" + game.getPlayersCount()
		    + ";" + game.getDelay()
		    + ";" + game.getGuardsCount();

	    repr += "],";
	}

	System.out.println(repr);
	return repr;
    }

    public void error(String msg) {
	System.err.println("[SERVER]" + msg + ".");
    }

    public void print(String msg) {
	System.out.println("[SERVER]" + msg + ".");
    }

    public void printSpec(String msg) {
	System.out.println("[SERVER:" + gameId + ":" + playerId + "]" + msg + ".");
    }

    /**
     * Rozdělí přijatou zprávu po bílých znacích.
     * @param msg Zpráva k rozdělení
     * @return Pole slov příkazu
     */
    private String[] parseCommand(String msg) {
	return msg.split(" ");
    }

    /* ======================================================
     *    METODY PRO INTERPRETACI JEDNOTLIVYCH ZPRAV
     * ====================================================== */
    /**
     * Reakce na příkaz "keys" - zašle hráči počet klíčů.
     * @throws IOException 
     */
    private void performKeys() throws IOException {
	Game g = server.getGame(gameId);

	int keys = g.playerKeys(playerId);

	send(Protocol.KEYS_SUCCESS + " " + keys);
    }

    /**
     * Reakce na příkaz "step". Provede krok, zašle hráči, zda se krok provedl,
     * neprovedl nebo zda-li krok vedl k výhře hry.
     * @throws IOException 
     */
    private void performStep() throws IOException {
	Game g = server.getGame(gameId);
	Player p = g.getPlayer(playerId);

	int res = g.stepPlayer(playerId);

	if (res < 0) {
	    send(Protocol.GENERAL_FAILURE);
	    return;
	}

	boolean isWin = (res == 1);

	send(Protocol.GENERAL_SUCCESS);

	Coords c = p.getCoords();

	g.broadcast(Protocol.PUT + " " + playerId + " " + c);

	if (isWin) {
	    g.broadcast(Protocol.WON + " " + playerId + " " + g.getTime() + " " + g.getStrStats());
	}

    }

    /**
     * Metoda "step" používaná vláknem pro generování plynulé chůze. Rozhraní
     * se liší o běžné reakce na "step".
     * @return
     * @throws IOException 
     */
    private boolean performGoStep() throws IOException {
	Game g = server.getGame(gameId);
	Player p = g.getPlayer(playerId);

	int res = g.stepPlayer(playerId);

	if (res < 0) {
	    send(Protocol.GO_FINISH);
	    return false;
	}

	boolean isWin = (res == 1);

	Coords c = p.getCoords();

	g.broadcast(Protocol.PUT + " " + playerId + " " + c);

	if (isWin) {
	    send(Protocol.GO_FINISH);
	    g.broadcast(Protocol.WON + " " + playerId + " " + g.getTime() + " " + g.getStrStats());
	    return false;
	}
	
	return true;

    }
    
    /**
     * Zastaví vlákno pro plynulou chůzi "go". Pokud vlákno neexistuje, je prázdnou
     * operací.
     */
    private void performStop() {
	
	if ( goThread == null)
	    return;
	
	goThread.interrupt();
	
	goThread = null;
	
    }

    /**
     * Reakce na příkaz "go". Spustí samostatné vlákno, které podle parametru
     * "delay" aktuální hry generuje plynulou sekvenci příkazů "step".
     */
    private void performGo() {

	if (goThread != null && !goThread.isInterrupted()) {
	    goThread.interrupt();
	}

	goThread = new Thread() {

	    @Override
	    public void run() {
		
		long ldelay = Math.round(game.getDelay() * 1000);

		while (true) {
		    
		    try {
			if (!performGoStep()) // nepodaril se step -> konec vlakna
			    return; 
			
		    } catch (IOException ex) {
			System.out.println("GO FAILED");
		    }
		    
		    try {
			sleep(ldelay);
		    } catch (InterruptedException ex) {
			break;
		    }
		}

	    }

	};
	
	goThread.start();

    }

    /**
     * Reakce na příkaz "take". Sebere objekt ležící před hráče. Hráči zašle
     * informaci o výsledku operace.
     * @throws IOException 
     */
    private void performTake() throws IOException {
	Game g = server.getGame(gameId);
	Player p = g.getPlayer(playerId);

	boolean res = g.takePlayer(playerId);

	if (!res) {
	    send(Protocol.GENERAL_FAILURE);
	    return;
	}

	// posle unicast o vysledku operace
	send(Protocol.GENERAL_SUCCESS);

	// a broadcast o zmene mapy vsem
	String bcMsg = Protocol.TAKEN + " " + p.getNextField().getCoords();

	g.broadcast(bcMsg);
    }

    /**
     * Reakce na příkaz "open". Pokusí se otevřít objekt před hráčem. Hráče
     * informuje o výsledku operace.
     * @throws IOException 
     */
    private void performOpen() throws IOException {
	Game g = server.getGame(gameId);
	Player p = g.getPlayer(playerId);

	boolean res = g.openPlayer(playerId);

	if (!res) {
	    send(Protocol.GENERAL_FAILURE);
	    return;
	}

	// posle unicast o vysledku operace
	send(Protocol.GENERAL_SUCCESS);

	// a broadcast o zmene mapy vsem
	String bcMsg = Protocol.OPENED + " " + p.getNextField().getCoords();

	g.broadcast(bcMsg);
    }

    /**
     * Reakce na operace "left" nebo "right". Otočí hráče požadovaným směrem.
     * @param left True - doleva, false - doprava
     * @throws IOException 
     */
    private void performTurn(boolean left) throws IOException {
	Game g = server.getGame(gameId);

	boolean res;

	if (left) {
	    res = g.turnPlayerLeft(playerId);
	} else {
	    res = g.turnPlayerRight(playerId);
	}

	if (!res) {
	    send(Protocol.GENERAL_FAILURE);
	    return;
	}

	send(Protocol.GENERAL_SUCCESS);

	//broadcast	
	Player p = g.getPlayer(playerId);

	g.broadcast(Protocol.TURNED + " " + playerId + " " + p.getDirection());

    }

    /**
     * Reakce an zavření okna hry nebo příkazu "close". Odhlásí hráče ze hry,
     * čímž uvolní místo ve hře. Hráč nadále nedostává informace o změnách hry.
     */
    private void performLeave() {
	Game g = server.getGame(gameId);

	g.leavePlayer(playerId);

	g.broadcast(Protocol.LEFT + " " + playerId);


	if (g.getPlayersCount() == 0) {
	    server.removeGame(gameId);
	    game.destroyGuardThread();
	}
    }

    /**
     * Reakce na libovolný příkaz, který hráč zaslal v době neaktivní hry 
     * (například po jejím dohrání).
     */
    private void performRefuseCommand() {
	Game g = server.getGame(gameId);

	Player p = g.getPlayer(playerId);

	p.send(Protocol.GAME_INACTIVE);
    }

    /**
     * Reakce na neznámý příkaz.
     */
    private void performUnknown() {
	Game g = server.getGame(gameId);

	Player p = g.getPlayer(playerId);

	p.send(Protocol.UNKNOWN_CMD);
    }

}
