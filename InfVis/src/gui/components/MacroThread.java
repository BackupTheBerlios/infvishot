/*
 * Created on 14.05.2005
 * Project: InfVis
 */
package gui.components;

import sys.main.SysCore;

/**
 * @author Harald
 */
public class MacroThread implements Runnable {
    private static SysCore sysCore = null;
    
    public MacroThread(InternalPerformanceFrm _ipf, SysCore _sysCore){
        sysCore = _sysCore;
    }
    
    public void run(){
        
    }
    
    public void stop(){
        
    }
}
