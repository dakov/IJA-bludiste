/**
 * Popisuje společné rozhranní pro všechny objekty, které je možné položit na hrací plochu
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.server.objects;

public abstract class MapObject {
    
    /** Identifikace objektu */
    protected String name;
    
    public MapObject(String name) {
        this.name = name;
    }    
    
    /**
     * Zjistí, zda je možné otevřít daný objekt.
     * @return Pravdivostní hodnotu, zda je možné otevřít objekt
     */
    public abstract boolean canBeOpened();
    
    /**
     * Zjistí, zda je možné obsadit daný objekt.
     * @return Pravdivostní hodnotu, zda je možné obsadit objekt
     */
    public abstract boolean canSeize();
    
    /**
     * Pokusí se otevřít objekt.
     * @return Výsledek, zda se podařilo objekt otevřít
     */
    public abstract boolean open();

    
    /**
     * Vezme objekt z hrací plochy a vrací úspěšnost této operace.
     * @return Výsledek, zda se podařilo objekt sebrat.
     */
    public boolean take() {
	return false;
    }
    
    /**
     * Získá znakovou reprezentaci objektu
     * @return Znak reprezentující daný objekt
     */
    public abstract char repr();

}
