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
import sys.sql.managers.SQLScatterDataListManager;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import javax.swing.JButton;
import gui.mosaic.MainWindow;
import javax.swing.JComboBox;

import javax.swing.JCheckBox;
import gui.mosaic.JSpinField;
public class InternalMosaicFrm extends JInternalFrame {
    private static SysCore sysCore = null;
    private JMenuItem jMenuItem = null;    
	private javax.swing.JPanel jContentPane = null;
	private String[][] curData = null;
	private JPanel jPanel = null;
	private JButton jButton = null;
	public MainWindow mainWindow = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	public JComboBox jComboBox = null;
	public JComboBox jComboBox1 = null;
	public JCheckBox jCheckBox = null;
    private JLabel jLabel2 = null;
    public JSpinField jSpinField1 = null;
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
	    
	    
	    SQLMosaicDataListManager sscdlm = new SQLMosaicDataListManager(sysCore,sysCore.getMainFrm().isettingsfrm.jComboBox.getSelectedItem().toString(),jComboBox.getSelectedItem().toString(), jComboBox1.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxXAxis.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxYAxis.getSelectedItem().toString(),true);
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
	    names[0] = jComboBox.getSelectedItem().toString();
		names[1] = jComboBox1.getSelectedItem().toString();
		
		//jPanel1.removeAll(); //H
		//jPanel1.add(new MainWindow(sscdlm.getDataArray(),names), java.awt.BorderLayout.CENTER); //H
        mainWindow.setCatCnt(jSpinField1.getValue());
        curData = sscdlm.getDataArray();
		mainWindow.setData(sscdlm.getDataArray(),names);
		mainWindow.Markers(jCheckBox.isSelected());
		mainWindow.setSQLManager(sscdlm);
	    //mainWindow.setData(sscdlm.getDataArray(),names);
	    tmo.stop();
	    
	    //Scatterplot
	    TimeMeasureObject tmo1 = new TimeMeasureObject();
	    tmo1.start();
	    SQLScatterDataListManager tmpssdlm = new SQLScatterDataListManager(sysCore,sysCore.getMainFrm().isettingsfrm.jComboBox.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxXAxis.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxYAxis.getSelectedItem().toString(),sysCore.getMainFrm().imosaicfrm.jComboBox.getSelectedItem().toString(),sysCore.getMainFrm().imosaicfrm.jComboBox1.getSelectedItem().toString(),false);
	    sysCore.getMainFrm().iscatterfrm.jScatterplotPanel.setSQLSPManager(tmpssdlm);
	    sysCore.getMainFrm().iscatterfrm.jScatterplotPanel.fillWithData(sscdlm.getDoubleBounds(),sscdlm.getDoubleDataArray());
	    sysCore.getMainFrm().iscatterfrm.jScatterplotPanel.updateUI();
	    tmo1.stop();
	    
	    sysCore.getMainFrm().iperformancefrm.addTimeRow(1,sscdlm.getTime().getTimeDiff(),tmo1.getTimeDiff(),tmo.getTimeDiff(),"PaintMosaic",sscdlm.getDataArray()[0].length);
	}
	
	public void fillMosaic(double _minX, double _minY, double _maxX, double _maxY, String[][] data, boolean _add){
	    
	    if (data == null){
	        SQLMosaicDataListManager sqlspmanager = new SQLMosaicDataListManager(sysCore,sysCore.getMainFrm().isettingsfrm.jComboBox.getSelectedItem().toString(),jComboBox.getSelectedItem().toString(), jComboBox1.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxXAxis.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxYAxis.getSelectedItem().toString(),false);
	        sqlspmanager.setBounds(_minX,_minY,_maxX,_maxY,sysCore.getMainFrm().iscatterfrm.jComboBoxXAxis.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxYAxis.getSelectedItem().toString());
	        sqlspmanager.loadData();
	        
	        data = sqlspmanager.getDataArray();
	    }
        
	    if (_add){
	        System.out.println("ADD!!");
	        int tmpacnt = 0;
            int tmpbcnt = 0;
	        if (curData != null) 
	            if (curData.length > 0)
                    tmpacnt = curData[0].length;
	        if (data != null) 
	            if (data.length > 0)
	                tmpbcnt = data[0].length;
	        
            
	        String[][] t_data = new String[2][tmpacnt + tmpbcnt];
	        int t_lastcnt = 0;
	        
            if (tmpacnt > 0){
                for (int i=0; i<curData[0].length; i++){
                    t_data[0][i] = curData[0][i];
                    t_data[1][i] = curData[1][i];
                    t_lastcnt++;
                }
            }
	        
            if (tmpbcnt > 0){
                for (int i=0; i<data[0].length; i++){
                    t_data[0][t_lastcnt] = data[0][i];
                    t_data[1][t_lastcnt] = data[1][i];
                    t_lastcnt++;
                }
            }
            
	        data = t_data;
        }
	    
        curData = data;
        
	    TimeMeasureObject tmo = new TimeMeasureObject();
	    tmo.start();
	    String[] names =  new String[2];
	    names[0] = jComboBox.getSelectedItem().toString();
		names[1] = jComboBox1.getSelectedItem().toString();
        mainWindow.setCatCnt(jSpinField1.getValue());
	    mainWindow.fillRects(data,names);
	    tmo.stop();
	    
	    //TODO: sysCore.getMainFrm().iperformancefrm.addTimeRow(1,sqlspmanager.getTime().getTimeDiff(),tmo.getTimeDiff(),"MosaicFill",sqlspmanager.getDataArray()[0].length);
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
			jContentPane.add(getMainWindow(), java.awt.BorderLayout.CENTER);
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
			jLabel2 = new JLabel();
			jLabel2.setText("#:");
			jLabel1 = new JLabel();
			jLabel = new JLabel();
			jPanel = new JPanel();
			jLabel.setText("Spalte 1:");
			jLabel1.setText("Spalte 2:");
			jPanel.add(jLabel2, null);
			jPanel.add(getJSpinField1(), null);
			jPanel.add(getJCheckBox(), null);
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
	 * This method initializes mainWindow	
	 * 	
	 * @return gui.mosaic.MainWindow	
	 */    
	private MainWindow getMainWindow() {
		if (mainWindow == null) {
			mainWindow = new MainWindow(false);
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
	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
			jCheckBox.setText("Marker anzeigen");
			jCheckBox.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					mainWindow.Markers(jCheckBox.isSelected());
				}
			});
		}
		return jCheckBox;
	}

    /**
     * This method initializes jSpinField1	
     * 	
     * @return gui.mosaic.JSpinField	
     */    
    private JSpinField getJSpinField1() {
    	if (jSpinField1 == null) {
    		jSpinField1 = new JSpinField();
    		jSpinField1.setValue(10);
    		jSpinField1.setPreferredSize(new java.awt.Dimension(50,20));
    	}
    	return jSpinField1;
    }
      }  //  @jve:decl-index=0:visual-constraint="10,30"
