/**
 * Třída reprezentující souřadnicový systém (řádek, sloupec)
 * Autoři: David Kovařík (xkovar66), Tomáš Bruckner(xbruck02)
 */
package ija.server.map;

/**
 * Třída reprezentuje dvojici hodnot, které jednoznačně identifikují políčko
 * v souřadnicovém systému mapy;
 */
public class Coords {
    
    /** Sloupec */
    private int row;
    
    /** Řádek */
    private int col;
    
    public Coords(int row, int col) {
	this.row = row;
	this.col = col;
    } 

    public Coords(String s) {
	// format "[ROW:COL]"
	
	String vals = s.substring(1,s.length()-1);
	
	String[] parsed = vals.split(":");
	
	
	this.row = Integer.valueOf(parsed[0]);
	this.col = Integer.valueOf(parsed[1]);
	
    }
    
    /**
     * Vrací hodnotu sloupce matice
     * @return Hodnotu slupce souřadnice
     */
    public int getRow(){
	return this.row;
    }
    
    /**
     * Vrací hodnotu řádku matice
     * @return Hodnotu řádku souřadnice
     */
    public int getCol() {
	return this.col;
    }
    
    /**
     * Přičte k souřadnici krok ve směru řádku a sloupce.
     * @param drow Posunutí ve směru řádků
     * @param dcol Posunutí ve směru sloupců
     * @return Novou souřadnici, která vznikla přičtením hodnot k původní.
     */
    public Coords add(int drow, int dcol){
	return new Coords(this.row + drow, this.col + dcol);
    }
    
    @Override
    public String toString(){
	return "[" + this.row +":" + this.col+"]";
    }
    
}
