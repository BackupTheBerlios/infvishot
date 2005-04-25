/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.db;

import java.io.Serializable;

public class DBProps implements Serializable {
    private String dbUrl = "";
    private String dbName = "";
    private String dbUsername = "";
    private String dbPassword = "";
    private int dbType = 0;
    private String lastUpdate = "";
    private int lastUpdateID = 0;
    
    /** Creates a new instance of DBProps */
    public DBProps() {
    }
    
    /** */
    public void setDBUrl(String _data){
        dbUrl = _data;
    }
    
    /** */
    public void setDBName(String _data){
        dbName = _data;
    }
    
    /** */
    public void setDBUsername(String _data){
        dbUsername = _data;
    }
    
    /** */
    public void setDBPassword(String _data){
        dbPassword = _data;
    }
    
    /** */
    public void setDBType(int _data){
        dbType = _data;
    }
    
    public void setLastUpdate(String _date){
    	lastUpdate = _date;
    }
    
    public void setlastUpdateID(int _id){
    	lastUpdateID = _id;
    }
    
    public String getLastUpdate(){
    	return lastUpdate;
    }
    
    public int getLastUpdateID(){
    	return lastUpdateID;
    }
    
    /** */
    public String getDBUrl(){
        return dbUrl;
    }
    
    /** */
    public String getDBName(){
        return dbName;
    }
    
    /** */
    public String getDBUsername(){
        return dbUsername;
    }
    
    /** */
    public String getDBPassword(){
        return dbPassword;
    }
    
    /** */
    public int getDBType(){
        return dbType;
    }
}
