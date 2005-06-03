/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.main;

import sys.db.*;

import java.util.Properties;
import java.io.*;
import gui.main.MainFrm;

public class SysCore implements Serializable {
    protected static DBManager sdb = null;
    protected static DBProps dbprops = null;
    protected static String version = "";
    protected static Properties sysProps = null;
    protected static int UserID = 1;
    protected static MainFrm mainfrm = null;
    protected static boolean debug = true; 
    
    /** Empty constructor */
    public SysCore() {
    	version = "InfVis/VRVIS 1.0";
    }
    
    /** Creates a new instance of SysCore */
    public SysCore(DBProps _dbprops) {
		dbprops = _dbprops;
        load(_dbprops);
    }
    
    public static boolean load(DBProps _dbprops) {
    	dbprops = _dbprops;
        sdb = new DBManager(_dbprops.getDBUsername(),_dbprops.getDBPassword(),_dbprops.getDBUrl(),_dbprops.getDBName(),_dbprops.getDBType());
        
        //Try to connect
        try {
            sdb.connectDB();
        }
        catch (InfVisException exc){
            return false;
        }
        
    	return true;
    }
    
    /** Returns database object */
    public static DBManager getDB() {
    	return sdb;
    }
    
    
    public static DBProps getDBProps(){
        if (dbprops == null){
        	return new DBProps();
        }
    	return dbprops;
    }
    
    public static void setDBProps(){
    	IOManager x = new IOManager();
    	
    	try {
    	    x.saveobject("data/db.conf", dbprops);
	    }
	    catch (InfVisException exc){
	        exc.showDialogMessage();
	    } 
    }
    
    public static boolean setProxy(){
        
        Properties sysprops = System.getProperties();
        
        if (getProps().getProperty("proxyEnable") != null) {
	        if (Boolean.parseBoolean(getProps().getProperty("proxyEnable"))){
	            try {
	                if (Boolean.parseBoolean(getProps().getProperty("proxySocks"))){
	                    sysprops.remove("http.proxyHost");
	                    sysprops.remove("http.proxyPort");
	                    sysprops.remove("http.proxyUser");
	                    sysprops.remove("http.proxyPassword");
	                    sysprops.remove("https.proxyHost");
	                    sysprops.remove("https.proxyPort");
	                    sysprops.remove("ftp.proxyHost");
	                    sysprops.remove("ftp.proxyPort");
	                    sysprops.remove("java.net.socks.username");
	                    sysprops.remove("java.net.socks.password");
	                    
	                    sysprops.put("socksProxyHost", getProps().getProperty("proxyServer"));
	                    sysprops.put("socksProxyPort", getProps().getProperty("proxyPort"));
	                    
	                    if (Boolean.parseBoolean(getProps().getProperty("proxyAuthentication"))) {
	                        sysprops.put("java.net.socks.username", getProps().getProperty("proxyUsername"));
	                        sysprops.put("java.net.socks.password", getProps().getProperty("proxyPassword"));
	                    }
	                }
	                else {
	                    sysprops.remove("http.proxyHost");
	                    sysprops.remove("http.proxyPort");
	                    sysprops.remove("http.proxyUser");
	                    sysprops.remove("http.proxyPassword");
	                    sysprops.remove("https.proxyHost");
	                    sysprops.remove("https.proxyPort");
	                    sysprops.remove("ftp.proxyHost");
	                    sysprops.remove("ftp.proxyPort");
	                    sysprops.remove("socksProxyHost");
	                    sysprops.remove("socksProxyPort");
	                    sysprops.remove("java.net.socks.username");
	                    sysprops.remove("java.net.socks.password");
	                    
	                    sysprops.put("http.proxyHost", getProps().getProperty("proxyServer"));
	                    sysprops.put("http.proxyPort", getProps().getProperty("proxyPort"));
	                    sysprops.put("https.proxyHost", getProps().getProperty("proxyServer"));
	                    sysprops.put("https.proxyPort", getProps().getProperty("proxyPort"));
	                    sysprops.put("ftp.proxyHost", getProps().getProperty("proxyServer"));
	                    sysprops.put("ftp.proxyPort", getProps().getProperty("proxyPort"));
	                    
	                    if (Boolean.parseBoolean(getProps().getProperty("proxyAuthentication"))) {
	                        sysprops.put("http.proxyUser", getProps().getProperty("proxyUsername"));
	                        sysprops.put("http.proxyPassword", getProps().getProperty("proxyPassword"));
	                    }
	                }
	            }
	            catch (Exception exc){
	                System.out.println("Error in config file. Proxy setting are not applied!");
	            }
	        }
	        else {
	            System.setProperty( "proxySet", "false" );
	            sysprops.remove("http.proxyHost");
                sysprops.remove("http.proxyPort");
                sysprops.remove("http.proxyUser");
                sysprops.remove("http.proxyPassword");
                sysprops.remove("https.proxyHost");
                sysprops.remove("https.proxyPort");
                sysprops.remove("ftp.proxyHost");
                sysprops.remove("ftp.proxyPort");
                sysprops.remove("socksProxyHost");
                sysprops.remove("socksProxyPort");
                sysprops.remove("java.net.socks.username");
                sysprops.remove("java.net.socks.password");
	        }
	    }
        
        return true;
    }
    
    public static String getVersion(){
    	return version;
    }
    
    public static void dbShutDown(){
    	if (dbprops.getDBType() == 2){
    		//HSQL
    	    try {
    	        sdb.sendStatement("SHUTDOWN COMPACT");
    	    }
    	    catch (InfVisException exc){
    	        exc.showDialogMessage();
    	    }
    	}
    }
    
    public static void shutDown(){
        //Save properties
        try {
            sysProps.store(new FileOutputStream("data/app.props", false),"UTF-8");
        }
        catch (Exception exc){
            System.out.println("Error while storing properties!");
        }
        
        //Exit
        dbShutDown();
        System.exit(0);
    }
    
    public static void setProps(Properties _sysProps){
        sysProps = _sysProps;
    }
    
    public static Properties getProps(){
        return sysProps;
    }
    
    public static int getUserID() {
        return UserID;
    }
    
    public void setUserID(int _userid){
        UserID = _userid;
    }
    
    public static MainFrm getMainFrm(){
        return mainfrm;
    }
    
    public static void setMainFrm(MainFrm _frm){
        mainfrm = _frm;
    }
    
    public boolean isDebug(){
        return debug;
    }
}
