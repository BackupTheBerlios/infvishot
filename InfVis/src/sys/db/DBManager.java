/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.db;

import java.sql.*;
import java.io.*;

import sys.main.InfVisException;
import sys.main.SysCore;

public final class DBManager implements java.io.Serializable {
    private static String username = "";
    private static String password = "";
    private static String url = "";
    private static String dbname = "";
    private static int dbtype = 0;
    private Connection conID = null;
    private Statement stmt = null;
    private ResultSet rSet = null;
    private PreparedStatement prepStatement = null;
        
    /** Creates a new instance of DBManager */
    public DBManager(String inUsername, String inPasswd, String inUrl, String inDbName, int inType) {
        username = inUsername;
        password = inPasswd;
        url = inUrl;
        dbname = inDbName;
        dbtype = inType;
    }
    
    /** Connects to database */
    public synchronized boolean connectDB() throws InfVisException{		
        try {
            if(this.conID == null) {
                switch (dbtype){
                    case 1: //MySQL
                        Class.forName( "org.gjt.mm.mysql.Driver" );	
                        this.conID = DriverManager.getConnection("jdbc:mysql://" + url + dbname, username, password );
                        break;
                    case 2: //HSQLDB
						Class.forName("org.hsqldb.jdbcDriver").newInstance();	
						this.conID = DriverManager.getConnection( "jdbc:hsqldb:" + url + dbname, username, password );
                    	break;
                    default:
                        throw new InfVisException("Kein Datenbanktyp ausgewählt!",false);
                }
            }
            else {
                //try
                try {
                    this.conID.createStatement().executeQuery("SELECT NOW()");
                }
                catch (Exception exc){
                    this.conID = null;
                    this.connectDB();
                }
                
            }
            return true;
            
        } catch (ClassNotFoundException e) {
			//JOptionPane.showMessageDialog( null, "Die Klasse zum starten der Datenbank wurde nicht gefunden! Stellen Sie sicher, dass sich diese in Ihrem CLASSPATH befindet!", "Datenbankfehler", JOptionPane.INFORMATION_MESSAGE );
            throw new InfVisException("Datenbankfehler","Die Klasse zum starten der Datenbank wurde nicht gefunden! Stellen Sie sicher, dass sich diese in Ihrem CLASSPATH befindet!",false);
        } catch (SQLException e) {
			//JOptionPane.showMessageDialog( null, "Folgender Datenbankfehler trat auf: " + e.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE );
			throw new InfVisException("Datenbankfehler","Folgender Datenbankfehler trat auf: " + e.getMessage(),false);
        }
        catch(Exception exc){
        	throw new InfVisException("DBManager: Anderer Fehler beim Laden der Datenbank!",false);
        }
    }
    
    /** Sends query to server */
    public synchronized ResultSet sendQuery (String inQuery) throws InfVisException {		
        
        try {
            this.connectDB();

            if(this.conID != null) {
                this.stmt  = this.conID.createStatement(); 
                this.rSet = stmt.executeQuery( inQuery );
               // System.out.println(inQuery);
            }else{
				//JOptionPane.showMessageDialog( null, "Es konnte keine Verbindung zur Datenbank hergestellt werden!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE );
                this.rSet = null;
                throw new InfVisException("Datenbankfehler","Es konnte keine Verbindung zur Datenbank hergestellt werden!",false);
            }

        } catch (SQLException e) {
			//JOptionPane.showMessageDialog( null, "Folgender Datenbankfehler trat auf: " + e.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE );
            System.out.println(e.getMessage() + "HH"); //TODO: delete
            this.conID = null;
			throw new InfVisException("Datenbankfehler","Folgender Datenbankfehler trat auf: " + e.getMessage(),false);
        }

         return this.rSet;
    }
	
    
    public synchronized int sendStatement (String inStatement) throws InfVisException {	
		int ret = 0;
		
        try {
            this.connectDB();

            if(this.conID != null) {
                this.stmt  = this.conID.createStatement();
                ret = stmt.executeUpdate( inStatement );
            }else{
				//JOptionPane.showMessageDialog( null, "Es konnte keine Verbindung zur Datenbank hergestellt werden!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE );
                this.rSet = null;
                throw new InfVisException("Datenbankfehler","Es konnte keine Verbindung zur Datenbank hergestellt werden!",false);
            }		

        } catch (SQLException e) {
			//JOptionPane.showMessageDialog( null, "Folgender Datenbankfehler trat auf: " + e.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE );
            this.conID = null;
            throw new InfVisException("Datenbankfehler","Folgender Datenbankfehler trat auf: " + e.getMessage(),false);
        }

        return ret;
    }
    
    /** Returns next ID */
    public synchronized int getNextID(SysCore _sysCore,String _table) throws InfVisException {
        this.connectDB();
		ResultSet rSet = sendQuery(new sys.sql.queries.SQLMain(_sysCore.getDBProps().getDBType()).getNextID(_table));
		
		try {
			while(rSet.next()) {
				return rSet.getInt(1);
			}
		}
		catch (SQLException e) {
			//JOptionPane.showMessageDialog( null, "Fehler beim Laden der NextID!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE );
		    this.conID = null;
			throw new InfVisException("Datenbankfehler","Fehler beim Laden der NextID!",false);
		}
    	return 0;
    }
    
    
    /** For storing a binary stream */
    public synchronized int sendPreparedStatement(String inStatement,InputStream inputStream, int sSize, int position) throws InfVisException {
		int ret = 0;
		
		try {
			this.connectDB();

			if(this.conID != null) {
				this.prepStatement = this.conID.prepareStatement(inStatement);
				this.prepStatement.setBinaryStream(position, inputStream, sSize);
				ret = prepStatement.executeUpdate();
			}else{
				//JOptionPane.showMessageDialog( null, "Es konnte keine Verbindung zur Datenbank hergestellt werden!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE );
				this.rSet = null;
				throw new InfVisException("Datenbankfehler","Es konnte keine Verbindung zur Datenbank hergestellt werden!",false);
			}		

		} catch (SQLException e) {
			//JOptionPane.showMessageDialog( null, "Folgender Datenbankfehler trat auf: " + e.getMessage(), "Datenbankfehler", JOptionPane.ERROR_MESSAGE );
		    this.conID = null;
		    throw new InfVisException("Datenbankfehler","Folgender Datenbankfehler trat auf: " + e.getMessage(),false);
		}

		return ret;
    }
    
    /** Loads SQL Dump */
	public synchronized boolean loadSqlDump (String dbname,String sfile) throws InfVisException {
	
		try {
		
			String sline;
			StringBuffer sbcontentOfFile = new StringBuffer();
			BufferedReader br = new BufferedReader(
								new InputStreamReader(
								new FileInputStream(sfile))); 
		
			this.sendStatement("CREATE DATABASE IF NOT EXISTS `"+ dbname +"` ;");
		
			this.connectDB();

			if(this.conID != null) {
			
				while ((sline = br.readLine()) != null) {
			
					if(!(sline.startsWith("#"))) {		
				
						sbcontentOfFile.append(sline);
			
						if(sline.endsWith(";")) {
							//System.out.println(sbcontentOfFile.toString());
							this.sendStatement(sbcontentOfFile.toString());
							sbcontentOfFile = new StringBuffer();					
						}
					}
				}
			}
		} catch (Exception e) {
			//JOptionPane.showMessageDialog( null, "Fehler beim Laden des SQL Dumps!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE );
		    this.conID = null;
			throw new InfVisException("Datenbankfehler","Fehler beim Laden des SQL Dumps!", false);
		}

		return true;
	}
}
