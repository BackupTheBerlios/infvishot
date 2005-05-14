/**
 * Created on 26.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.sql.queries;

import java.io.Serializable;

import sys.sql.*;

public class SQLTableList extends SQLQuery implements Serializable {
		
	public SQLTableList(int _sqlType) {
		super(_sqlType);
	}

	/** List of  */
	public String getList() {
		String outp = "";
		
		switch (sqlType) {
			case 1: //MySQL
			case 2: //HSQL
				outp = "SHOW TABLES"; 
				break;
		}
		
		return outp;
	}
	
	public String createTempTable(String _table, String _x, String _y, String _x1, String _y1, String _srcTable, int _cnt){
	    String outp = "";
		
	    if (_x.equalsIgnoreCase(_y)){
	        _y = "";
	    }
	    if (_x.equalsIgnoreCase(_x1)){
	        _x1 = "";
	    }
	    if (_x.equalsIgnoreCase(_y1)){
	        _y1 = "";
	    }
	    if (_y.equalsIgnoreCase(_x1)){
	        _x1 = "";
	    }
	    if (_y.equalsIgnoreCase(_y1)){
	        _y1 = "";
	    }
	    if (_x1.equalsIgnoreCase(_y1)){
	        _y1 = "";
	    }
	    
	    if (!_y.equalsIgnoreCase("")){
	        _y = ", " + _y;
	    }
	    if (!_x1.equalsIgnoreCase("")){
	        _x1 = ", " + _x1;
	    }
	    if (!_y1.equalsIgnoreCase("")){
	        _y1 = ", " + _y1;
	    }
	    
		switch (sqlType) {
			case 1: //MySQL
			case 2: //HSQL
				//outp = "DROP TABLE IF EXISTS " + _table + " ";
				outp += "CREATE TABLE " + _table + " " +
						"SELECT " + _x + " " + _y + " " + _x1 + " " + _y1 + " " +
					    "FROM " + _srcTable + " " +
					    "LIMIT " + _cnt + "; ";
				break;
		}
		System.out.println(outp);
		return outp;
	}
	
	public String dropTable(String _table){
	    String outp = "";
		
		switch (sqlType) {
			case 1: //MySQL
			case 2: //HSQL
				outp = "DROP TABLE IF EXISTS " + _table + " ";
				break;
		}
		//System.out.println(outp);
		return outp;
	}
}
