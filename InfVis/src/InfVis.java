/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

import javax.swing.*;

import sys.db.*;
import sys.main.IOManager;
import sys.main.InfVisException;
import sys.main.SysCore;
import gui.main.*;
import java.io.*;

public class InfVis implements java.io.Serializable {
    private static SysCore sysCore = null;
    
    public static void main(String[] args) {
        //Set user interface
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e){
		    System.out.println("Error while setting System-GUI!");
		}
		
		DBProps dbprops = new DBProps();
		sysCore = new SysCore();
		
		//System properties
		try {
		    java.util.Properties tmp = new java.util.Properties();
		    tmp.load(new FileInputStream("data/app.props"));
		    sysCore.setProps(tmp);
		}
		catch (Exception exc){
		    sysCore.setProps(new java.util.Properties());
		    System.out.println("Error while reading properties!" + exc.getMessage());
		}
		
		//Load proxy settings
		sysCore.setProxy();
		
		//Start main app
		IOManager x = new IOManager();
		try {
		    dbprops = (DBProps)x.loadobject("data/db.conf");
		    
		    if (!sysCore.load(dbprops)){
		        throw new InfVisException();
		    }
		    
		    new MainFrm(sysCore).setVisible(true);
		}
		catch (InfVisException exc){
		    //Show database input window
		    DatabaseSettingsDlg dsdlg = new DatabaseSettingsDlg(sysCore);
		    dsdlg.setVisible(true);
		    if (!dsdlg.wasAbort()){
		        new MainFrm(sysCore).setVisible(true);
		    }
		    else {
		        sysCore.shutDown();
		    }
		}
		
    }
}
