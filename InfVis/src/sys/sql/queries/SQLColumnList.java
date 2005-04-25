/**
 * Created on 26.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.sql.queries;

import java.io.Serializable;

import sys.sql.*;

public class SQLColumnList extends SQLQuery implements Serializable {
		
	public SQLColumnList(int _sqlType) {
		super(_sqlType);
	}

	/** List of  */
	public String getList(String _table) {
		String outp = "";
		
		switch (sqlType) {
			case 1: //MySQL
			case 2: //HSQL
				outp = "SHOW COLUMNS FROM " + _table + " "; 
				break;
		}
		
		return outp;
	}
}
