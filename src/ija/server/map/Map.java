/**
 * Obsahuje třídu reprezentující mapu hry.
 *
 * Autoři: David Kovařík (xkovar66), Tomáš Bruckner (xbruck02)
 */
package ija.server.map;

import ija.server.humanoid.Player;
import ija.server.humanoid.Humanoid;
import ija.protocol.Protocol;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 * Matice objektů reprezentující mapu hry.
 */
public class Map {

    /**
     * Maximální počet polí v řádku/sloupci.
     */
    public static final int MAX_FIEDS = 50;

    /**
     * Minimální počet polí ve řádku/sloupci.
     */
    public static final int MIN_FILEDS = 20;

    /**
     * Maximální počet hráčů v jedné hře v jeden okamžik.
     */
    public static final int MAX_PLAYERS = 4;

    /**
     * Matice hracích polí.
     */
    private MapField fields[][];

    /**
     * Seznam hráčů.
     */
    private Player players[] = new Player[MAX_PLAYERS];

    private int cols;
    private int rows;

    /**
     * Otevře zadaný soubor obsahující definici mapy, načte jej a vytvoří
     * vnitřní reprezentaci této mapy.
     *
     * @param filename Jméno souboru obsahujícího popis mapy.
     *
     * @throws MapProcessingException if map loading and processing fails
     */
    public Map(String filename) throws MapProcessingException {

	String format = "";

	try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

	    String line = reader.readLine();

	    // omezeni radku je min = 20x20, max=50x50   => dvouciferna cisla
	    this.rows = Integer.parseInt(line.substring(0, 2));
	    this.cols = Integer.parseInt(line.substring(3, 5));
	    
	    if (rows < MIN_FILEDS || rows > MAX_FIEDS || cols < MIN_FILEDS || cols > MAX_FIEDS) {
		throw new MapProcessingException("Dimensions of " + filename
			+ "are not in interval <" + MIN_FILEDS + ";" + MAX_FIEDS + ">");
	    }

	    this.fields = new MapField[this.rows][this.cols];

	    for (int i = 0; i < this.rows; ++i) {
		line = reader.readLine();

		if (line == null) { // neocekavany konec souboru
		    throw new MapProcessingException("Unexpected EOF in " + filename);
		}

		for (int j = 0; j < this.cols; j++) {

		    char ch;

		    try {
			ch = line.charAt(j);
		    } catch (IndexOutOfBoundsException ex) { //neocekavany konec radku
			throw new MapProcessingException("Unexpected EOL in " + filename);
		    }

		    switch (ch) {
			case Protocol.CLOSED_GATE: // zavrena brana
			case Protocol.WALL: // zed
			case Protocol.PATH: // volno
			case Protocol.KEY: // klic
			case Protocol.DEST: // cil
			    this.fields[i][j] = new MapField(this, new Coords(i, j), ch);
			    break;
			default:
			    throw new MapProcessingException("Unknown object at '" + i + "x" + j + "'.");
		    } // switch

		} // inner for

	    } // top for

	} catch (IOException ex) {
	    throw new MapProcessingException("Unknown map: " + filename);
	}
    }

    /**
     * Vytiskne aktuální stav mapy na standardní vstup.
     */
    public void print() {

	for (int i = 0; i < this.rows; i++) {
	    for (int j = 0; j < this.cols; j++) {
		System.out.print(this.fields[i][j].getRepresentation());
	    }
	    System.out.print('\n');
	}
    }

    /**
     * Vrací pole mapy identifikované zadanou souřadnicí
     *
     * @param c Souřadnice identifikující políčko
     * @return Instanci nalezeného pole
     */
    public MapField fieldAt(Coords c) {

	if (!this.isInMap(c)) {
	    return null;
	}

	return this.fields[c.getRow()][c.getCol()];
    }

    /**
     * Zjistí, zda se zadaná souřadnice nachází v mapě nebo za ní.
     *
     * @param c Zkoumaná souřadnice
     * @return Vrací true, pokud souřadnice leží v mapě, jinak false.
     */
    public boolean isInMap(Coords c) {

	if (c.getRow() < 0 || c.getRow() >= rows || c.getCol() < 0 || c.getCol() >= cols) {
	    return false;
	}

	return true;
    }

    /**
     * Náhodně vygeneruje místo spawnu hráče.
     *
     * @return Souřadnice pro spawn.
     */
    public Coords generateSpawn() {
	Random randomGenerator = new Random();
	Coords res = null;

	int max = 5;

	for (int x = 0; x <= max; ++x) {
	    int randx = randomGenerator.nextInt(this.rows - 1);
	    int randy = randomGenerator.nextInt(this.cols - 1);

	    if (x == max) // 5x nic nahodne nenasel  -> jde sekvencne
	    {
		randx = randy = 0;
	    }

	    for (int i = randx; i < this.rows; i++) {

		for (int j = randy; j < this.cols; j++) {
		    MapField field = this.fields[i][j];

		    if (field.isPath() && field.canSeize()) {
			return new Coords(i, j);
		    }
		}
	    }
	}

	if (res == null) {
	    res = this.generateSpawn();
	}
	

	return res;
    }

    /**
     * Obsadí pole souřadnici c hráčem p.
     *
     * @param c Souřadnice obsazovaného políčka
     * @param p Instace hráče, který obsazuje
     * @return Vrací true, pokud se obsazení povedlo, jinak false.
     */
    public boolean seize(Coords c, Humanoid p) {

	MapField f = this.fields[c.getRow()][c.getCol()];

	if (f.canSeize()) {
	    return f.seize(p);
	}

	return false;

    }


    /**
     * Vrací serialializovanou podobu persisentních obejktů v mapě a jejich stav.
     * Persistentním objektem je myšlen objekt s neměnnou polohou.
     * @return Jednořádkový řetězec obsahující serializovanou persistentní vrstvu.
     */
    public String serializePersistent() {
	
	String res = rows + " " + cols + " ";
	
	for (int i = 0; i < rows; ++i) {
	    
	    for (int j = 0; j < cols; ++j) {
		MapField f = fields[i][j];
		
		char repr = f.getObjectRepresentation();
		
		res += repr;
	    }
	    
	}
	
	return res;
	
    }

}
