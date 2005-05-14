/*
 * Created on 14.05.2005
 * Project: InfVis
 */
package gui.components;

import sys.main.SysCore;
import sys.sql.managers.SQLMosaicDataListManager;
import sys.sql.managers.SQLScatterDataListManager;
import sys.sql.managers.SQLTableManager;

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
        SQLTableManager stm = new SQLTableManager(sysCore,false);
        String destTable = "abcde";
        
        for (int i=0; i<ipf.jTable1.getRowCount(); i++){
            if (stop) {
                break;
            }
            
            String x = sysCore.getMainFrm().imosaicfrm.jComboBox.getSelectedItem().toString();
            String y = sysCore.getMainFrm().imosaicfrm.jComboBox1.getSelectedItem().toString();
            String x1 = sysCore.getMainFrm().iscatterfrm.jComboBoxXAxis.getSelectedItem().toString();
            String y1 = sysCore.getMainFrm().iscatterfrm.jComboBoxYAxis.getSelectedItem().toString();
            
            if (!stm.createTmpTable(Integer.parseInt(ipf.jTable1.getValueAt(i,0).toString()),x,y,x1,y1,sysCore.getMainFrm().isettingsfrm.jComboBox.getSelectedItem().toString(),destTable)){
                System.out.println("Error while creating temporary table.");
                continue;
            }
            
            sysCore.getMainFrm().iscatterfrm.repaintPlot(destTable);
            
            //Selection
            if (ipf.mosaicSelection == null){
                if (ipf.scatterBounds == null){
                    continue;
                }
                //Brush Scatter
                SQLScatterDataListManager tmpssdlm = new SQLScatterDataListManager(sysCore,destTable,x1,y1,x,y,false);
	            sysCore.getMainFrm().iscatterfrm.jScatterplotPanel.setSQLSPManager(tmpssdlm);
	            sysCore.getMainFrm().iscatterfrm.jScatterplotPanel.getScatterArea().checkEnclosedPoints(ipf.scatterBounds);
	            System.out.println("hallo");
	            sysCore.getMainFrm().iscatterfrm.jScatterplotPanel.setSQLSPManager(new SQLScatterDataListManager(sysCore,sysCore.getMainFrm().isettingsfrm.jComboBox.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxXAxis.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxYAxis.getSelectedItem().toString(),sysCore.getMainFrm().imosaicfrm.jComboBox.getSelectedItem().toString(),sysCore.getMainFrm().imosaicfrm.jComboBox1.getSelectedItem().toString(),false));
	            sysCore.getMainFrm().imosaicfrm.mainWindow.setSQLManager(new SQLMosaicDataListManager(sysCore,sysCore.getMainFrm().isettingsfrm.jComboBox.getSelectedItem().toString(),sysCore.getMainFrm().imosaicfrm.jComboBox.getSelectedItem().toString(), sysCore.getMainFrm().imosaicfrm.jComboBox1.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxXAxis.getSelectedItem().toString(),sysCore.getMainFrm().iscatterfrm.jComboBoxYAxis.getSelectedItem().toString(),false));
            }
            else {
                //Brush Mosaic
            }
        }
        
        //shut down thread
        stop = true;
        stm.dropTable(destTable);
        ipf.macroRunning = true;
        ipf.threadMeth();
    }
    
    public void stop(){
        stop = true;
    }
}
