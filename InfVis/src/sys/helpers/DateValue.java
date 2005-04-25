/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.helpers;

import java.util.Date;
import java.text.SimpleDateFormat;

public class DateValue  implements java.io.Serializable {
    private Date date = null;
    private SimpleDateFormat sdf = null;
    private SimpleDateFormat sdf_2 = null;
    
    public DateValue() {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf_2 = new SimpleDateFormat("dd.MM.yyyy");
        date = new Date();
    }
    
    public void setDate(Date _date){
        date = _date;
    }
    
    public Date getDate() {
        return date;
    }
    
    public String getSQLDate(){
        return sdf.format(date);
    }
    
    public boolean setSQLDate(String _date){
        try {
            date = sdf.parse(_date);
            return true;
        }
        catch (Exception exc){
            return false;
        }
    }
    
    public String getNormalDate(){
        return sdf_2.format(date);
    }
    
    public boolean setNormalDate(String _date){
        try {
            date = sdf_2.parse(_date);
            return true;
        }
        catch (Exception exc){
            return false;
        }
    }
    
    public String toString(){
        return getNormalDate();
    }
}
