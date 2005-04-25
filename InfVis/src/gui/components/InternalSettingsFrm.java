/**
 * Created on 26.03.2005
 * Project: InfVis
 * @author: harald
 */

package gui.components;

import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

import sys.helpers.SimpleStringValue;
import sys.main.SysCore;
import sys.sql.managers.*;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import javax.swing.JComboBox;

public class InternalSettingsFrm extends JInternalFrame {
    private static SysCore sysCore = null;
    private JMenuItem jMenuItem = null;    
	private javax.swing.JPanel jContentPane = null;

	private JPanel jPanel1 = null;
	private JLabel jLabel1 = null;
	public JComboBox jComboBox = null;
	/**
	 * This is the default constructor
	 */
	public InternalSettingsFrm(SysCore _sysCore) {
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
		final InternalSettingsFrm tmpiframe = this;
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
		this.setTitle("Einstellungen");
		this.setSize(340, 82);
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
			jLabel1 = new JLabel();
			jPanel1 = new JPanel();
			jPanel1.setLayout(null);
			jLabel1.setBounds(10, 11, 106, 16);
			jLabel1.setText("Tabelle:");
			jPanel1.add(jLabel1, null);
			jPanel1.add(getJComboBox(), null);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
			jComboBox.setBounds(118, 11, 174, 21);
			jComboBox.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					sysCore.getMainFrm().iscatterfrm.jComboBoxXAxis.removeAllItems();
					sysCore.getMainFrm().iscatterfrm.jComboBoxYAxis.removeAllItems();
					SQLColumnManager cdata = new SQLColumnManager(sysCore,jComboBox.getSelectedItem().toString(),true);
					
					for (int i=0; i<cdata.count(); i++){
					    SimpleStringValue ss = (SimpleStringValue)cdata.elementAt(i);
					    if (ss.isS2Numeric()){
					        sysCore.getMainFrm().iscatterfrm.jComboBoxXAxis.addItem(cdata.elementAt(i));
					        sysCore.getMainFrm().iscatterfrm.jComboBoxYAxis.addItem(cdata.elementAt(i));
					    }
					}
				}
			});
			
			SQLTableManager sdata = new SQLTableManager(sysCore, true);
			
			for (int i=0; i<sdata.count(); i++){
			    jComboBox.addItem(sdata.elementAt(i));
			}
		}
		return jComboBox;
	}
  }  //  @jve:decl-index=0:visual-constraint="10,30"
