/**
 * Soubor obsahuje implementaci klientské části. 
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client;

import ija.client.gui.LoginDialog;;
import ija.client.gui.SetGameDialog;
import ija.protocol.Protocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import java.io.PrintWriter;
import javax.swing.JOptionPane;


public class Client {

    /** komunikční socket */
    Socket sock;
    PrintWriter pwout;
    BufferedReader pwin;

    /** Grafické rozhraní, které klient obsluhuje */
    Workspace workspace;

    int id;

    BufferedReader input; //temporary - for stdin input

    public Client(String host, int port) throws IOException {

	this.sock = new Socket(host, port);

	this.pwout = new PrintWriter(this.sock.getOutputStream(), true);
	this.pwin = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
	this.input = new BufferedReader(new InputStreamReader(System.in));

	init(); // vyhazuje vyjimku -> posila ji vys  
    }

    private void init() throws IOException {

	String message;
	

	/* --- otevre se dialog pro zvoleni / vytvoreni hry ---- */
	//GameListDialog dialog = new GameListDialog(pwin, pwout);
	
	SetGameDialog dialog = new SetGameDialog(pwin, pwout);

	int selected = dialog.getSelected();

	if (selected == -1) {
	    JOptionPane.showMessageDialog(workspace, "No game selected!");
	    System.out.println("[CLIENT] No game selected!");

	    // TODO: musi se odpojit regulerne
	    System.exit(1);
	}

	/* -------- KLIENT POSLE SERVER ID HRY, KTEROU SI VYBRAL ------ */
	sendRequest(Protocol.JOIN_GAME_REQUEST + " " + selected);
	System.out.println("[CLIENT] Selected game with id: " + selected);

	// Zjisti, zda se povedlo pripojit
	String response = readline();

	if (!response.startsWith(Protocol.JOIN_GAME_SUCCESS)) {
	    System.err.println("[CLIENT] Failed to join the game.");
	    JOptionPane.showMessageDialog(workspace, "Failed to join the game.");
	    return;
	}

	// podarilo se pripojit do hry -> zjisti, jake mas ID
	id = Integer.valueOf(response.substring(Protocol.JOIN_GAME_SUCCESS.length() + 1));

	System.out.println("[CLIENT] Successfully joined game with id: " + id + ".");

    }

    /**
     * Zašle požadavek na server.
     * @param command Text požadavku
     * @throws IOException 
     */
    public void sendRequest(String command) throws IOException {
	this.pwout.println(command);
	this.pwout.flush();
    }

    /**
     * Přečte zprávu ze serveru.
     * @return Obsah zprávy
     * @throws IOException 
     */
    public String readline() throws IOException {
	return pwin.readLine();
    }

    /**
     * Zavře všechny otevřené streamy.
     * @throws IOException 
     */
    public void close() throws IOException {

	System.out.println("LOGOUT");
	sendRequest(Protocol.LEAVE_GAME_REQUEST);
	
	// uz necekej zadnou odpoved

	pwout.close();
	pwin.close();
	input.close();
	sock.close();

    }
    
    /**
     * @return Přiřazené id, se kterým vystupuje hráč ve hře na serveru.
     */
    public int getId(){
	return this.id;
    }

    public static void main(String[] args) {
	
	
	try {
	    
	    LoginDialog d = new LoginDialog();

	    Client client = new Client(d.getServer(), d.getPort());

	    Workspace workspace = new Workspace(client);

	} catch (IOException ex) {
	    System.err.println("[ Error ] Client failed. ");
	    System.err.println("[  ...  ] " + ex.getMessage());
	}
    } 

}
