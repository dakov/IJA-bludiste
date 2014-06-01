/**
 * Dialog pro přihlášení k serveru.
 * Autoři: David Kovařík, Tomáš Bruckner
 */

package ija.client.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Modální dialog pro nastavení adresy serveru a čísla portu.
 */
public class LoginDialog extends JDialog  {

    private String server;
    private int port;
    
    private JTextField serverField;
    private JTextField portField;
    private JButton okBtt;
    private JButton cancelBtt;

    public LoginDialog() {
	super( (Window) null);
	
	initComponents();
    }

    private void initComponents() {
	setModal(true);

	setTitle("Připojit");
	setResizable(false);
	
	JPanel parentPanel = new JPanel();
	JPanel buttonPanel = new JPanel();
	
	
	JPanel labelPanel = new JPanel(new GridLayout(2,1,5,5));
	JPanel fieldPanel = new JPanel(new GridLayout(2,1,5,5));
	
	serverField = new JTextField(20);
	portField   = new JTextField(20);
	
	JLabel serverLbl = new JLabel("Server", JLabel.RIGHT);
	JLabel portLbl =   new JLabel("Port", JLabel.LEFT);
	
	setLayout(new BorderLayout());
	
	labelPanel.add(serverLbl);
	labelPanel.add(portLbl);
	
	fieldPanel.add(serverField);
	fieldPanel.add(portField);
	
	parentPanel.add(labelPanel);
	parentPanel.add(fieldPanel);
	
	
	add(parentPanel, BorderLayout.CENTER);
	
	okBtt = new JButton("Connect");
	
	okBtt.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		
		if (serverField.getText().equals("") || portField.getText().equals("")) {
		    JOptionPane.showMessageDialog(LoginDialog.this, "Empty fields");
		    return;
		}
		
		server = serverField.getText();
		
		try {
		    port = Integer.valueOf(portField.getText());
		} catch (NumberFormatException ex) {
		    JOptionPane.showMessageDialog(LoginDialog.this, "Not a valid port number");
		    return;
		}
		    
		dispose();
	    }
	});
	
	addKeyListener(new KeyAdapter() {

	    @Override
	    public void keyPressed(KeyEvent e) {
		okBtt.doClick();
	    }
	});
	
	
	cancelBtt = new JButton("Cancel");
	
	cancelBtt.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
	    }
	});
	
	addWindowListener(new WindowAdapter() {

	    @Override
	    public void windowClosing(WindowEvent e) {
		System.exit(0);
	    }

	});
	
	
	buttonPanel.add(cancelBtt);
	okBtt.setDefaultCapable(true);
	buttonPanel.add(okBtt);
	
	add(buttonPanel, BorderLayout.SOUTH);
	
	serverField.setText("localhost");
	portField.setText("1337");
	
	pack();
	setLocationRelativeTo(null);
	setVisible(true);
    }

    /**
     * @return Vrátí zadanou hodnotu z pole server.
     */
    public String getServer() {
	return server;
    }

    /**
     * @return Vrátí hodnotuz pole port.
     */
    public int getPort() {
	return port;
    }


}
