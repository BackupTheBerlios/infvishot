/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.sql;

import java.io.Serializable;

public abstract class SQLQuery implements Serializable {
	protected static int sqlType = 0;
		
	public SQLQuery(int _sqlType) {
		sqlType = _sqlType;
	}
	 
	/** NextID */
	public String getNextID(String _table) {
		String outp = "";
		
		switch (sqlType) {
			case 1: //MySQL
				outp = "SELECT LAST_INSERT_ID() ";
				break;
		}
		
		return outp;
	}
}
