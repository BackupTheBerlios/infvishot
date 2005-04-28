/**
 * Created on 26.03.2005
 * Project: InfVis
 * @author: harald
 */

package gui.components;

import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

import sys.helpers.TimeMeasureObject;
import sys.main.SysCore;
import sys.sql.managers.SQLMosaicDataListManager;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import javax.swing.JButton;
import gui.mosaic.MainWindow;
import javax.swing.JComboBox;

public class InternalMosaicFrm extends JInternalFrame {
    private static SysCore sysCore = null;
    private JMenuItem jMenuItem = null;    
	private javax.swing.JPanel jContentPane = null;

	private JPanel jPanel = null;
	private JButton jButton = null;
	private JPanel jPanel1 = null;
	private MainWindow mainWindow = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	public JComboBox jComboBox = null;
	public JComboBox jComboBox1 = null;
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
	
	public void repaintPlot(){
	    if (sysCore.isDebug())
	        System.out.println("Start fetching data...");
	    
	    
	    SQLMosaicDataListManager sscdlm = new SQLMosaicDataListManager(sysCore,sysCore.getMainFrm().isettingsfrm.jComboBox.getSelectedItem().toString(),jComboBox.getSelectedItem().toString(), jComboBox1.getSelectedItem().toString(),true);
	    /*for (int i=0; i<sscdlm.getDataArray().length; i++)
	        for (int j=0; j<sscdlm.getDataArray()[i].length; j++)
	            System.out.println(sscdlm.getDataArray()[i][j]);
	    */
	    if (sysCore.isDebug())
	        System.out.println("End fetching data...");
	    
	    //jScatterplotPanel.setSQLSPManager(sscdlm);
	    
	    //jScatterplotPanel.dispData = sscdlm.getDataVector();
	    
	    TimeMeasureObject tmo = new TimeMeasureObject();
	    tmo.start();
	    //jScatterplotPanel.fillWithData();
	    //jScatterplotPanel.updateUI();
	    String[] names =  new String[2];
	    names[0] = "Spalte1";
		names[1] = "Spalte2";
		
		//jPanel1.removeAll(); //H
		//jPanel1.add(new MainWindow(sscdlm.getDataArray(),names), java.awt.BorderLayout.CENTER); //H
		
	    //mainWindow.setData(sscdlm.getDataArray(),names);
	    tmo.stop();
	    sysCore.getMainFrm().iperformancefrm.addTimeRow(1,sscdlm.getTime().getTimeDiff(),tmo.getTimeDiff(),"PaintMosaic",sscdlm.getDataArray()[0].length);
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
			jLabel1 = new JLabel();
			jLabel = new JLabel();
			jPanel = new JPanel();
			jLabel.setText("Spalte 1:");
			jLabel1.setText("Spalte 2:");
			jPanel.add(jLabel, null);
			jPanel.add(getJComboBox(), null);
			jPanel.add(jLabel1, null);
			jPanel.add(getJComboBox1(), null);
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
			jButton.setText("neu Zeichnen");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					repaintPlot();
				}
			});
		}
		return jButton;
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
			jPanel1.add(getMainWindow(), java.awt.BorderLayout.CENTER);
		}
		return jPanel1;
	}
	/**
	 * This method initializes mainWindow	
	 * 	
	 * @return gui.mosaic.MainWindow	
	 */    
	private MainWindow getMainWindow() {
		if (mainWindow == null) {
			mainWindow = new MainWindow();
		}
		return mainWindow;
	}
	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
		}
		return jComboBox;
	}
	/**
	 * This method initializes jComboBox1	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox getJComboBox1() {
		if (jComboBox1 == null) {
			jComboBox1 = new JComboBox();
		}
		return jComboBox1;
	}
     }  //  @jve:decl-index=0:visual-constraint="10,30"
