/**
 * Soubor obsahuje implementaci společného chování "živých" objektů ve hře.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.server.humanoid;

import ija.server.map.Coords;
import ija.server.map.MapField;
import ija.protocol.Protocol;

public abstract class Humanoid {

    protected MapField field;
    protected Coords coords;

    protected int id;

    /**
     * Směr, kterým se humanoid aktuálně dívá
     */
    protected int direction = Protocol.NORTH;

    /**
     * Otočí humanoida vpravo
     */
    public void turnRight() {
	this.direction = ++this.direction % 4; // kdyby to nefungovalo, tak je to tady!!!!
    }

    /**
     * Otočí humanoida vlevo
     */
    public void turnLeft() {
	this.direction = --this.direction % 4;

	if (this.direction < 0) {
	    this.direction += 4;
	}
    }

    /**
     * @return Směr pohledu humanoida
     */
    public int getDirection() {
	return direction;
    }

    public Coords getCoords() {
	//return this.coords;
	Coords c = field.getCoords();
	return c;
    }

    public void setCoords(Coords coords) {
	this.coords = coords;
    }

    /**
     * Nalezne sousední pole ve směru pohledu.
     *
     * @return Instance sousedního pole
     */
    public MapField getNextField() {
	int dx = 0, dy = 0;

	switch (this.getDirection()) {
	    case Protocol.NORTH:
		dy = -1;
		break;
	    case Protocol.SOUTH:
		dy = 1;
		break;
	    case Protocol.EAST:
		dx = 1;
		break;
	    case Protocol.WEST:
		dx = -1;
		break;
	}

	return this.field.getNeighbour(dy, dx);
    }

    /**
     * Provede jeden krok ve směru svého pohledu
     *
     * @return Instanci pole, na které soupnul, jinak null, pokud pole není
     * dostupné.
     */
    public MapField step() {

	MapField f = this.getNextField();

	if (f == null) {
	    return null;
	}

	if (f.seize(this)) {

	    this.field.leave();

	    this.field = f;

	    return f;
	}

	return null;

    }

    public void setField(MapField f) {
	this.field = f;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getId() {
	return id;
    }

    /**
     * @return 0rientované souřadnice humanoida.
     */
    public OrientedCoords getOrientedCoords() {
	return new OrientedCoords(getCoords(), getDirection());
    }

    /**
     * Vytvoří vhodnou znakovou reprezentaci humanoida.
     *
     * @return Znaková reprezentace
     */
    public abstract char repr();

}
