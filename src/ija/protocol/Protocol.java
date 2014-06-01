/**
 * Spolčné komunikační rozhraní pro klienta i server. Obsahuje domluvené konstanty
 * reprezentující domluvenou sémantiku. Obsahuje také reprezentaci objektů, směrů
 * atp.
 */
package ija.protocol;

public class Protocol {
    
    //zamezeni instanciace tridy    
    private Protocol(){}
    
    /* "prikazy" ktere nejsou primo soucasti hry */
    
    public final static String GAME_LIST_REQUEST = "GIMME-GAMES";
    
    public final static String CREATE_GAME_REQUEST = "CREATE-GAME"; // + nazev prodleva pocet-guardu
    public final static String CREATE_GAME_SUCCESS = "100"; // + id nove hry
    public final static String CREATE_GAME_FAILURE = "101";
    
    public final static String JOIN_GAME_REQUEST = "JOIN";
    public final static String JOIN_GAME_SUCCESS = "110"; // + id hrace ve hre
    
    //odpovedi na zpravu jsou obecne
    public final static String LEAVE_GAME_REQUEST = "LEAVE";
    
    public final static String GO_FINISH = "333";
    
    /* herni prikazy a odpovedi */
    public final static String GENERAL_SUCCESS = "000";
    public final static String GENERAL_FAILURE = "999";
    
    public final static String YOU_ARE_DEAD = "200";
    
    public final static String GAME_INACTIVE = "822"; // hra je dohrana - odmita prikazy
    public final static String UNKNOWN_CMD = "834"; // neznamy prikaz
    
    public final static String KEYS_SUCCESS = "120"; // + pocet klicu
    
    public final static String GAME_SUCCESS = "820";
    public final static String GAME_SUCCESS_RESPONSE = "821";    //deprecated
    
    public final static String GAME_INVALID = "830";
    public final static String GAME_DELAY_FAILURE = "831";
    public final static String ALREADY_PLAYING = "832";
    public final static String NOT_IN_GAME = "833";
    
    // unicastove zpravy
    public final static String STEP = "step";
    public final static String GO = "go";
    public final static String TURN_LEFT = "left";
    public final static String TURN_RIGHT = "right";
    public final static String STOP = "stop";
    public final static String TAKE = "take";
    public final static String OPEN = "open";
    public final static String KEYS = "keys";
    public final static String GAME = "game";
    public final static String CLOSE = "close";
    public final static String REFRESH = "refresh";
    
    
    // broadcastove zpravy
    public static final String TAKEN = "TAKEN"; // + pozice klice
    public static final String OPENED = "OPENED"; // + pozice brany
    public static final String TURNED = "TURNED"; // + TURNED kdo novy_smer
    public static final String GUARD_TURNED = "GUARD-TURNED"; // + TURNED kdo novy_smer
    public static final String PUT = "PUT"; // + TURNED kdo novy_smer
    public static final String GUARD_PUT = "PUT-GUARD"; 
    public static final String KILL = "KILL"; // + KILL kdo
    public static final String JOINED = "JOINED"; // JOINED id coord
    public static final String LEFT = "LEFT"; // LEFT id
    public static final String WON = "WON"; // WON id
    
    
    /* =====================================================
     *          TEXTOVÁ REPREZENTACE OBJEKTU  
     * ===================================================== */ 
    
    
    /** Řetězcová reprezentace objektu zeď */
    public static final char WALL = '#';
    
    /** Řetězcová reprezentace chybějícího objektu, tedy cesty */
    public static final char PATH = '.';
    
    /** Řetězcová reprezentace zavřené brány */
    public static final char CLOSED_GATE = 'G';
    
    /** Řetězcová reprezentace otevřené brány */
    public static final char OPENED_GATE = '_';
    
    /** Řetězcová reprezentace objektu klíč */
    public static final char KEY = 'K';
    
    /** Řetězcová reprezentace cíle hry*/
    public static final char DEST = 'X';
    
    
    
    /* =======  DEFINICE SMERU HUMANOIDA ================ */
    
    /** Směr pohledu hráče na sever */
    public static final int NORTH = 0;
    
    /** Směr pohledu hráče na východ */
    public static final int EAST = 1;
    
    /** Směr pohledu hráče na jih */
    public static final int SOUTH = 2;
    
    /** Směr pohledu hráče na západ */
    public static final int WEST = 3;
    

    
    public static boolean isSuccess(String resp){
	
	return resp.startsWith(GENERAL_SUCCESS);

    }
}
