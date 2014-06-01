/**
 * Modální dialog pro vytvoření / připojení ke hře.
 * Autoři: David Kovařík, Tomáš Bruckner
 */
package ija.client.gui;

import ija.protocol.Protocol;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class SetGameDialog extends JDialog {

    private int selected = -1; //implicitni hodnota - nic nebylo vybrano
    private String games;
    private String maps;

    private List gameList;

    private BufferedReader pwin;
    private PrintWriter pwout;
    private JButton bttNew;
    private JButton bttOk;

    GameStatsPanel statsPanel;
    SetGameStatsPanel setStatsPanel;

    private ArrayList<String> stats = new ArrayList<String>();

    JPanel beingPlayedPanel;
    JPanel createGamePanel;

    public SetGameDialog(BufferedReader pwin, PrintWriter pwout) {
	super((Window) null);
	setModal(true);

	this.pwin = pwin;
	this.pwout = pwout;

	initComponents();
    }

    private void initComponents() {

	initData(); // seznam map do games

	setTitle("Select or create game");
	//setPreferredSize(new Dimension(200, 200));
	setDefaultCloseOperation(HIDE_ON_CLOSE);
	setLayout(new BorderLayout());

	initBeingPlayedPanel();
	initCreateGamePanel();

	JTabbedPane tab = new JTabbedPane();
	tab.add("Rozehrané", beingPlayedPanel);
	tab.add("Založit", createGamePanel);

	add(tab, BorderLayout.CENTER);

	pack();
	setLocationRelativeTo(null);
	setVisible(true);

    }

    private void initCreateGamePanel() {
	createGamePanel = new JPanel(new BorderLayout());

	JPanel fieldPanel = new JPanel(new GridLayout(4, 1, 5, 5));

	setStatsPanel = new SetGameStatsPanel(this.maps);

	createGamePanel.add(setStatsPanel, BorderLayout.CENTER);

	JPanel buttonPanel = new JPanel();

	JButton cancelBtt = new JButton("Cancel");
	JButton okBtt = new JButton("Create");

	cancelBtt.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
	    }
	});

	okBtt.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {

		createGame();
	    }

	});

	buttonPanel.add(cancelBtt);
	buttonPanel.add(okBtt);

	createGamePanel.add(buttonPanel, BorderLayout.SOUTH);

    }
    
    /**
     * Inicializuje panel pro vytvoření nové hry.
     */
    private void createGame() {
	String name = setStatsPanel.getNameValue();
	double delay = setStatsPanel.getDelayValue();
	int guardCount = setStatsPanel.getGuardCountValue();

	if (name.equals("")) {
	    JOptionPane.showMessageDialog(null, "No game selected");
	    return;
	}

	String msg = Protocol.CREATE_GAME_REQUEST + " " + name + " " + delay + " " + guardCount;
	pwout.println(msg); // posli zadost o novou mapu

	try {
	    // precti odpoved
	    msg = pwin.readLine();
	} catch (IOException ex) {

	    System.err.println("[CLIENT] Couldn't prerform I/O operation");
	    return;
	}

	// fail!
	if (msg.startsWith(Protocol.CREATE_GAME_SUCCESS)) {

	    int gameId = Integer.valueOf(msg.substring(Protocol.CREATE_GAME_SUCCESS.length() + 1));

	    selected = gameId;
	    dispose();

	} else {
	    System.err.println("[CLIENT] Map couldn't be created");

	    JOptionPane.showMessageDialog(null, "Map could not be created!");
	}
    }
    
    /**
     * Inicializuje hodnoty panelu rozehraných her.
     */
    private void initBeingPlayedPanel() {

	beingPlayedPanel = new JPanel(new BorderLayout());

	//games = "[0:a;2;2.0;1],[1:a;2;3.0;1],[5:a;4;2.0;1]";
	// odstrani ze zpravy prefix GAMES + 1 mezeru za nim -> dale nasleduje cislo
	gameList = new List();

	try {
	    String tmp = games.split(">")[1];

	    String[] parsed = tmp.split(",");

	    for (String s : parsed) {

		s = s.substring(1, s.length() - 1);
		stats.add(s);

		String[] gameParsed = s.split(";");

		gameList.add(gameParsed[0]);
	    }

	} catch (ArrayIndexOutOfBoundsException ex) {

	}

	gameList.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mouseClicked(MouseEvent e) {
		List list = (List) e.getSource();

		String[] items = list.getItems();

		int index = list.getSelectedIndex();

		try {
		    statsPanel.display(stats.get(index));
		} catch (ArrayIndexOutOfBoundsException ec) {

		}

	    }
	});

	beingPlayedPanel.add(gameList, BorderLayout.WEST);

	// --------------------- Statistiky -------------------------
	JPanel centralPanel = new JPanel(new BorderLayout());
	JPanel bttPanel = new JPanel();

	statsPanel = new GameStatsPanel();

	centralPanel.add(statsPanel, BorderLayout.CENTER);

	JButton okBtt = new JButton("Join");
	JButton cancelBtt = new JButton("Cancel");

	okBtt.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {

		try {
		    int selected = Integer.valueOf(
			    stats.get(gameList.getSelectedIndex()).split(":")[0]
		    );
		} catch (ArrayIndexOutOfBoundsException ex) {
		    JOptionPane.showMessageDialog(null, "No game to select!");
		    return;
		}
		    selectGame();
		    dispose();
	    }
	}
	);

	cancelBtt.addActionListener(
		new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e
		    ) {
			System.exit(0);
		    }
		}
	);

	bttPanel.add(cancelBtt, BorderLayout.SOUTH);

	bttPanel.add(okBtt, BorderLayout.SOUTH);

	centralPanel.add(bttPanel, BorderLayout.SOUTH);

	beingPlayedPanel.add(centralPanel, BorderLayout.CENTER);

    }

    /**
     * @return Vrací zvolenou hru
     */
    public int getSelected() {
	return selected;
    }

    /**
     * Vybere konkrétní hru.
     */
    public void selectGame() {

	String item = gameList.getSelectedItem();

	if (item == null) {
	    JOptionPane.showMessageDialog(this, "No game selected!");
	} else {
	    selected = Integer.valueOf(item.split(":")[0]);
	    dispose();
	}
    }

    /**
     * Inicializuje seznam rozheraných her a seznam dostupných map.
     */
    private void initData() {

	String message;

	try {
	    message = pwin.readLine();
	    this.games = message;

	    message = pwin.readLine();
	    this.maps = message;

	} catch (IOException ex) {
	    System.err.println("[CLIENT] Failed to recieve map list! " + ex);
	    return;
	}

    }

}
