/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.helpers;

import java.io.Serializable;

import sys.main.SysCore;

public class DoubleValue implements Serializable {
    private static SysCore sysCore = null;
    private String seperator = ".";
    private String value = "0";
    
    public DoubleValue() {
       seperator = "."; 
    }
    
    public DoubleValue(SysCore _sysCore) {
        sysCore = _sysCore;
        
        if (sysCore.getProps().getProperty("decimalseperatorsign") != null) {
	        if (sysCore.getProps().getProperty("decimalseperatorsign").equalsIgnoreCase(",")){
	        	seperator = ",";
	    	}
	    	else {
	        	seperator = ".";
	    	}
	    }
    }
    
    public void setDouble(double _val){
        value = String.valueOf(_val);
    }
    
    public double getDoubleValue(String _value) {
        value = _value.trim();
        
        return Double.parseDouble(value.replace(',','.'));
    }
    
    public String getValue(String _value) {
        value = _value;
        
        if (seperator.equalsIgnoreCase(",")){
            return value.replace('.',',').trim();
        }
        else {
            return value.replace(',','.').trim();
        }
    }
    
    public String toString() {
        return getValue(value);
    }
}
