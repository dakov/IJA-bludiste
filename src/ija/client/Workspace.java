/**
 * Soubor obsahuje implementaci hlavního grafického okna. 
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client;

import ija.client.gui.humanoid.XGuard;
import ija.client.gui.humanoid.XPlayer;
import ija.client.gui.map.XMap;
import ija.client.gui.map.XMapField;
import ija.server.map.Coords;
import ija.protocol.Protocol;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class Workspace extends JFrame {

    private final Client client;

    private JTextArea history;
    private JTextField prompt;
    private JScrollPane historyScrollPane;

    private JScrollPane mapScrollPane;

    /** Grafická reprezentace mapy */
    private XMap map;

    public Workspace(Client client) {

	this.client = client;

	try {

	    String response = client.readline();

	    initComponents(response);

	    run();
	} catch (IOException ex) {
	    System.err.println("[CLIET] Workspace unable to perform I/O operation");
	    JOptionPane.showMessageDialog(null, "Unable to perform I/O operation", "I/O fail", JOptionPane.ERROR_MESSAGE);
	}

    }

    private void initComponents(String mapFormat) {

	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setTitle("IJA 2014 - Bludiště");

	    getContentPane().setLayout(new BorderLayout());

	    JPanel controlPane = new JPanel();

	    controlPane.setLayout(new BorderLayout());

	    map = new XMap(mapFormat);

	    history = new JTextArea(5, 20);
	    history.setEnabled(false);
	    history.setDisabledTextColor(Color.GRAY);

	    // auto-scroll na dno text arey
	    ((DefaultCaret) history.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

	    prompt = new JTextField();

	//pridej mapu
	    //getContentPane().add(map, BorderLayout.CENTER);
	    int i = 0;

	    mapScrollPane = new JScrollPane(map);

	//scrollPane.setPreferredSize(new Dimension(50, 50));
	    //mapScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	    //mapScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    getContentPane().add(mapScrollPane);

	    historyScrollPane = new JScrollPane(history);
	    historyScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    history.setWrapStyleWord(true);

	    controlPane.add(prompt, BorderLayout.SOUTH);

	    controlPane.add(historyScrollPane, BorderLayout.CENTER);

	    add(controlPane, BorderLayout.WEST);

	    prompt.addKeyListener(new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {

		    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			String text = prompt.getText();
			enterMessage(text);

			send(text);
			
			if (text.equals("close")) {
				Workspace.this.dispatchEvent(
					new WindowEvent(Workspace.this, WindowEvent.WINDOW_CLOSING)
				);
			}
			
		    } //temporary
		    else if (e.getKeyCode() == KeyEvent.VK_UP) {
			enterMessage("step");
			send("step");
		    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			enterMessage("left");
			send("left");
		    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			enterMessage("right");
			send("right");
		    } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			enterMessage("open");
			send("open");
		    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			enterMessage("take");
			send("take");
		    }

		}
	    }
	    );

	    this.addWindowListener(new WindowAdapter() {

		@Override
		public void windowClosing(WindowEvent e) {

		    try {
			client.close();
		    } catch (IOException ex) {
			JOptionPane.showMessageDialog(Workspace.this, ex.getMessage());
		    }
		}

	    });

	//setResizable(false);
	    pack();
	    
	    prompt.requestFocusInWindow();
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    
	    this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
	    
	    setVisible(true);
	    
    }

    public void send(String text) {

	try {
	    client.sendRequest(text);
	} catch (IOException ex) {
	    System.err.println("[CLIENT] Failed to send request");
	}
    }

    /**
     * Vloží zadaný příkaz do panelu s historií
     * @param message Obsah zprávy.
     */
    public void enterMessage(String message) {
	prompt.setText("");

	history.append(">>> " + message + '\n');
    }

    /**
     * Vloží odpověď serveru do panelu s historií.
     * @param message Obsah odpovědi
     */
    public void enterResponse(String message) {
	prompt.setText("");

	history.append(message + '\n');
    }

    
    private void run() throws IOException {

	enterMessage("Game joined");

	// ted ocekava poslani pozic hracu
	String message = client.readline();
	map.createMultiplePlayers(message);
	
	//nastavi okno na sebe
	this.locatePlayer(client.getId());

	// ted ocekava poslani pozic hlidacu
	message = client.readline();
	map.createMultipleGuards(message);

	// cyklus slouzi jak pro zpracovani odpovedi na pro prikazy, 
	// tak pro zpracovani a interpretaci broadcastovych zprav
	while (true) {

	    String msg = client.readline();

	    if (msg == null) {
		break;
	    }

	    String[] command = parseCommand(msg);

	    interpret(command);
	}

    }

    private String[] parseCommand(String msg) {
	return msg.split(" ");
    }

    /**
     * Interpretuje odpověď serveru.
     * @param command Rozparsovaný příkaz
     */
    private void interpret(String[] command) {
	
	switch (command[0]) {
	    case Protocol.YOU_ARE_DEAD:
		enterResponse("You are dead!");
		break;
	    case Protocol.GENERAL_SUCCESS:
		enterResponse("Ok");
		break;
	    case Protocol.GENERAL_FAILURE:
		enterResponse("Failed");
		break;
	    case Protocol.GO_FINISH:
		enterResponse("Go finished");
		break;
	    case Protocol.GAME_INACTIVE:
		enterResponse("Game is finished!");
		break;
	    case Protocol.UNKNOWN_CMD:
		enterResponse("Unknown command!");
		break;
	    case Protocol.KEYS_SUCCESS:
		enterResponse(command[1]);
		break;
	    case Protocol.TAKEN:
		performTaken(command[1]);
		break;
	    case Protocol.OPENED:
		performOpened(command[1]);
		break;
	    case Protocol.TURNED:
		performTurned(command[1], command[2]);
		break;
	    case Protocol.GUARD_TURNED:
		performGuardTurned(command[1], command[2]);
		break;
	    case Protocol.PUT:
		performPut(command[1], command[2]);
		break;
	    case Protocol.GUARD_PUT:
		performGuardPut(command[1], command[2]);
		break;
	    case Protocol.JOINED:
		performJoined(command[1], command[2]);
		break;
	    case Protocol.LEFT:
		performLeft(command[1]);
		break;

	    case Protocol.KILL:
		performKilled(command[1]);
		break;
	    case Protocol.WON:
		performWon(command[1], command[2], command[3]);
		break;
	}
    }

    private void performTaken(String coord) {
	//System.out.println("[CLIENT] Key taken from " + coord);

	Coords c = new Coords(coord);

	XMapField f = map.fieldAt(c);

	f.getObject().take();
    }

    private void performOpened(String coord) {
	//System.out.println("[CLIENT] Key taken from " + coord);

	Coords c = new Coords(coord);

	XMapField f = map.fieldAt(c);

	f.getObject().open();
    }

    private void performTurned(String who, String ndir) {

	int id = Integer.valueOf(who);
	int dir = Integer.valueOf(ndir);

	XPlayer p = map.getPlayer(id);

	if (p == null) {
	    System.err.println("[CLIENT] turn failed");
	    return;
	}

	p.setDirection(dir);
    }

    private void performKilled(String who) {
	int id = Integer.valueOf(who);
	XPlayer p = map.getPlayer(id);

	map.unsetPlayer(id, p.getCoords());
	
	enterResponse("!Player " +  id +" has been killed!");

	if (id == client.getId()) {
	    JOptionPane.showMessageDialog(null, "You've lost the game");
	}
    }

    private void performGuardTurned(String who, String ndir) {

	int id = Integer.valueOf(who);
	int dir = Integer.valueOf(ndir);

	XGuard g = map.getGuard(id);

	if (g == null) {
	    System.err.println("[CLIENT] guard turn failed");
	    return;
	}

	g.setDirection(dir);
    }

    private void performJoined(String who, String where) {

	int id = Integer.valueOf(who);
	Coords c = new Coords(where);

	map.createNewPlayer(id, c);

	XPlayer p = map.getPlayer(id);

	map.setPlayer(id, c);

    }

    private void performPut(String who, String where) {

	int id = Integer.valueOf(who);

	Coords c = new Coords(where);

	XPlayer p = map.getPlayer(id);
	Coords prevC = p.getCoords();

	p.setCoords(c);

	map.unsetPlayer(id, prevC);
	map.setPlayer(id, c);

	if (id == this.client.getId()) {
	    locatePlayer(id);
	}
    }

    private void performGuardPut(String who, String where) {

	int id = Integer.valueOf(who);
	Coords c = new Coords(where);

	XGuard g = map.getGuard(id);
	Coords prevC = g.getCoords();

	g.setCoords(c);

	map.unsetGuard(id, prevC);
	map.setGuard(id, c);

    }

    private void performLeft(String who) { // left game
	int id = Integer.valueOf(who);

	XPlayer p = map.getPlayer(id);
	Coords c = p.getCoords();

	map.unsetPlayer(id, c);

	map.removeUser(id);
    }

    private void performWon(String who, String gt, String format) {
	int id = Integer.valueOf(who);

	String[] stats = format.split(";");

	Long gameTime = Long.valueOf(gt);

	int idx = 0;

	for (int i = 0; i < stats.length; ++i) {

	    String s = stats[i];

	    if (s.equals("[]")) { //prazdne id
		continue;
	    }

	    s = s.substring(1, s.length() - 1); // odstran hranate zavorky

	    String[] data = s.split(":");

	    long time = Long.valueOf(data[0]);
	    int steps = Integer.valueOf(data[1]);

	    String gtime = formatTimeInMilis(Long.valueOf(gt));

	    String stime = formatTimeInMilis(time);

	    assignStatsToPlayes(i, gtime, stime, steps); // prida i-temu hraci listenery na enter/leave mysi

	}

	if (client.id == id) {
	    System.out.println("[CLIENT] You've won the game!");
	    JOptionPane.showMessageDialog(this, "[CLIENT] You've won the game!");
	} else {
	    System.out.println("Player " + id + " won");
	    JOptionPane.showMessageDialog(this, "Player " + id + " won");
	}

    }
    /**
     * Přiřadí hráč statistiky. Po najetí hráče se zobrazí panel zobrazující tyto
     * statistiky.
     * @param id Hráč, kterému přiřazujeme
     * @param gt Délka hry
     * @param time Čas strávený ve hře
     * @param steps Počet kroků
     */
    void assignStatsToPlayes(int id, String gt, String time, int steps) {

	Coords c = map.getPlayer(id).getCoords();

	XMapField f = map.fieldAt(c);

	String text;
	text = "<html>"
		+ "Player:    " + id + "<br>"
		+ "Steps: " + steps + "<br>"
		+ "Play time: " + time + "<br>"
		+ "Game time: " + gt + "<br>"
		+ "</html>";

	f.setToolTipText(text);

    }
    
    /**
     * Převede časový interval v milisekundách do řetězcové reprezentace čitelné
     * pro člověka.
     * @param millis Délka intervalu v milisekundách
     * @return Řetězcová reprezentace časového intervalu
     */
    private String formatTimeInMilis(long millis) {

	return String.format("%d min, %d sec",
		TimeUnit.MILLISECONDS.toMinutes(millis),
		TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
	);

    }
    
    /**
     * Nastaví pohled mapy na hráče. Pokud je to možné, pokusí se hráče zobrazi
     * uprostřed okna.
     * @param id Sledovaný hráč
     */
    public void locatePlayer(int id) {
	Coords c = map.getPlayer(id).getCoords();
	
	int size = map.fieldAt(new Coords(0, 0)).getWidth(); //zjisteni sirky policka

	JScrollBar hsb = mapScrollPane.getHorizontalScrollBar();
	JScrollBar vsb = mapScrollPane.getVerticalScrollBar();

	JViewport v = mapScrollPane.getViewport();

	int widthHalf = v.getWidth() / 2;
	int heightHalf = v.getHeight() / 2;

	int viewx = c.getCol() * size - widthHalf;
	int viewy = c.getRow() * size - heightHalf;

	try {
	    hsb.setValue(viewx);
	    vsb.setValue(viewy);

	    map.repaint();
	    map.revalidate();
	} catch (Exception ex) { //neni potreba hlasit
	    //System.err.println("scroll failed");
	}
    }

}
