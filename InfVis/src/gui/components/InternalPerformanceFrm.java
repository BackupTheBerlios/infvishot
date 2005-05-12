/**
 * Created on 28.04.2005
 * Project: InfVis
 * @author: harald
 */

package gui.components;

import javax.swing.*;

import sys.main.IOManager;
import sys.main.InfVisException;
import sys.main.SysCore;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.FlowLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.StackedBarRenderer;
import org.jfree.data.*;

import javax.swing.JCheckBox;
public class InternalPerformanceFrm extends JInternalFrame {
    private static SysCore sysCore = null;
    private JMenuItem jMenuItem = null;    
	private javax.swing.JPanel jContentPane = null;
	
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
	private JButton jButton1 = null;
	private JCheckBox jCheckBox = null;
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
	
	public void addTimeRow(int id, long _timedb, long _timesp, long _timemv, String _typ, int _datacount){
	    Object[] ox = new Object[6];
	    ox[0] = new Integer(id);
	    ox[1] = new Long(_timedb);
	    ox[2] = new Long(_timesp);
	    ox[3] = new Long(_timemv);
	    ox[4] = new Integer(_datacount);
	    ox[5] = _typ;
	    
	    jTable.addRow(ox);
	    
	    if (jCheckBox.isSelected()){
	        drawChart();
	    }
	}
	
	/**
	 * Chart
	 */
	
	private void drawChart() {
	    jPanel2.removeAll();
	    
	    CategoryDataset dataset = createDataset();
	    
        JFreeChart chart = ChartFactory.createStackedBarChart(
            "Performancegraph",
            "Datensätze",            
            "Zeit (Millisekunden)",  
            dataset,                    
            PlotOrientation.VERTICAL, 
            true,                       
            true,                       
            false                       
        );
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(new java.awt.Color(0,0,0));
        StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
        renderer.setItemLabelsVisible(true);
        
        //draw
        ChartPanel chartPanel = new ChartPanel(chart);
        
        jPanel2.add(chartPanel);
        jPanel2.updateUI();
    }
	
	private CategoryDataset createDataset() {
	    String series0 = "DB";
        String series1 = "Scatterpaint";
        String series2 = "Brush/Link";
        String series3 = "Mosaicpaint";
        String series4 = "Brush/Link";
        
        // column keys...
        String[] category = new String[jTable.getRowCount()];
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i=0; i<category.length; i++){
            category[i] = "i=" + i + ": " + jTable.getValueAt(i,4).toString();
            
            int tmpid = ((Integer)jTable.getValueAt(i,0)).intValue();
            long tmptime1 = ((Long)jTable.getValueAt(i,1)).longValue();
            long tmptime2 = ((Long)jTable.getValueAt(i,2)).longValue();
            long tmptime3 = ((Long)jTable.getValueAt(i,3)).longValue();
            
            dataset.addValue(tmptime1, series0, category[i]);
            
            dataset.addValue(tmptime2, series1, category[i]);
            dataset.addValue(tmptime3, series3, category[i]);
            
        }
        
        return dataset;
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
		
		drawChart();
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
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getJPanel4(), java.awt.BorderLayout.CENTER);
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
			jPanel2.setLayout(new BorderLayout());
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
			jPanel4.setPreferredSize(new java.awt.Dimension(200,400));
			jPanel4.add(getJButton1(), null);
			jPanel4.add(getJCheckBox(), null);
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
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
				    javax.swing.JFileChooser jFileChooser1 = new javax.swing.JFileChooser();
					jFileChooser1.setDialogType(JFileChooser.SAVE_DIALOG);
					jFileChooser1.setDialogTitle("Daten als CSV exportieren...");
					jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
					jFileChooser1.setMultiSelectionEnabled(false);
					
					if (jFileChooser1.showSaveDialog(null) != 1){
						StringBuffer sb = new StringBuffer();
						sb.append("ID;ZeitDB;ZeitGUI;Datasets;Typ\n");
						
						for (int i=0; i<jTable.getRowCount(); i++){
							
							sb.append(jTable.getValueAt(i,0).toString() + ";" +
							          jTable.getValueAt(i,1).toString() + ";" +
							          jTable.getValueAt(i,2).toString() + ";" +
							          jTable.getValueAt(i,3).toString() + ";'" +
							          jTable.getValueAt(i,4).toString() + "'\n");
						}
						
						IOManager iom = new IOManager();
						try {
						    iom.writeTextfile(sb.toString().replace(".",","),jFileChooser1.getSelectedFile().getAbsolutePath());
						}
						catch (Exception exc){
						    new InfVisException("Schreibfehler","Fehler beim Schreiben von: " + jFileChooser1.getSelectedFile().getAbsolutePath(),true);
						}
					}
					
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
			jButton1.setText("Zurücksetzen");
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jTable.removeAll();
					drawChart();
				}
			});
		}
		return jButton1;
	}
	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
			jCheckBox.setText("update");
		}
		return jCheckBox;
	}
             }  //  @jve:decl-index=0:visual-constraint="10,30"
