/**
 * Výjimka reprezentující chybu při zpracovávání definice mapy
 * Autoři: David Kovařík (xkovar66), Tomáš Bruckner (xbruck02)
 */
package ija.server.map;

public class MapProcessingException extends Exception {
    
    public MapProcessingException(String message) {
        super(message);
    }
    
}
