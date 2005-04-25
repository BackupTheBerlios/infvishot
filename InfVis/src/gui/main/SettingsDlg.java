/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package gui.main;

import javax.swing.JDialog;

import sys.helpers.*;
import sys.main.SysCore;

import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;

public class SettingsDlg extends JDialog {
    private static SysCore sysCore = null;
	private javax.swing.JPanel jContentPane = null;
	
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private JLabel jLabel = null;
	private JRadioButton jRadioButton = null;
	private JRadioButton jRadioButton1 = null;
	private JTabbedPane jTabbedPane = null;
	private JPanel jPanel2 = null;
	private JPanel jPanel7 = null;
	private JCheckBox cbxProxyEnable = null;
	private JLabel jLabel2 = null;
	private JTextField edtProxyServer = null;
	private JLabel jLabel3 = null;
	private JTextField edtProxyPort = null;
	private JLabel jLabel4 = null;
	private JTextField edtProxyUsername = null;
	private JLabel jLabel5 = null;
	private JPasswordField edtProxyPassword = null;
	private JCheckBox cbxProxyAuthentication = null;
	private JCheckBox cbxSocksProxy = null;
	private JPanel jPanel8 = null;
	private JPanel jPanel9 = null;
	private JPanel jPanel10 = null;
	/**
	 * This is the default constructor
	 */
	public SettingsDlg(SysCore _sysCore) {
		super();
		sysCore = _sysCore;
		initialize();
		loadData();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setModal(true);
		this.setResizable(false);
		this.setTitle("Allgemeine Einstellungen");
		this.setSize(496, 298);
		this.setContentPane(getJContentPane());
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (d.width - getSize().width ) / 2,(d.height- getSize().height) / 2 );
		
	}
	/**
	 * Functions
	 */
	
	private void loadData() {
	    if (sysCore.getProps().getProperty("decimalseperatorsign") != null) {
	        if (sysCore.getProps().getProperty("decimalseperatorsign").equalsIgnoreCase(",")){
	        	jRadioButton1.setSelected(true);
	    	}
	    	else {
	        	jRadioButton.setSelected(true);
	    	}
	    }
	    
	    //Proxy settings
	    if (sysCore.getProps().getProperty("proxyEnable") != null) {
	        cbxProxyEnable.setSelected(Boolean.parseBoolean(sysCore.getProps().getProperty("proxyEnable")));
	    }
	    
	    if (sysCore.getProps().getProperty("proxySocks") != null) {
	        cbxSocksProxy.setSelected(Boolean.parseBoolean(sysCore.getProps().getProperty("proxySocks")));
	    }
	    
	    if (sysCore.getProps().getProperty("proxyAuthentication") != null) {
	        cbxProxyAuthentication.setSelected(Boolean.parseBoolean(sysCore.getProps().getProperty("proxyAuthentication")));
	    }
	    
	    if (sysCore.getProps().getProperty("proxyServer") != null) {
	        edtProxyServer.setText(sysCore.getProps().getProperty("proxyServer"));
	    }
	    
	    if (sysCore.getProps().getProperty("proxyPort") != null) {
	        edtProxyPort.setText(sysCore.getProps().getProperty("proxyPort"));
	    }
	    
	    if (sysCore.getProps().getProperty("proxyUsername") != null) {
	        edtProxyUsername.setText(sysCore.getProps().getProperty("proxyUsername"));
	    }
	    
	    if (sysCore.getProps().getProperty("proxyPassword") != null) {
	        edtProxyPassword.setText(sysCore.getProps().getProperty("proxyPassword"));
	    }
	}
	
	private void saveData() {
	    //General settings
	    if (jRadioButton1.isSelected()){
	        sysCore.getProps().setProperty("decimalseperatorsign",",");
	    }
	    else {
	        sysCore.getProps().setProperty("decimalseperatorsign",".");
	    }
	    
	    //Proxy settings
	    sysCore.getProps().setProperty("proxyEnable",String.valueOf(cbxProxyEnable.isSelected()));
	    sysCore.getProps().setProperty("proxySocks",String.valueOf(cbxSocksProxy.isSelected()));
	    sysCore.getProps().setProperty("proxyAuthentication",String.valueOf(cbxProxyAuthentication.isSelected()));
	    sysCore.getProps().setProperty("proxyServer",edtProxyServer.getText());
	    sysCore.getProps().setProperty("proxyPort",edtProxyPort.getText());
	    sysCore.getProps().setProperty("proxyUsername",edtProxyUsername.getText());
	    sysCore.getProps().setProperty("proxyPassword",edtProxyPassword.getText());
	    
	    sysCore.setProxy();
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getJPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getJPanel1(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			jPanel = new JPanel();
			jPanel.setLayout(flowLayout1);
			flowLayout1.setAlignment(java.awt.FlowLayout.RIGHT);
			jPanel.add(getJButton1(), null);
			jPanel.add(getJButton(), null);
		}
		return jPanel;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jLabel = new JLabel();
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BorderLayout());
			jLabel.setText("Dezimaltrennzeichen:");
			jPanel1.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
		}
		return jPanel1;
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
				    saveData();
				    JOptionPane.showMessageDialog(null,"Um Dateninkonsistenzen zu vermeiden, sollten sie das Programm neu starten!", "Hinweis", JOptionPane.INFORMATION_MESSAGE );
				    dispose();
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
					dispose();
				}
			});
		}
		return jButton1;
	}
	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getJRadioButton() {
		if (jRadioButton == null) {
			jRadioButton = new JRadioButton();
			jRadioButton.setText("Punkt (.)");
			jRadioButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
				    jRadioButton.setSelected(true);
					jRadioButton1.setSelected(false);
				}
			});
		}
		return jRadioButton;
	}
	/**
	 * This method initializes jRadioButton1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getJRadioButton1() {
		if (jRadioButton1 == null) {
			jRadioButton1 = new JRadioButton();
			jRadioButton1.setText("Komma (,)");
			jRadioButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jRadioButton.setSelected(false);
					jRadioButton1.setSelected(true);
				}
			});
		}
		return jRadioButton1;
	}
	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */    
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("Allgemein", null, getJPanel2(), null);
			jTabbedPane.addTab("Proxy", null, getJPanel7(), null);
		}
		return jTabbedPane;
	}
	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			FlowLayout flowLayout11 = new FlowLayout();
			jPanel2 = new JPanel();
			jPanel2.setLayout(flowLayout11);
			flowLayout11.setAlignment(java.awt.FlowLayout.LEFT);
			jPanel2.add(jLabel, null);
			jPanel2.add(getJRadioButton(), null);
			jPanel2.add(getJRadioButton1(), null);
		}
		return jPanel2;
	}
	/**
	 * This method initializes jPanel7	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel7() {
		if (jPanel7 == null) {
			jLabel5 = new JLabel();
			jLabel4 = new JLabel();
			jLabel3 = new JLabel();
			jLabel2 = new JLabel();
			jPanel7 = new JPanel();
			jPanel7.setLayout(null);
			jLabel2.setText("Server:");
			jLabel2.setBounds(5, 7, 41, 16);
			jLabel3.setText("Port:     ");
			jLabel3.setBounds(5, 32, 42, 16);
			jLabel4.setText("Benutzername:");
			jLabel4.setBounds(5, 36, 86, 16);
			jLabel5.setText("Passwort:         ");
			jLabel5.setBounds(5, 61, 85, 16);
			jPanel7.add(getJPanel8(), null);
			jPanel7.add(getJPanel9(), null);
			jPanel7.add(getJPanel10(), null);
		}
		return jPanel7;
	}
	/**
	 * This method initializes cbxProxyEnable	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getCbxProxyEnable() {
		if (cbxProxyEnable == null) {
			cbxProxyEnable = new JCheckBox();
			cbxProxyEnable.setText("aktivieren");
		}
		return cbxProxyEnable;
	}
	/**
	 * This method initializes edtProxyServer	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEdtProxyServer() {
		if (edtProxyServer == null) {
			edtProxyServer = new JTextField();
			edtProxyServer.setPreferredSize(new java.awt.Dimension(100,20));
			edtProxyServer.setBounds(51, 5, 100, 20);
		}
		return edtProxyServer;
	}
	/**
	 * This method initializes edtProxyPort	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEdtProxyPort() {
		if (edtProxyPort == null) {
			edtProxyPort = new JTextField();
			edtProxyPort.setPreferredSize(new java.awt.Dimension(100,20));
			edtProxyPort.setBounds(52, 30, 100, 20);
		}
		return edtProxyPort;
	}
	/**
	 * This method initializes edtProxyUsername	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEdtProxyUsername() {
		if (edtProxyUsername == null) {
			edtProxyUsername = new JTextField();
			edtProxyUsername.setPreferredSize(new java.awt.Dimension(100,20));
			edtProxyUsername.setBounds(96, 34, 100, 20);
		}
		return edtProxyUsername;
	}
	/**
	 * This method initializes edtProxyPassword	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */    
	private JPasswordField getEdtProxyPassword() {
		if (edtProxyPassword == null) {
			edtProxyPassword = new JPasswordField();
			edtProxyPassword.setPreferredSize(new java.awt.Dimension(100,20));
			edtProxyPassword.setBounds(95, 59, 100, 20);
		}
		return edtProxyPassword;
	}
	/**
	 * This method initializes cbxProxyAuthentication	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getCbxProxyAuthentication() {
		if (cbxProxyAuthentication == null) {
			cbxProxyAuthentication = new JCheckBox();
			cbxProxyAuthentication.setText("Authentifizierung");
			cbxProxyAuthentication.setBounds(5, 5, 121, 24);
		}
		return cbxProxyAuthentication;
	}
	/**
	 * This method initializes cbxSocksProxy	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getCbxSocksProxy() {
		if (cbxSocksProxy == null) {
			cbxSocksProxy = new JCheckBox();
			cbxSocksProxy.setText("Socks Proxy");
		}
		return cbxSocksProxy;
	}
	/**
	 * This method initializes jPanel8	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel8() {
		if (jPanel8 == null) {
			jPanel8 = new JPanel();
			jPanel8.setBounds(5, 5, 193, 34);
			jPanel8.add(getCbxProxyEnable(), null);
			jPanel8.add(getCbxSocksProxy(), null);
		}
		return jPanel8;
	}
	/**
	 * This method initializes jPanel9	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel9() {
		if (jPanel9 == null) {
			jPanel9 = new JPanel();
			jPanel9.setLayout(null);
			jPanel9.setBounds(5, 44, 180, 59);
			jPanel9.add(jLabel2, null);
			jPanel9.add(getEdtProxyServer(), null);
			jPanel9.add(jLabel3, null);
			jPanel9.add(getEdtProxyPort(), null);
		}
		return jPanel9;
	}
	/**
	 * This method initializes jPanel10	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel10() {
		if (jPanel10 == null) {
			jPanel10 = new JPanel();
			jPanel10.setLayout(null);
			jPanel10.setBounds(4, 107, 216, 87);
			jPanel10.add(getCbxProxyAuthentication(), null);
			jPanel10.add(jLabel4, null);
			jPanel10.add(getEdtProxyUsername(), null);
			jPanel10.add(jLabel5, null);
			jPanel10.add(getEdtProxyPassword(), null);
		}
		return jPanel10;
	}
           }  //  @jve:decl-index=0:visual-constraint="10,10"