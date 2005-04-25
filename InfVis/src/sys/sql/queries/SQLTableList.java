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
}
