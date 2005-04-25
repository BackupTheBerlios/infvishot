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
	public String getList(String _table, String _x, String _y) {
		String outp = "";
		
		switch (sqlType) {
			case 1: //MySQL
			case 2: //HSQL
				outp = "SELECT " + _x + ", " + _y + " " +
					   "FROM " + _table + " "; 
				break;
		}
		
		return outp;
	}
}
