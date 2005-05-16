/**
 * Created on 22.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package gui.components;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.*;
import java.util.*;

import javax.swing.JDialog;
import sys.main.*;
import sys.helpers.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

public class JCSVImportFrm extends JDialog {
    private static SysCore sysCore = null;
    private String filen = "";
    
	private javax.swing.JPanel jContentPane = null;

	private JButton jButton = null;
	private CSVImportTable jTable = null;
	private JScrollPane jScrollPane = null;
	private JButton jButton1 = null;
	private JLabel jLabel = null;
	private JTextField jTextField = null;
	private JCheckBox jCheckBox = null;
	private JButton jButton2 = null;
	private JLabel jLabel1 = null;
	/**
	 * This is the default constructor
	 */
	public JCSVImportFrm(SysCore _sysCore) {
	    super();
	    this.setModal(true);
	    sysCore = _sysCore;
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(532, 566);
		this.setContentPane(getJContentPane());
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setTitle("CSV Importer");
		this.setModal(true);
		this.setResizable(false);
		this.setLocation( (d.width - getSize().width ) / 2,(d.height- getSize().height) / 2 );
	}
	/*
	 * Functions
	 */
	public void setUpValsColumn(CSVImportTable table, javax.swing.table.TableColumn sColumn) {
	    //Set up the editor for the sport cells.
	    javax.swing.JComboBox comboBoxT = new javax.swing.JComboBox();
	    comboBoxT.setEditable(true);

	    //TODO
	    comboBoxT.addItem("-");
	    comboBoxT.addItem("DOUBLE");
	    comboBoxT.addItem("INT");
	    comboBoxT.addItem("VARCHAR(100)");
			    
	    comboBoxT.addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {    
			    if (jTable.getSelectedRow() >= 0){
			        //none
				}
			}
		});
	    
	    sColumn.setCellEditor(new javax.swing.DefaultCellEditor(comboBoxT));

		//Set up tool tips for the cells.
		javax.swing.table.DefaultTableCellRenderer renderer = new javax.swing.table.DefaultTableCellRenderer();
		sColumn.setCellRenderer(renderer);
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jLabel1 = new JLabel();
			jLabel = new JLabel();
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(null);
			jLabel.setBounds(16, 35, 112, 17);
			jLabel.setText("Tabellenname:");
			jLabel1.setBounds(125, 11, 383, 17);
			jLabel1.setText("");
			jContentPane.add(getJButton(), null);
			jContentPane.add(getJScrollPane(), null);
			jContentPane.add(getJButton1(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(getJTextField(), null);
			jContentPane.add(getJCheckBox(), null);
			jContentPane.add(getJButton2(), null);
			jContentPane.add(jLabel1, null);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setLocation(405, 499);
			jButton.setSize(107, 17);
			jButton.setText("Schlieﬂen");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					dispose();
				}
			});
		}
		return jButton;
	}
	
	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */    
	private CSVImportTable getJTable() {
		if (jTable == null) {
			jTable = new CSVImportTable();
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
			jScrollPane.setBounds(15, 68, 493, 398);
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setLocation(15, 10);
			jButton1.setSize(107, 17);
			jButton1.setText("CSV-File");
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    JFileChooser jFileChooser1 = new javax.swing.JFileChooser(new File(""));
					jFileChooser1.setDialogTitle("CSV-Datei ausw‰hlen...");
					jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
					jFileChooser1.setMultiSelectionEnabled(false);
					//jFileChooser1.setFileFilter(new CSVFileFilter());
					
					CSV csv = new CSV(';');
					
					if (jFileChooser1.showOpenDialog(null) != 1){
					    filen = jFileChooser1.getSelectedFile().getAbsolutePath();
					    jLabel1.setText(filen);
					    
					    try {
							FileInputStream fr = new FileInputStream(filen);
							InputStreamReader isr =	new InputStreamReader(fr);
							BufferedReader reader =	new BufferedReader(isr);
							
							String line = "";
							if ((line = reader.readLine())!= null) {
							    Iterator ei = csv.parse(line);
							    
							    while (ei.hasNext()){
							        Object[] data = new Object[3];
							        data[0] = ei.next();
							        data[1] = data[0];
							      
							        data[2] = "DOUBLE";
							        jTable.addRow(data);
							    }
							}
							
							reader.close();
							
							jButton2.setEnabled(true);
					    }
					    catch (Exception exc){
					        System.out.println("Fehler beim lesen des CSV!");
					    }
					}
				    
				    setUpValsColumn(jTable,jTable.getColumnModel().getColumn(2));
				}
			});
		}
		return jButton1;
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBounds(131, 36, 183, 20);
		}
		return jTextField;
	}
	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
			jCheckBox.setBounds(318, 37, 190, 21);
			jCheckBox.setText("exisitiert");
		}
		return jCheckBox;
	}
	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setEnabled(false);
			jButton2.setLocation(16, 472);
			jButton2.setSize(107, 17);
			jButton2.setText("Importieren!");
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    
				    if (jTextField.getText().trim().equals("")){
				        JOptionPane.showMessageDialog(null,"Geben Sie einen Tabellennamen ein!", "Fehler", JOptionPane.ERROR_MESSAGE );
				        return;
				    }
				    
					//Create tables
					if (!jCheckBox.isSelected()){
					    //DIRTY VERSION !!!
					    try {
					        String data = "CREATE TABLE `" + jTextField.getText() + "` (\n";
					        
					        for (int i=0; i<jTable.getRowCount(); i++){
					            if (jTable.getValueAt(i,2).toString().equals("-")){
					               continue; 
					            }
					            data += "`" + jTable.getValueAt(i,1) + "` " + jTable.getValueAt(i,2) + " NOT NULL";
					            
					            if (i == jTable.getRowCount()-1 || (i == jTable.getRowCount()-2 && jTable.getValueAt(i+1,2).toString().equals("-"))){
					                data += "\n";
					            }
					            else {
					                data += ", \n";
					            }
					        }
					        
					        data += ");\n";
					        
					        //System.out.println(data);
					        
				            //Store
					        if (sysCore.getDB().sendStatement(data) == 0){
				                //throw new InfVisException("Fehler","Query!",false);
				            }
				        }
				        catch (Exception exc){
				            new InfVisException("Fehler","Fehler beim Erstellen der Tabelle!",false).showDialogMessage();
				            return;
				        }
					}
					
					//Insert Data
					try {
				        CSV csv = new CSV(';');
						    
				        FileInputStream fr = new FileInputStream(filen);
				        InputStreamReader isr =	new InputStreamReader(fr);
				        BufferedReader reader =	new BufferedReader(isr);
				        
				        String data = "INSERT INTO " + jTextField.getText() + " (";
				        int genRowCnt = 0;
				        for (int i=0; i<jTable.getRowCount(); i++){
				            if (jTable.getValueAt(i,2).toString().equals("-")){
				               continue; 
				            }
				            
				            data += jTable.getValueAt(i,1);
				            
				            if (i != jTable.getRowCount()-1){
				                data += ",";
				            }
				            genRowCnt++;
				        }
				        
				        data += ") VALUES(";
				        
				        String line = reader.readLine(); //1st
				        while ((line = reader.readLine())!= null) {
				            String rdata = data + "";
				            
				            Iterator ei = csv.parse(line);
				            
				            int cnt = 0;
				            
				            while (ei.hasNext()){
				                if (jTable.getValueAt(cnt,2).toString().equals("-") && ei.hasNext()){
				                    ei.next();
				                    continue;
						        }
				                
				                String tt = "0";
				                
				                Object tmpo = null;
				                try{
				                    tmpo = ei.next();
				                }
				                catch (Exception excc){
				                    
				                }
				                if (tmpo != null) {
				                    tt = tmpo.toString();
				                }
				                 
				                
				                
				                
				                rdata += "'" + (tt.equals("") ? "0" : tt) + "',";
				                
				                cnt++;
				            }
				            
				            rdata = rdata.substring(0,rdata.length()-1);
				            
				            rdata += ");\n";
				            
				            //Store
					        if (sysCore.getDB().sendStatement(rdata) == 0){
				                //throw new InfVisException("Fehler","Query!",false);
				            }
				            //System.out.println(rdata);
				            
				            //break;
				        }
				        
				        reader.close();
				        
				        JOptionPane.showMessageDialog(null,"Daten erfolgreich importiert!", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
			        }
			        catch (Exception exc){
			            new InfVisException("Fehler","Fehler beim Erstellen der Tabelle!" + exc.getMessage(),false).showDialogMessage();
			            exc.printStackTrace();
			            return;
			        }
			        
					/*
					CREATE TABLE `abc` (
					        `bcd` INT NOT NULL ,
					        `bce` VARCHAR( 233 ) NOT NULL
					        );
					        
					ALTER TABLE `abc` ADD `xyt` INT NOT NULL ;
					*/
				}
			});
		}
		return jButton2;
	}
       }  //  @jve:decl-index=0:visual-constraint="10,10"
