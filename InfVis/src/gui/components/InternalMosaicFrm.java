/**
 * Created on 26.03.2005
 * Project: InfVis
 * @author: harald
 */

package gui.components;

import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import sys.main.SysCore;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class InternalMosaicFrm extends JInternalFrame {
    private static SysCore sysCore = null;
    private JMenuItem jMenuItem = null;    
	private javax.swing.JPanel jContentPane = null;

	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JPanel jPanel1 = null;
	private JLabel jLabel1 = null;
	/**
	 * This is the default constructor
	 */
	public InternalMosaicFrm(SysCore _sysCore) {
	    super();
	    sysCore = _sysCore;
		initialize();
		
		loadData();
	}
	
	/*
	 * 
	 */
	private void loadData(){
	    
	    //Add to "Windows"
        jMenuItem = new JMenuItem();
		jMenuItem.setText(this.getTitle());
		final InternalMosaicFrm tmpiframe = this;
		jMenuItem.addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {
			    try {
			        tmpiframe.setSelected(true);
			    }
			    catch (Exception exc){   
			    }
			}
		});
		sysCore.getMainFrm().jMenu1.add(jMenuItem);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setResizable(true);
		this.setMaximizable(false);
		this.setTitle("Mosaic-View");
		this.setSize(612, 499);
		this.setContentPane(getJContentPane());
		this.setVisible(true);
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new BorderLayout());
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
			jLabel = new JLabel();
			jPanel = new JPanel();
			jLabel.setText("Hier kommen Mosaicview-spezifische Dinge rein...");
			jPanel.add(jLabel, null);
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
			jLabel1 = new JLabel();
			jPanel1 = new JPanel();
			jPanel1.setBackground(new java.awt.Color(153,255,255));
			jLabel1.setText("Mosaicview");
			jPanel1.add(jLabel1, null);
		}
		return jPanel1;
	}
 }  //  @jve:decl-index=0:visual-constraint="10,30"
