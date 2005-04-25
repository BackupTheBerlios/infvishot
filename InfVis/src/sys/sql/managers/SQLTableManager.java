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

import java.sql.*;

public class SQLTableManager implements java.io.Serializable {
	private static SysCore sysCore = null;
	private java.util.Vector entries = null;
	private String id = "";
	
	public SQLTableManager(SysCore _sysCore, boolean _load) {
		sysCore = _sysCore;
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
		    ResultSet rSet = sysCore.getDB().sendQuery(new SQLTableList(sysCore.getDBProps().getDBType()).getList());
			while(rSet.next()) {
			   addElement(rSet.getString(1));
			}
		}
		catch (Exception e) {
			new InfVisException("Fehler","Fehler beim Laden!" + e.getMessage(),false).showDialogMessage();
		}
	}
	
	
	public void addElement(String _data){
	    entries.add(_data);
	}
	
	public int count(){
	    return entries.size();
	}
	
	public String elementAt(int _pos){
	    return (String) entries.elementAt(_pos);
	}
	
}
