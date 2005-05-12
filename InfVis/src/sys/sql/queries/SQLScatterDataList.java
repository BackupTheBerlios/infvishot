/**
 * Created on 26.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.sql.queries;

import java.io.Serializable;

import sys.sql.*;

public class SQLScatterDataList extends SQLQuery implements Serializable {
		
	public SQLScatterDataList(int _sqlType) {
		super(_sqlType);
	}

	/** List of  */
	public String getList(String _table, String _x, String _y, String _x1, String _y1) {
		String outp = "";
		
		switch (sqlType) {
			case 1: //MySQL
			case 2: //HSQL
				outp = "SELECT " + _x + ", " + _y + ", " + _x1 + ", " + _y1 + " " +
					   "FROM " + _table + " "; 
				break;
		}
		System.out.println(outp);
		return outp;
	}
	
	/** List of with min,max */
	public String getList(String _table, String _x, String _y, String _x1, String _y1, double minX, double minY, double maxX, double maxY) {
		String outp = "";
		
		switch (sqlType) {
			case 1: //MySQL
			case 2: //HSQL
				outp = "SELECT " + _x + ", " + _y + ", " + _x1 + ", " + _y1 + " " +
					   "FROM " + _table + " " +
					   "WHERE " + _x + ">=" + minX + " AND " + _x + "<=" +  maxX + " AND " + _y + ">=" + minY + " AND " + _y + "<=" +  maxY + " "; 
				break;
		}
		System.out.println(outp);
		return outp;
	}
	
	/** List of with min,max */
	public String getList(String _table, String _x, String _y, double minX, double minY, double maxX, double maxY, String _spx, String _spy) {
		String outp = "";
		
		switch (sqlType) {
			case 1: //MySQL
			case 2: //HSQL
				outp = "SELECT " + _x + ", " + _y + ", " + _spx + ", " + _spy + " " +
					   "FROM " + _table + " " +
					   "WHERE " + _spx + ">=" + minX + " AND " + _spx + "<=" +  maxX + " AND " + _spy + ">=" + minY + " AND " + _spy + "<=" +  maxY + " "; 
				break;
		}
		System.out.println(outp);
		return outp;
	}
}
