/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package gui.main;

import javax.swing.JDialog;

import sys.*;
import sys.db.*;
import sys.main.InfVisException;
import sys.main.SysCore;

import java.awt.*;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JButton;

public class DatabaseSettingsDlg extends JDialog {
    private static SysCore sysCore = null;
    private boolean wasAbort = false;
    
	private javax.swing.JPanel jContentPane = null;

	private JLabel jLabel = null;
	private JTextField edtServer = null;
	private JLabel jLabel1 = null;
	private JTextField edtDatabase = null;
	private JLabel jLabel2 = null;
	private JTextField edtUsername = null;
	private JLabel jLabel3 = null;
	private JPasswordField edtPassword = null;
	private JPanel jPanel = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	/**
	 * This is the default constructor
	 */
	public DatabaseSettingsDlg(SysCore _sysCore) {
		super();
		sysCore = _sysCore;
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(322, 179);
		this.setContentPane(getJContentPane());
		this.setTitle("Datenbankeinstellungen");
		this.setModal(true);
		this.setResizable(false);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
			    if (!storeSettings()) {
			        sysCore.shutDown();
			    }
			    
			}
		});
		this.setLocation( (d.width - getSize().width ) / 2,(d.height- getSize().height) / 2 );
		
		//Load fields
		setFields();
	}
	
	/*
	 * Functions for DatabaseSettingsDlg
	 */
	private void setFields(){
	    if (sysCore.getDBProps() != null){
	        this.edtDatabase.setText(sysCore.getDBProps().getDBName());
	        this.edtUsername.setText(sysCore.getDBProps().getDBUsername());
	        this.edtPassword.setText(sysCore.getDBProps().getDBPassword());
	        this.edtServer.setText(sysCore.getDBProps().getDBUrl());
	    }
	}
	
	private boolean storeSettings(){
	    DBProps dbprops = new DBProps();
	    
	    dbprops.setDBName(this.edtDatabase.getText());
	    dbprops.setDBUrl(this.edtServer.getText());
	    dbprops.setDBUsername(this.edtUsername.getText());
	    dbprops.setDBPassword(this.edtPassword.getText());
	    dbprops.setDBType(1); //MySQL
	    
	    if (!sysCore.load(dbprops)){
	        new InfVisException("Datenbankfehler","Es konnte keine Verbindung zur Datenbank hergestellt werden!",false).showDialogMessage();
	        return false;
	    }
	    else {
	        sysCore.setDBProps();
	        dispose();
	        return true;
	    }
	}
	
	public boolean wasAbort(){
	    return wasAbort;
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jLabel3 = new JLabel();
			jLabel2 = new JLabel();
			jLabel1 = new JLabel();
			jLabel = new JLabel();
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(null);
			jLabel.setText("Server:");
			jLabel.setPreferredSize(new java.awt.Dimension(100,16));
			jLabel.setBounds(5, 7, 100, 16);
			jLabel1.setBounds(5, 32, 100, 16);
			jLabel2.setBounds(5, 57, 100, 16);
			jLabel3.setBounds(5, 82, 100, 16);
			jContentPane.add(jLabel, null);
			jContentPane.add(getEdtServer(), null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getEdtDatabase(), null);
			jContentPane.add(jLabel2, null);
			jContentPane.add(getEdtUsername(), null);
			jContentPane.add(jLabel3, null);
			jContentPane.add(getEdtPassword(), null);
			jContentPane.add(getJPanel(), null);
			jLabel1.setText("Datenbank:");
			jLabel1.setPreferredSize(new java.awt.Dimension(100,16));
			jLabel2.setText("Benutzername:");
			jLabel2.setPreferredSize(new java.awt.Dimension(100,16));
			jLabel3.setText("Password");
			jLabel3.setPreferredSize(new java.awt.Dimension(100,16));
		}
		return jContentPane;
	}
	/**
	 * This method initializes edtServer	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEdtServer() {
		if (edtServer == null) {
			edtServer = new JTextField();
			edtServer.setPreferredSize(new java.awt.Dimension(200,20));
			edtServer.setBounds(110, 5, 200, 20);
		}
		return edtServer;
	}
	/**
	 * This method initializes edtDatabase	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEdtDatabase() {
		if (edtDatabase == null) {
			edtDatabase = new JTextField();
			edtDatabase.setPreferredSize(new java.awt.Dimension(200,20));
			edtDatabase.setBounds(110, 30, 200, 20);
		}
		return edtDatabase;
	}
	/**
	 * This method initializes edtUsername	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEdtUsername() {
		if (edtUsername == null) {
			edtUsername = new JTextField();
			edtUsername.setPreferredSize(new java.awt.Dimension(200,20));
			edtUsername.setBounds(110, 55, 200, 20);
		}
		return edtUsername;
	}
	/**
	 * This method initializes edtPassword	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */    
	private JPasswordField getEdtPassword() {
		if (edtPassword == null) {
			edtPassword = new JPasswordField();
			edtPassword.setPreferredSize(new java.awt.Dimension(200,20));
			edtPassword.setBounds(110, 80, 200, 20);
		}
		return edtPassword;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			FlowLayout flowLayout6 = new FlowLayout();
			jPanel = new JPanel();
			jPanel.setLayout(flowLayout6);
			jPanel.setPreferredSize(new java.awt.Dimension(300,30));
			jPanel.setBounds(5, 105, 304, 30);
			flowLayout6.setAlignment(java.awt.FlowLayout.RIGHT);
			jPanel.add(getJButton1(), null);
			jPanel.add(getJButton(), null);
		}
		return jPanel;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Speichern");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) { 
				    storeSettings();
				}
			});
		}
		return jButton;
	}
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Abbrechen");
			
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) { 
				    wasAbort = true;
				    dispose();
				}
			});
		}
		return jButton1;
	}
  }  //  @jve:decl-index=0:visual-constraint="10,10"
