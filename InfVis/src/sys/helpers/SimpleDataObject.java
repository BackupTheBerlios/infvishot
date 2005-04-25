/**
 * Created on 26.03.2005
 * Project: InfVis
 * @author: harald
 */

package sys.helpers;

public class SimpleDataObject {
    private String id = "";
    private double x = 0d;
    private double y = 0d;
    
    public SimpleDataObject(String _id, double _x, double _y){
        id = _id;
        x = _x;
        y = _y;
    }
    
    public String getID(){
        return id;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
}
