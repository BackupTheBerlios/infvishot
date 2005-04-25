/**
 * Created on 25.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package gui.components;

import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import sys.main.SysCore;
import sys.sql.managers.SQLScatterDataListManager;

import javax.swing.JLabel;
import java.awt.BorderLayout;

import gui.scatterplot.JScatterplotPanel;

import javax.swing.JPanel;

import javax.swing.JComboBox;
import javax.swing.JButton;
public class InternalScatterFrm extends JInternalFrame {
    private static SysCore sysCore = null;
    private JMenuItem jMenuItem = null;    
	private javax.swing.JPanel jContentPane = null;

	private JScatterplotPanel jScatterplotPanel = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	public JComboBox jComboBoxXAxis = null;
	private JLabel jLabel1 = null;
	public JComboBox jComboBoxYAxis = null;
	private JButton jButton = null;
	/**
	 * This is the default constructor
	 */
	public InternalScatterFrm(SysCore _sysCore) {
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
		final InternalScatterFrm tmpiframe = this;
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
	
	public void repaintPlot(){
	    if (sysCore.isDebug())
	        System.out.println("Start fetching data...");
	    
	    SQLScatterDataListManager sscdlm = new SQLScatterDataListManager(sysCore,sysCore.getMainFrm().isettingsfrm.jComboBox.getSelectedItem().toString(),jComboBoxXAxis.getSelectedItem().toString(), jComboBoxYAxis.getSelectedItem().toString(),true);
	    
	    if (sysCore.isDebug())
	        System.out.println("End fetching data...");
	    
	    jScatterplotPanel.dispData = sscdlm.getDataVector();
	    	    
	    jScatterplotPanel.fillWithData();
	    jScatterplotPanel.updateUI();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setResizable(true);
		this.setMaximizable(false);
		this.setTitle("Scatter-Plot");
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
			jContentPane.add(getJScatterplotPanel(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getJPanel(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jScatterplotPanel	
	 * 	
	 * @return gui.scatterplot.JScatterplotPanel	
	 */    
	private JScatterplotPanel getJScatterplotPanel() {
		if (jScatterplotPanel == null) {
			jScatterplotPanel = new JScatterplotPanel(this.getSize(),sysCore);
		}
		return jScatterplotPanel;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jLabel1 = new JLabel();
			jLabel = new JLabel();
			jPanel = new JPanel();
			jLabel.setText("X-Achse:");
			jLabel1.setText("Y-Achse");
			jPanel.add(jLabel, null);
			jPanel.add(getJComboBoxXAxis(), null);
			jPanel.add(jLabel1, null);
			jPanel.add(getJComboBoxYAxis(), null);
			jPanel.add(getJButton(), null);
		}
		return jPanel;
	}
	/**
	 * This method initializes jComboBoxXAxis	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox getJComboBoxXAxis() {
		if (jComboBoxXAxis == null) {
			jComboBoxXAxis = new JComboBox();
		}
		return jComboBoxXAxis;
	}
	/**
	 * This method initializes jComboBoxYAxis	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox getJComboBoxYAxis() {
		if (jComboBoxYAxis == null) {
			jComboBoxYAxis = new JComboBox();
		}
		return jComboBoxYAxis;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("neu Zeichnen");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					repaintPlot();
				}
			});
		}
		return jButton;
	}
   }  //  @jve:decl-index=0:visual-constraint="10,30"
