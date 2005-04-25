/**
 * Created on 26.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.sql.managers;

import sys.main.InfVisException;
import sys.main.SysCore;
import sys.sql.queries.*;
import sys.helpers.*;
import java.sql.*;

public class SQLScatterDataListManager implements java.io.Serializable {
	private static SysCore sysCore = null;
	private java.util.Vector entries = null;
	private String id = "";
	private String table = "";
	private String x = "";
	private String y = "";
	
	public SQLScatterDataListManager(SysCore _sysCore, String _table, String _x, String _y, boolean _load) {
		sysCore = _sysCore;
		table = _table;
		x = _x;
		y = _y;
		entries = new java.util.Vector();
		
		if (_load){
		    loadData();
		}
	}
	
	//Clear list
	public void clear() {
	    entries.clear();
	}
	
	/** Load entry */
	public void loadData(){
		
		try {
		    ResultSet rSet = sysCore.getDB().sendQuery(new SQLScatterDataList(sysCore.getDBProps().getDBType()).getList(table,x,y));
		    int cnt = 0;
			while(rSet.next()) {
			   SimpleDataObject sdo = new SimpleDataObject("id"+cnt, rSet.getDouble(1), rSet.getDouble(2));
			   addElement(sdo);
			   cnt++;
			}
		}
		catch (Exception e) {
			new InfVisException("Fehler","Fehler beim Laden!" + e.getMessage(),false).showDialogMessage();
		}
	}
	
	
	public void addElement(SimpleDataObject _data){
	    entries.add(_data);
	}
	
	public int count(){
	    return entries.size();
	}
	
	public SimpleDataObject elementAt(int _pos){
	    return (SimpleDataObject) entries.elementAt(_pos);
	}
	
	public java.util.Vector getDataVector(){
	    return entries;
	}
	
}
