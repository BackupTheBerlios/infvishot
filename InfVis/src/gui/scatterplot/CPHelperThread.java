/**
 * Created on 26.03.2005
 * Project: InfVis
 * @author: harald
 */

package gui.scatterplot;

import sys.main.*;;

public class CPHelperThread implements Runnable,java.io.Serializable {
    private static SysCore sysCore = null;
    private boolean doRun = false;
    private long waitingDuration = 0l;
    
    public CPHelperThread(String _frame, SysCore _sysCore){
        sysCore = _sysCore;
    }
    
    /**
     * Starts thread
     */
    public void run(){
        
        doRun = true;
        
        while (doRun){
            //Wait
    		synchronized(this){
    			try{
    			    if (waitingDuration > 0){
    			        ((java.lang.Object)this).wait(waitingDuration);
    			    }
    			    else {
    			        ((java.lang.Object)this).wait(1);
    			    }
    			}
    			catch (InterruptedException exc){
    				//do nothing
    			}
    		}    		
        }
    }
    
    /**
     * Stops thread
     */
    public void stop(){
        doRun = false;
    }
    
    /**
     * Set waiting time
     */
    public void setWaitingTime(long _time){
        waitingDuration = _time;
    }
}
