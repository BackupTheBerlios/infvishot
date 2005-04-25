/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

/*
 * Sample SQLManager TODO: delete comment
 */

package sys.sql.managers;

import sys.main.InfVisException;
import sys.main.SysCore;
import sys.sql.queries.SQLSampleQuery;

import java.sql.*;

public class SampleSQLManager implements java.io.Serializable {
	private static SysCore sysCore = null;
	private java.util.Vector entries = null;
	private String id = "";
	
	public SampleSQLManager(SysCore _sysCore, boolean _load) {
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
		    ResultSet rSet = sysCore.getDB().sendQuery(new SQLSampleQuery(sysCore.getDBProps().getDBType()).getList(0));
			while(rSet.next()) {
			   //TODO
			   //addElement(new SSO());
			}
		}
		catch (Exception e) {
			new InfVisException("Fehler","Fehler beim Laden!",false).showDialogMessage();
		}
	}
	
	/*
	public void addElement(SSO _data){
	    entries.add(_data);
	}
	
	public int count(){
	    return entries.size();
	}
	
	public elementAt(int _pos){
	    return (SSO) entries.elementAt(_pos);
	}
	*/
}
