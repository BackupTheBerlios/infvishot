/**
 * Created on 25.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package gui.components;

import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

import sys.helpers.TimeMeasureObject;
import sys.main.SysCore;
import sys.sql.managers.SQLMosaicDataListManager;
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

	public JScatterplotPanel jScatterplotPanel = null;
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
	
	public void repaintPlot(String _table){
	    if (sysCore.isDebug())
	        System.out.println("Start fetching data...");
	    
	    SQLScatterDataListManager sscdlm = new SQLScatterDataListManager(sysCore,_table,jComboBoxXAxis.getSelectedItem().toString(), jComboBoxYAxis.getSelectedItem().toString(),sysCore.getMainFrm().imosaicfrm.jComboBox.getSelectedItem().toString(),sysCore.getMainFrm().imosaicfrm.jComboBox1.getSelectedItem().toString(),true);
	    
	    if (sysCore.isDebug())
	        System.out.println("End fetching data...");
	    
	    jScatterplotPanel.setSQLSPManager(sscdlm);
	    
	    //jScatterplotPanel.dispData = sscdlm.getDataVector();
	    
	    TimeMeasureObject tmo = new TimeMeasureObject();
	    tmo.start();
	    jScatterplotPanel.fillWithData();
	    jScatterplotPanel.updateUI();
	    tmo.stop();
	    
	    //Mosaic
	    TimeMeasureObject tmo1 = new TimeMeasureObject();
	    tmo1.start();
	    
	    String[] names =  new String[2];
	    names[0] = sysCore.getMainFrm().imosaicfrm.jComboBox.getSelectedItem().toString();
		names[1] = sysCore.getMainFrm().imosaicfrm.jComboBox1.getSelectedItem().toString();
		
		sysCore.getMainFrm().imosaicfrm.mainWindow.setSQLManager(new SQLMosaicDataListManager(sysCore,_table,sysCore.getMainFrm().imosaicfrm.jComboBox.getSelectedItem().toString(), sysCore.getMainFrm().imosaicfrm.jComboBox1.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxXAxis.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxYAxis.getSelectedItem().toString(),false));
        sysCore.getMainFrm().imosaicfrm.mainWindow.setCatCnt(sysCore.getMainFrm().imosaicfrm.jSpinField1.getValue());
	    sysCore.getMainFrm().imosaicfrm.mainWindow.setData(sscdlm.getStringDataArray(),names);
	    sysCore.getMainFrm().imosaicfrm.mainWindow.Markers(sysCore.getMainFrm().imosaicfrm.jCheckBox.isSelected());
	    
	    tmo1.stop();
	    
	    sysCore.getMainFrm().iperformancefrm.addTimeRow(1,sscdlm.getTime().getTimeDiff(),tmo.getTimeDiff(),tmo1.getTimeDiff(),"PaintScatter",sscdlm.getDataArray().length);
	}
	
	public void repaintPlot(){
	    repaintPlot(sysCore.getMainFrm().isettingsfrm.jComboBox.getSelectedItem().toString());
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
			jScatterplotPanel = new JScatterplotPanel(this.getSize(),sysCore,null);
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
