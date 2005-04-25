/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */


/*
 * Sample implementation for SQL Queries TODO: delete comment
 */

package sys.sql.queries;

import java.io.Serializable;

import sys.sql.*;

public class SQLSampleQuery extends SQLQuery implements Serializable {
		
	public SQLSampleQuery(int _sqlType) {
		super(_sqlType);
	}

	/** List of  */
	public String getList(int _id) {
		String outp = "";
		
		switch (sqlType) {
			case 1: //MySQL
			case 2: //HSQL
				outp = "SELECT * FROM XXX WHERE " +
					   "ID='" + _id + "' " +
					   "ORDER BY YYY"; 
				break;
		}
		
		return outp;
	}
	
	/** get item of  */
	public String getItem(int _id) {
		String outp = "";
		
		switch (sqlType) {
			case 1: //MySQL
			case 2: //HSQL
				outp = "SELECT * FROM XXX WHERE " +
					   "ID='" + _id + "' "; 
				break;
		}
		
		return outp;
	}
}
