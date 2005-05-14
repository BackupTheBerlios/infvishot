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
    private InternalPerformanceFrm ipf = null;
    private boolean stop = false;
    
    public MacroThread(InternalPerformanceFrm _ipf, SysCore _sysCore){
        sysCore = _sysCore;
        ipf = _ipf;
    }
    
    public void run(){
        for (int i=0; i<ipf.jTable1.getRowCount(); i++){
            if (stop) {
                ipf.macroRunning = false;
                ipf.threadMeth();
                break;
            }
            
            //
        }
    }
    
    public void stop(){
        stop = true;
    }
}
