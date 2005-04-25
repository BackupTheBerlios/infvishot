/**
 * Created on 22.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package gui.main;

import gui.components.*;
import gui.components.JCSVImportFrm;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

import sys.main.InfVisException;
import sys.main.SysCore;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import javax.swing.JDesktopPane;

public class MainFrm extends JFrame {
    private static SysCore sysCore = null;
    public InternalScatterFrm iscatterfrm = null;
    public InternalMosaicFrm imosaicfrm = null;
    public InternalSettingsFrm isettingsfrm = null;
    
	private javax.swing.JPanel jContentPane = null;

	private JMenuBar jJMenuBar = null;
	private JMenu jMenu = null;
	private JMenu jMenu2 = null;
	private JMenu jMenu3 = null;
	private JMenuItem jMenuItem = null;
	private JMenuItem jMenuItem1 = null;
	private JMenuItem jMenuItem2 = null;
	private JMenuItem jMenuItem6 = null;
	public JDesktopPane jDesktopPane = null;
	
	public JMenu jMenu1 = null;
	private JMenu jMenu4 = null;
	private JMenuItem jMenuItem3 = null;
	/**
	 * This is the default constructor
	 */
	public MainFrm(SysCore _sysCore) {
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
	    this.setSize(770, 582);
		this.setContentPane(getJContentPane());
		this.setTitle(sysCore.getVersion());
		this.setJMenuBar(getJJMenuBar());
		this.addWindowListener(new java.awt.event.WindowAdapter() {   
		 
			public void windowClosing(java.awt.event.WindowEvent e) {    
			    sysCore.shutDown();
			}
		});
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (d.width - getSize().width ) / 2,(d.height- getSize().height) / 2 );
	}
	/* 
	 * Functions
	 */
	public void loadData(){
	    sysCore.setMainFrm(this);
	    
	    //Scatterplot
	    iscatterfrm = new InternalScatterFrm(sysCore);
        jDesktopPane.add(iscatterfrm);
        iscatterfrm.setVisible(true);
        
        //Mosaic-View
        imosaicfrm = new InternalMosaicFrm(sysCore);
        jDesktopPane.add(imosaicfrm);
        imosaicfrm.setVisible(true);
        
        //Settings-frame
        isettingsfrm = new InternalSettingsFrm(sysCore);
        jDesktopPane.add(isettingsfrm);
        isettingsfrm.setVisible(true);
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
			jContentPane.add(getJDesktopPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */    
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getJMenu());
			jJMenuBar.add(getJMenu4());
			jJMenuBar.add(getJMenu2());
			jJMenuBar.add(getJMenu1());
			jJMenuBar.add(getJMenu3());
		}
		return jJMenuBar;
	}
	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu() {
		if (jMenu == null) {
			jMenu = new JMenu();
			jMenu.setText("Datei");
			jMenu.add(getJMenuItem1());
		}
		return jMenu;
	}
	/**
	 * This method initializes jMenu2	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu2() {
		if (jMenu2 == null) {
			jMenu2 = new JMenu();
			jMenu2.setText("Einstellungen");
			jMenu2.add(getJMenuItem2());
			jMenu2.add(getJMenuItem6());
		}
		return jMenu2;
	}
	/**
	 * This method initializes jMenu3	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu3() {
		if (jMenu3 == null) {
			jMenu3 = new JMenu();
			jMenu3.setText("Hilfe");
			jMenu3.add(getJMenuItem());
		}
		return jMenu3;
	}
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem() {
		if (jMenuItem == null) {
			jMenuItem = new JMenuItem();
			jMenuItem.setText("Info");
			jMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					new InfVisException("Info","Informationsvisualisierungstool for VrVis.at \nby Harald Meyer, Tobias Schleser, Oliver Hörbinger",false).showInfoDialogMessage();
				}
			});
		}
		return jMenuItem;
	}
	/**
	 * This method initializes jMenuItem1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem1() {
		if (jMenuItem1 == null) {
			jMenuItem1 = new JMenuItem();
			jMenuItem1.setText("Beenden");
			jMenuItem1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					sysCore.shutDown();
				}
			});
		}
		return jMenuItem1;
	}
	/**
	 * This method initializes jMenuItem2	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem2() {
		if (jMenuItem2 == null) {
			jMenuItem2 = new JMenuItem();
			jMenuItem2.setText("Datenbank");
			jMenuItem2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
				    new DatabaseSettingsDlg(sysCore).setVisible(true);
				}
			});
		}
		return jMenuItem2;
	}
	/**
	 * This method initializes jMenuItem6	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem6() {
		if (jMenuItem6 == null) {
			jMenuItem6 = new JMenuItem();
			jMenuItem6.setText("Allgemein");
			jMenuItem6.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					new SettingsDlg(sysCore).setVisible(true);
				}
			});
		}
		return jMenuItem6;
	}
	/**
	 * This method initializes jDesktopPane	
	 * 	
	 * @return javax.swing.JDesktopPane	
	 */    
	private JDesktopPane getJDesktopPane() {
		if (jDesktopPane == null) {
			jDesktopPane = new JDesktopPane();
		}
		return jDesktopPane;
	}
	/**
	 * This method initializes jMenu1	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu1() {
		if (jMenu1 == null) {
			jMenu1 = new JMenu();
			jMenu1.setText("Fenster");
		}
		return jMenu1;
	}
	/**
	 * This method initializes jMenu4	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu4() {
		if (jMenu4 == null) {
			jMenu4 = new JMenu();
			jMenu4.setText("Tools");
			jMenu4.add(getJMenuItem3());
		}
		return jMenu4;
	}
	/**
	 * This method initializes jMenuItem3	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem3() {
		if (jMenuItem3 == null) {
			jMenuItem3 = new JMenuItem();
			jMenuItem3.setText("CSV-Import");
			jMenuItem3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
				    JCSVImportFrm jcsvfrm = new JCSVImportFrm(sysCore);
				    jcsvfrm.setVisible(true);
				}
			});
		}
		return jMenuItem3;
	}
   }  //  @jve:decl-index=0:visual-constraint="10,10"