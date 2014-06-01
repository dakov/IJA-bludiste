/**
 * Grafická reprezentace mapy.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client.gui.map;

import ija.client.gui.humanoid.XGuard;
import ija.client.gui.humanoid.XPlayer;
import ija.server.map.Coords;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

public class XMap extends JPanel {

    private int rows;
    private int cols;

    private XPlayer[] players;
    private XGuard[] guards;

    private XMapField[][] fields;

    public XMap(String format) {

	init(format);
	players = new XPlayer[4]; // TODO: konstantu do protokolu
    }

    private void init(String format) {
	//           012345678..
	//format je "XX YY <mapa>"
	// nacte 2 cisla
	this.rows = Integer.valueOf(format.substring(0, 2));
	this.cols = Integer.valueOf(format.substring(3, 5));
	
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	
	c.fill = GridBagConstraints.BOTH;
	
	c.gridwidth = 1;
	
	setLayout(gbl);
	
	setMaximumSize(new Dimension(50, 50));
	
	fields = new XMapField[rows][cols];

	format = format.substring(6);

	for (int i = 0; i < rows; ++i) {
	    for (int j = 0; j < cols; ++j) {

		char item = format.charAt(i * cols + j);

		XMapField f = new XMapField(this, new Coords(i, j), item);
		this.fields[i][j] = f;
		
		if (j == cols-1){
		    c.gridwidth = GridBagConstraints.REMAINDER;
		    add(f, c);
		    c.gridwidth = 1;
		} else {
		    add(f,c);
		}
		    
	    }

	}

	XMapField f = fields[10][10];

    }

    /**
     * Na základě zadaného formátu vytvoří nové hráče do mapy.
     * Formát je [row:col:dir]. Důležité je pořadí hráčů. Tedy pokud ve
     * hře neexistuje hráč s id 2, pak je celý tvar: 
     * [row:col:dir][][row:col:dir][row:col:dir]
     * @param format Formátovací řetězec
     */
    public void createMultiplePlayers(String format) {

	// zpracovava konfiguraci hracu [row:col:dir]
	int i = 0;

	for (String s : format.split(";")) {

	    if (s.equals("[]")) {
		players[i] = null;
		continue;
	    }

	    //odstraneni []
	    s = s.substring(1, s.length() - 1);

	    String[] data = s.split(":");

	    Coords c = new Coords(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
	    players[i] = new XPlayer(i, c, Integer.valueOf(data[2]));

	    setPlayer(i, c);

	    ++i;
	}

    }
    /**
     * Na základě zadaného formátu vytvoří nové hlídače do mapy.
     * Formát je [row:col:dir];[row:col:dir]
     * @param format Formátovací řetězec
     */
    
    public void createMultipleGuards(String format) {

	// zpracovava konfiguraci hracu [row:col:dir]
	
	String[] items = format.split(";");
	
	guards = new XGuard[items.length];

	for (int i = 0; i < guards.length; ++i) {
	    
	    String s = items[i];

	    //odstraneni [, ]
	    s = s.substring(1, s.length() - 1);

	    String[] data = s.split(":");

	    Coords c = new Coords(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
	    guards[i] = new XGuard(i, c, Integer.valueOf(data[2]));

	    setGuard(i, c);

	}

    }

    /**
     * Přidá nového hráče se zadaným id na zadané souřadnice.
     * @param id id hráče
     * @param c souřadnice hráče
     */
    public void createNewPlayer(int id, Coords c) {
	players[id] = new XPlayer(id, c);
    }

    /**
     * Odstraní hráče ze hry.
     * @param id Identifikátor hráče.
     */
    public void removeUser(int id) {
	players[id] = null;
    }

    /**
     * Na pozici 'c' umístí hráče s daným id.
     * @param id identifikátor hráče
     * @param c Cílové souřadnice
     */
    public void setPlayer(int id, Coords c) {
	XMapField f = fieldAt(c);

	f.setHumanoid(players[id]);

    }

    /**
     * Z pozice 'c' odebere hráče s daným id
     * @param id identifikátor hráče
     * @param c Cílové souřadnice
     */
    public void unsetPlayer(int id, Coords c) {
	XMapField f = fieldAt(c);

	f.unsetHumanoid(players[id]);

    }
    
    public void setGuard(int id, Coords c) {
	XMapField f = fieldAt(c);

	f.setHumanoid(guards[id]);

    }

    public void unsetGuard(int id, Coords c) {
	XMapField f = fieldAt(c);

	f.unsetHumanoid(guards[id]);

    }

    public XPlayer getPlayer(int id) {
	return players[id];
    }
    
    public XGuard getGuard(int id) {
	return guards[id];
    }

    public XMapField fieldAt(Coords c) {
	return fields[c.getRow()][c.getCol()];
    }


}
