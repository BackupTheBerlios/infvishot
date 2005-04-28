/**
 * Created on 28.04.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.helpers;

public class TimeMeasureObject {
    private long start = 0l;
    private long end = 0l;
    
    public TimeMeasureObject(){
        
    }
    
    public void start(){
        clear();
        start = System.currentTimeMillis();
    }
    
    public void stop(){
        end = System.currentTimeMillis();
    }
    
    public long getTimeDiff(){
        return end-start;
    }
    
    public void clear(){
        start = end = 0l;
    }
    
    public String toString(){
        //return "Start: " + start + ", Stop: " + end;
        return getTimeDiff() + " milliseconds";
    }
}
