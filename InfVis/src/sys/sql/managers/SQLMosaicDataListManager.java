/**
 * Created on 28.04.2005
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

public class SQLMosaicDataListManager implements java.io.Serializable {
	private static SysCore sysCore = null;
	private java.util.Vector entries = null;
	private String id = "";
	private String table = "";
	private String x = "";
	private String y = "";
	private String sy = "";
	private String sx = "";
	private String[][] dData = null;
	private double minX = 0.0d, 
				   minY = 0.0d, 
				   maxX = 0.0d, 
				   maxY = 0.0d;
	private boolean treshSet = false;
	private TimeMeasureObject timemeasure = null;
	
	public SQLMosaicDataListManager(SysCore _sysCore, String _table, String _x, String _y, boolean _load) {
		sysCore = _sysCore;
		table = _table;
		x = _x;
		y = _y;
		entries = new java.util.Vector();
		timemeasure = new TimeMeasureObject();
		
		if (_load){
		    loadData();
		}
	}
	
	//Clear list
	public void clear() {
	    entries.clear();
	}
	
	public TimeMeasureObject getTime(){
	    return timemeasure;
	}
	
	/** Load entry */
	public void loadData(){
		
		try {
		    timemeasure.start();
		    ResultSet rSet = null;
		    int cnt = 0;
		    entries = new java.util.Vector();
		    
		    if (treshSet) {
		        rSet = sysCore.getDB().sendQuery(new SQLScatterDataList(sysCore.getDBProps().getDBType()).getList(table,x,y,minX,minY,maxX,maxY,sx,sy));
		    }
		    else {
		        rSet = sysCore.getDB().sendQuery(new SQLScatterDataList(sysCore.getDBProps().getDBType()).getList(table,x,y));
		    }
		    
		    rSet.last();
		    //System.out.println(rSet.getRow());
		    if (rSet.getRow() > 0){
		        dData = new String[2][rSet.getRow()];
		    }
		    else {
		        dData = new String[0][0];
		    }
		    
		    rSet.beforeFirst();
		    
			while(rSet.next()) {
			   dData[0][cnt] = rSet.getString(1);
			   dData[1][cnt] = rSet.getString(2);
			   
		//	   SimpleDataObject sdo = new SimpleDataObject("id"+cnt, rSet.getDouble(1), rSet.getDouble(2));
		//	   addElement(sdo);
			   cnt++;
			}
			timemeasure.stop();
			System.out.println(timemeasure.getTimeDiff()); //TODO: delete
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
	
	/*
	 * sets min,max bounds from brush area
	 */
	public void setBounds(double _minX, double _minY, double _maxX, double _maxY,String _sx, String _sy){
	    minX = _minX;
	    maxX = _maxX;
	    minY = _minY;
	    maxY = _maxY;
	    sx = _sx;
	    sy = _sy;
	    treshSet = true;
	}
	
	/*
	 * returns data as vector
	 */
	public java.util.Vector getDataVector(){
	    return entries;
	}
	
	/*
	 * resets treshold (for brushing area)
	 */
	public void resetTresh(){
	    treshSet = false;
	}
	
	/*
	 * returns data as double array
	 */
	public String[][] getDataArray(){
	    return dData;
	}
	
	public SysCore getSysCore(){
	    return sysCore;
	}
}
