/**
 * Created on 22.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package gui.components;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

public class CSVImportTable extends JTable implements ListSelectionListener{
    private PlugTableModel model;
	private String[] header = {"Spaltenname CSV","Spaltenname DB","Typ"};
		
	/**
	 * 
	 */
	public CSVImportTable() {
		super();
		
		Object[][] data = null;
		model = new PlugTableModel(data,header);
		setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		setColumnSelectionAllowed(false);
		getTableHeader().setReorderingAllowed(false);
		
		this.setModel(model);
	}
	
	public void valueChanged(ListSelectionEvent e){  
		super.valueChanged(e);  
	}
	
	public void addRow(Object[] _data){
		model.addRow(_data);
	}
	
	public void removeRow(int position){
		model.removeRow(position);
	}
	
	public void removeAll(){
		while (model.getRowCount() > 0){
			removeRow(0);
		}
	}
	
	class PlugTableModel extends DefaultTableModel{        
		PlugTableModel(Object[][] dataIn, Object[] columnIn){            
			super(dataIn, columnIn);        
		}        
		public boolean isCellEditable(int row, int col){   
		    if (col != 0){
		        return true;
		    }
		    else {
		        return false;
		    }
		}    
	}

}
