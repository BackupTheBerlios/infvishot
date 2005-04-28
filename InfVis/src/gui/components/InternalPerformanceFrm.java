/**
 * Created on 28.04.2005
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

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.FlowLayout;
public class InternalPerformanceFrm extends JInternalFrame {
    private static SysCore sysCore = null;
    private JMenuItem jMenuItem = null;    
	private javax.swing.JPanel jContentPane = null;
	private int curID = 0;
	
	private JPanel jPanel1 = null;
	private JPanel jPanel = null;
	private JTabbedPane jTabbedPane = null;
	private JPanel jPanel2 = null;
	private JPanel jPanel3 = null;
	private JPanel jPanel4 = null;
	private PerformanceTable jTable = null;
	private JScrollPane jScrollPane = null;
	private JPanel jPanel5 = null;
	private JButton jButton = null;
	/**
	 * This is the default constructor
	 */
	public InternalPerformanceFrm(SysCore _sysCore) {
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
		final InternalPerformanceFrm tmpiframe = this;
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
	
	public void addTimeRow(long _time1, long _time2, String _typ){
	    Object[] ox = new Object[4];
	    ox[0] = new Integer(curID);
	    ox[1] = new Long(_time1);
	    ox[2] = new Long(_time2);
	    ox[3] = _typ;
	    
	    jTable.addRow(ox);
	    
	    //TODO: draw graph
	    
	    curID++;
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setResizable(true);
		this.setMaximizable(false);
		this.setTitle("Performance");
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
			jContentPane.add(getJPanel1(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BorderLayout());
			jPanel1.add(getJPanel(), java.awt.BorderLayout.WEST);
			jPanel1.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.add(getJPanel4(), null);
		}
		return jPanel;
	}
	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */    
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("Visualisiert", null, getJPanel2(), null);
			jTabbedPane.addTab("Messdaten", null, getJPanel3(), null);
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
			jPanel2 = new JPanel();
		}
		return jPanel2;
	}
	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setLayout(new BorderLayout());
			jPanel3.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
			jPanel3.add(getJPanel5(), java.awt.BorderLayout.SOUTH);
		}
		return jPanel3;
	}
	/**
	 * This method initializes jPanel4	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			jPanel4 = new JPanel();
			jPanel4.setPreferredSize(new java.awt.Dimension(200,10));
		}
		return jPanel4;
	}
	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */    
	private PerformanceTable getJTable() {
		if (jTable == null) {
			jTable = new PerformanceTable();
		}
		return jTable;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jPanel5	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel5() {
		if (jPanel5 == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			jPanel5 = new JPanel();
			jPanel5.setLayout(flowLayout1);
			flowLayout1.setAlignment(java.awt.FlowLayout.RIGHT);
			jPanel5.add(getJButton(), null);
		}
		return jPanel5;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Als CSV exportieren");
		}
		return jButton;
	}
          }  //  @jve:decl-index=0:visual-constraint="10,30"
