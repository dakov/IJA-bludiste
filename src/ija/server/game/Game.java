/**
 * Soubor obsahuje implementaci rozehrané hry. 
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.server.game;

import ija.server.humanoid.Player;
import ija.server.humanoid.Guard;
import ija.server.humanoid.Humanoid;
import ija.server.map.Map;
import ija.server.map.Coords;
import ija.server.map.MapField;
import ija.server.map.MapProcessingException;
import ija.protocol.Protocol;
import ija.server.GuardsThread;
import java.io.PrintWriter;
import java.util.Calendar;

public class Game {

    /**
     * Maximální počet hráčů v jedné hře v jeden okamžik
     */
    public static final int MAX_PLAYERS = 4;

    private String fullName;
    private Player[] players;
    private Guard[] guards;

    private int playersCount = 0;

    private Map map;
    private int id;
    private boolean active = true;

    private double delay;
    public int guardsCount;

    private long startTime;
    private long endTime;
    
    private Thread guardThread;
    
    public static final String mapFolder = "dest-server/maps/";
    
    public Game(int id, String name, double delay, int guardsCount) throws MapProcessingException {
	this.id = id;
	this.fullName = mapFolder + name;
	this.players = new Player[MAX_PLAYERS];

	startTime = Calendar.getInstance().getTimeInMillis();

	this.delay = delay;
	this.guardsCount = guardsCount;

	this.guards = new Guard[guardsCount];

	// nehraje zadny hrac -> null
	for (Player player : players) {
	    player = null;
	}

	this.map = new Map(this.fullName);

	// guardy muzu vytvaret az vytvorim mapu!
	for (int i = 0; i < guardsCount; ++i) {
	    addGuard(i);
	}
	
	guardThread = new GuardsThread(this);
	guardThread.start();

	//map.print();
    }

    /**
     * @return True, pokud je hra aktivní, jinak false.
     */
    public boolean isActive() {
	return active;
    }

    public Guard[] getGuards() {
	return guards;
    }

    /**
     * Vrací interval v milisekundách o délce hry. Metoda může být zavolána až
     * po konci hry.
     * @return interval v milisendách
     */
    public long getTime() {
	if (endTime == 0) {
	    endTime = Calendar.getInstance().getTimeInMillis();
	}

	return endTime - startTime;
    }

    /**
     * Zašle zprávu všem hráčům ve hře.
     * @param message Obsah zprávy
     */
    public void broadcast(String message) {

	for (Player p : players) {

	    if (p == null) {
		continue;
	    }

	    p.send(message);

	}

    }

    /**
     * Zašle zprávu všem hráčům kromě jednoho. Používá se např. při připojení
     * do hry, kdy všichni hráče, kromě nového, obdrží onformaci o připojení
     * nového hráče.
     * @param playerId Id hráče, kterému se zpráva nepošle
     * @param message Obsah zprávy
     */
    public void broadcastExcept(int playerId, String message) {

	for (Player p : players) {

	    if (p == null || p.getId() == playerId) {
		continue;
	    }

	    p.send(message);

	}
    }

    /**
     * Zjistí první dostupné id pro nového hráče.
     *
     * @return Id pro nového hráče nebo -1, pokud je hra plná.
     */
    public int getAvailableId() {

	for (int i = 0; i < MAX_PLAYERS; ++i) {

	    if (players[i] == null) {
		return i;
	    }
	}

	return -1;
    }

    /**
     * Přidá hráče do hry na náhodně vygenerovanou pozici.
     *
     * @param sock
     * @return Identifikátor hráče v rámci přihlášené hry
     */
    public int addPlayer(PrintWriter sock) {
	return this.addPlayer(sock, null);
    }

    /**
     * Přidá hráče do hry na zadané souřadnice
     *
     * @param sock
     * @param c Počáteční souřadnice hráče
     * @return Identifikátor hráče v přihlášené hře nebo -1 pokud je hra plná.
     */
    public int addPlayer(PrintWriter sock, Coords c) {

	// ------------------------------------------------
	// TODO: tady bych si dokázal predstavit zamek!!!!
	// ------------------------------------------------
	int playerId = getAvailableId();

	if (playerId < 0) {
	    return playerId;
	}

	Coords spawn;
	/* pokud je zadane c == null, vygeneruj nahodnou */
	if (c == null) {
	    spawn = this.map.generateSpawn();
	} else {
	    spawn = c;
	}

	Player player = new Player(playerId, sock, this.map.fieldAt(spawn));

	this.map.seize(spawn, player);

	this.players[playerId] = player;

	playersCount++; // zvys pocet hracu

	return playerId; // vraci poradove cislo hrace v teto hre
    }

    public void addGuard(int id) {
	this.addGuard(id, null);
    }

    public void addGuard(int id, Coords c) {

	int playerId = getAvailableId();

	Coords spawn;
	/* pokud je zadane c == null, vygeneruj nahodnou */
	if (c == null) {
	    spawn = map.generateSpawn();
	} else {
	    spawn = c;
	}

	Guard g = new Guard(id, this.map.fieldAt(spawn));

	this.map.seize(spawn, g);

	this.guards[id] = g;

    }

    /**
     * Odebere hráče s daným id ze hry.
     * @param playerId Identifikace hráče.
     */
    public void leavePlayer(int playerId) {
	players[playerId] = null;
	playersCount--;
    }

    /**
     * Vrací počet hráčů.
     * @return počet hráčů
     */
    public int getPlayersCount() {
	return playersCount;
    }

    public boolean turnHumanoidLeft(Humanoid h) {
	h.turnLeft();

	return true;
    }

    public boolean turnHumanoidRight(Humanoid h) {
	h.turnRight();

	return true;
    }

    public boolean turnPlayerLeft(int playerId) {

	if (playerId > this.players.length - 1) {
	    return false;
	}

	Player p = this.players[playerId];

	p.turnLeft();

	return true;

    }

    public boolean turnPlayerRight(int playerId) {

	if (playerId > this.players.length - 1) {
	    return false;
	}

	Player p = this.players[playerId];

	p.turnRight();

	return true;

    }
    
    public int stepPlayer(int playerId) {

	//-1 => nemohl se pohnout, 0 pohnul se, 1 pohnul se a ukoncil hru
	if (playerId > this.players.length - 1) {
	    return -1;
	}

	Player p = this.players[playerId];
	MapField f = p.step();

	if (f == null) // hrac se nepohnul se -> nemohl se pohnout
	{
	    return -1;
	}

	if (f.isDestination()) {
	    // this.quit(); tohle na serveru uz nechci ?!
	    active = false;
	    return 1;
	}

	return 0;
    }

    /**
     * Provede operaci "open" hráčem s daným id.
     * @param playerId Identifikace hráče.
     * @return True pokud se operace povedla, jinak false.
     */
    public boolean openPlayer(int playerId) {

	if (playerId > this.players.length - 1) {
	    return false;
	}

	Player p = this.players[playerId];

	return p.open();
    }

    /**
     * Provede operaci "take" hráčem s daným id.
     * @param playerId Identifikace hráče.
     * @return True pokud se operace povedla, jinak false.
     */
    public boolean takePlayer(int playerId) {

	if (playerId > this.players.length - 1) {
	    return false;
	}

	Player p = this.players[playerId];

	return p.take();
    }

    /**
     * Nalezne hráče s identifikátore playerId a vrátí, kolik drží klíčů.
     *
     * @param playerId Identifikace hráče
     * @return Počet klíčů, které daný hráč právě drží
     */
    public int playerKeys(int playerId) {

	Player p = this.players[playerId];

	return p.keys();
    }

    /**
     * Vytiskne stav hry
     */
    public void show() {
	this.map.print();
    }

    /**
     * @return Jméno hry
     */
    public String name() {
	return this.fullName;
    }

    /**
     * @return Id hry
     */
    public int id() {
	return this.id;
    }

    public Map getMap() {
	return this.map;
    }

    /**
     * Vytvoří konfigurace všech hráčů ve hře.
     * @return Textovou reprezentaci konfigurace.
     */
    public String getPlayersConf() {

	String res = "";

	for (Player p : players) {
	    res += "[";

	    if (p != null) {
		Coords c = p.getCoords();
		res += c.getRow() + ":" + c.getCol() + ":" + p.getDirection();
	    }

	    res += "];";
	}

	return res;

    }

    /**
     * Vytvoří konfigurace všech hlídačů ve hře.
     * @return Textovou reprezentaci konfigurace.
     */
    public String getGuardsConf() {

	String res = "";

	for (Guard g : guards) {
	    res += "[";

	    if (g != null) {
		Coords c = g.getCoords();
		res += c.getRow() + ":" + c.getCol() + ":" + g.getDirection();
	    }

	    res += "];";
	}

	return res;

    }

    /**
     * @param playerId Identifikace hráče
     * @return Instanci hráče
     */
    public Player getPlayer(int playerId) {
	return players[playerId];
    }

    public double getDelay() {
	return delay;
    }

    public int getGuardsCount() {
	return guardsCount;
    }

    /**
     * Vygeneruje textovou reprezentaci statistik pro každého hráče.
     * @return Statistiky hráčů v textové podobě.
     */
    public String getStrStats() {

	String res = "";

	for (Player p : players) {
	    if (p == null) {
		res += "[];";
		continue;
	    }

	    res += "[" + p.getPlayTime() + ":" + p.getSteps() + "];";
	}

	return res;
    }

    /**
     * Provede jeden krok hlídače.
     * @param g Hlídač, se kterým se pracuje.
     */
    public void moveGuard(Guard g) {
	
	//nejdriv zkusi krok
	
	int res = g.guardStep();
	
	if (res == 0) {
	    Coords c = g.getCoords();
	    broadcast(Protocol.GUARD_PUT + " " + g.getId() + " " + c);	    
	} else if (res == 1) {
	    guardKilled(g);
	} else { //nepovedl se krok
	    
	    boolean dir = g.getTurnDirection();
	    //System.out.println("směr: " + ((dir) ? "doleva" : "doprava"));
	    
	    if (dir) {//true jdi doleva
		turnHumanoidLeft(g);
	    } else { //jdi doprava
		turnHumanoidRight(g);
	    }
	    
	    broadcast(Protocol.GUARD_TURNED + " " + g.getId() + " " + g.getDirection());
	}
    }
    
    
    /**
     * Reakce na situaci, kdy hlídač zabil hráče.
     * @param g 
     */
    public void guardKilled(Guard g) {
	//MapField f = map.fieldAt(g.getCoords());
	MapField f = g.getNextField();
	int x = f.getHumanoid().getId();
	broadcast(Protocol.KILL + " " + x);
	kill(x);
    }

    /**
     * Provede potřebné operace pro odstranění hráče po zabití hlídačem.
     * @param id Identifikace hráče.
     */
    public void kill(int id) {

	Player p = players[id];

	if (p != null) {

	    p.kill();

	    MapField f = p.getField();

	    f.leave();
	    
	}

    }

    /**
     * Přeruší vlákno hlídačů.
     */
    public void destroyGuardThread() {
	if (guardThread == null)
	    return;
	
	guardThread.interrupt();
	System.out.println("[GUARDS] Thread interrupted");
    }
}
