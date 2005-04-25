/**
 * Created on 25.04.2005
 * Project: InfVis
 * @author: harald
 */

package sys.helpers;

public class SimpleStringValue {
    private String s1,s2;
    
    public SimpleStringValue(String _s1, String _s2){
        s1 = _s1;
        s2 = _s2;
    }
    
    public String toString(){
        return s1;
    }
    
    public String getS1(){
        return s1;
    }
    
    public String getS2(){
        return s2;
    }
    
    public boolean isS2Numeric(){
        if (s2.indexOf("int") >= 0 || s2.indexOf("double") >= 0 || s2.indexOf("float") >= 0 || s2.indexOf("decimal") >= 0) {
            return true;
        }
        return false;
    }
}
