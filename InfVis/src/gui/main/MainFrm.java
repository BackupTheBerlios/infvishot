/**
 * Created on 22.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package gui.main;

import gui.components.*;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.JFrame;

import sys.main.InfVisException;
import sys.main.SysCore;

import javax.swing.*;

import javax.swing.JDesktopPane;
import javax.swing.JMenuItem;

public class MainFrm extends JFrame {
    private static SysCore sysCore = null;
    public InternalScatterFrm iscatterfrm = null;
    public InternalMosaicFrm imosaicfrm = null;
    public InternalSettingsFrm isettingsfrm = null;
    public InternalPerformanceFrm iperformancefrm = null;
    
    private final static int HORIZONTAL = 0;
    private final static int VERTICAL = 1;
    private final static int CASCADE = 2;
    private final static int ARRANGE = 3;
    
	private javax.swing.JPanel jContentPane = null;

	private JMenuBar jJMenuBar = null;
	private JMenu jMenu = null;
	private JMenu jMenu2 = null;
	private JMenu jMenu3 = null;
	private JMenuItem jMenuItem = null;
	private JMenuItem jMenuItem1 = null;
	private JMenuItem jMenuItem2 = null;
	private JMenuItem jMenuItem6 = null;
	public JDesktopPane jDesktopPane = null;
	
	public JMenu jMenu1 = null;
	private JMenu jMenu4 = null;
	private JMenuItem jMenuItem3 = null;
    private JMenu jMenu5 = null;
    private JMenuItem jMenuItem4 = null;
    private JMenuItem jMenuItem5 = null;
    private JMenuItem jMenuItem7 = null;
    private JMenuItem jMenuItem8 = null;
    /**
	 * This is the default constructor
	 */
	public MainFrm(SysCore _sysCore) {
		super();
		sysCore = _sysCore;
		initialize();
		loadData();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
	    this.setSize(770, 582);
		this.setContentPane(getJContentPane());
		this.setTitle(sysCore.getVersion());
		this.setJMenuBar(getJJMenuBar());
		this.addWindowListener(new java.awt.event.WindowAdapter() {   
		 
			public void windowClosing(java.awt.event.WindowEvent e) {    
			    sysCore.shutDown();
			}
		});
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (d.width - getSize().width ) / 2,(d.height- getSize().height) / 2 );
	}
	/* 
	 * Functions
	 */
	public void loadData(){
	    sysCore.setMainFrm(this);
	    
	    //Scatterplot
	    iscatterfrm = new InternalScatterFrm(sysCore);
        jDesktopPane.add(iscatterfrm);
        iscatterfrm.setVisible(true);
        
        //Mosaic-View
        imosaicfrm = new InternalMosaicFrm(sysCore);
        jDesktopPane.add(imosaicfrm);
        imosaicfrm.setVisible(true);
        
        //Settings-frame
        isettingsfrm = new InternalSettingsFrm(sysCore);
        jDesktopPane.add(isettingsfrm);
        isettingsfrm.setVisible(true);
        
        //Performance
        iperformancefrm = new InternalPerformanceFrm(sysCore);
        jDesktopPane.add(iperformancefrm);
        iperformancefrm.setVisible(true);
	}
	
    public void tileFrames(int style){
        Dimension deskDim = jDesktopPane.getSize();
        int deskWidth = deskDim.width;
        int deskHeight = deskDim.height;
        JInternalFrame[] frames = jDesktopPane.getAllFrames();
        int frameCount = frames.length;
        int frameWidth=0;
        int frameHeight=0;
        int xpos=0;
        int ypos=0;
        double scale = 0.6;
        int spacer=30;
        int frameCounter=0;
        Vector frameVec=new Vector(1,1);
        boolean areIcons=false;
        int tempy=0,tempx=0;
        for (int i =0; i< frameCount; i++) {
            if (frames[i].isVisible() && !frames[i].isIcon() &&
                    frames[i].isResizable()) {
                frameVec.addElement(frames[i]);
                frameCounter++;
            }
            else if(frames[i].isIcon())
                areIcons=true;
        }
        if(areIcons)
            deskHeight = deskHeight - 50;
        switch(style){
        case(HORIZONTAL):
            for (int i=0; i<frameCounter; i++){
                JInternalFrame temp = (JInternalFrame) frameVec.elementAt(i);
                frameWidth = deskWidth;
                frameHeight = (int)(deskHeight/frameCounter);
                temp.reshape(xpos, ypos, frameWidth, frameHeight);
                ypos = ypos+frameHeight;
                temp.moveToFront();
            }
        break;
        
        case(VERTICAL):
            for (int i=0; i<frameCounter; i++){
                JInternalFrame temp = (JInternalFrame) frameVec.elementAt(i);
                frameWidth = (int)(deskWidth/frameCounter);
                frameHeight = deskHeight;
                if (temp.isResizable())
                    temp.reshape(xpos, ypos, frameWidth, frameHeight);
                else
                    temp.setLocation(xpos,ypos);
                xpos = xpos+frameWidth;
                temp.moveToFront();
            }
        break;
        case(CASCADE):
            for (int i=0; i<frameCounter; i++){
                JInternalFrame temp = (JInternalFrame) frameVec.elementAt(i);
                frameWidth =  (int)(deskWidth*scale);
                frameHeight = (int)(deskHeight*scale);
                if (temp.isResizable()) {
                    temp.reshape(xpos, ypos, frameWidth, frameHeight);
                }
                else
                    temp.setLocation(xpos,ypos);
                temp.moveToFront();
                xpos=xpos+spacer;
                ypos=ypos+spacer;
                if((xpos+frameWidth>deskWidth)||(ypos+frameHeight>deskHeight-50)){
                    xpos=0;
                    ypos=0;
                }
                
            }
        break;
        case(ARRANGE):
            int row=new Long(Math.round(Math.sqrt(new Integer(frameCounter).doubleValue()))).intValue();
        if(row==0)
            break;
        int col=frameCounter/row;
        if (col ==0)
            break;
        int rem=frameCounter%row;
        int rowCount=1;
        frameWidth = (int) deskWidth/col;
        frameHeight = (int) deskHeight/row;
        for (int i=0; i<frameCounter; i++){
            JInternalFrame temp = (JInternalFrame) frameVec.elementAt(i);
            if(rowCount<=row-rem) {
                if (temp.isResizable()){
                    temp.reshape(xpos,ypos,frameWidth,frameHeight);
                }
                else
                    temp.setLocation(xpos,ypos);
                if(xpos+10<deskWidth-frameWidth)
                    xpos=xpos+frameWidth;
                else {
                    ypos=ypos+frameHeight;
                    xpos=0;
                    rowCount++;
                }
            }
            else
            {
                frameWidth = (int)deskWidth/(col+1);
                if (temp.isResizable())
                    temp.reshape(xpos,ypos,frameWidth,frameHeight);
                else
                    temp.setLocation(xpos,ypos);
                if(xpos+10<deskWidth-frameWidth)
                    xpos=xpos+frameWidth;
                else {
                    ypos=ypos+frameHeight;
                    xpos=0;
                }
            }
            temp.dispatchEvent(new java.awt.event.ComponentEvent(temp,java.awt.event.ComponentEvent.COMPONENT_SHOWN));
        }
        break;
        default:
            break;
        }
        
        iscatterfrm.moveToFront();
        iscatterfrm.dispatchEvent(new java.awt.event.ComponentEvent(iscatterfrm,java.awt.event.ComponentEvent.COMPONENT_RESIZED));
    }
    
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getJDesktopPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */    
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getJMenu());
			jJMenuBar.add(getJMenu4());
			jJMenuBar.add(getJMenu2());
			jJMenuBar.add(getJMenu1());
			jJMenuBar.add(getJMenu3());
		}
		return jJMenuBar;
	}
	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu() {
		if (jMenu == null) {
			jMenu = new JMenu();
			jMenu.setText("Datei");
			jMenu.add(getJMenuItem1());
		}
		return jMenu;
	}
	/**
	 * This method initializes jMenu2	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu2() {
		if (jMenu2 == null) {
			jMenu2 = new JMenu();
			jMenu2.setText("Einstellungen");
			jMenu2.add(getJMenuItem2());
			jMenu2.add(getJMenuItem6());
		}
		return jMenu2;
	}
	/**
	 * This method initializes jMenu3	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu3() {
		if (jMenu3 == null) {
			jMenu3 = new JMenu();
			jMenu3.setText("Hilfe");
			jMenu3.add(getJMenuItem());
		}
		return jMenu3;
	}
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem() {
		if (jMenuItem == null) {
			jMenuItem = new JMenuItem();
			jMenuItem.setText("Info");
			jMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					new InfVisException("Info","Informationsvisualisierungstool for VrVis.at \nby Harald Meyer, Tobias Schleser, Oliver Hörbinger",false).showInfoDialogMessage();
				}
			});
		}
		return jMenuItem;
	}
	/**
	 * This method initializes jMenuItem1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem1() {
		if (jMenuItem1 == null) {
			jMenuItem1 = new JMenuItem();
			jMenuItem1.setText("Beenden");
			jMenuItem1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					sysCore.shutDown();
				}
			});
		}
		return jMenuItem1;
	}
	/**
	 * This method initializes jMenuItem2	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem2() {
		if (jMenuItem2 == null) {
			jMenuItem2 = new JMenuItem();
			jMenuItem2.setText("Datenbank");
			jMenuItem2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
				    new DatabaseSettingsDlg(sysCore).setVisible(true);
				}
			});
		}
		return jMenuItem2;
	}
	/**
	 * This method initializes jMenuItem6	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem6() {
		if (jMenuItem6 == null) {
			jMenuItem6 = new JMenuItem();
			jMenuItem6.setText("Allgemein");
			jMenuItem6.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					new SettingsDlg(sysCore).setVisible(true);
				}
			});
		}
		return jMenuItem6;
	}
	/**
	 * This method initializes jDesktopPane	
	 * 	
	 * @return javax.swing.JDesktopPane	
	 */    
	private JDesktopPane getJDesktopPane() {
		if (jDesktopPane == null) {
			jDesktopPane = new JDesktopPane();
		}
		return jDesktopPane;
	}
	/**
	 * This method initializes jMenu1	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu1() {
		if (jMenu1 == null) {
			jMenu1 = new JMenu();
			jMenu1.setText("Fenster");
			jMenu1.add(getJMenu5());
		}
		return jMenu1;
	}
	/**
	 * This method initializes jMenu4	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu4() {
		if (jMenu4 == null) {
			jMenu4 = new JMenu();
			jMenu4.setText("Tools");
			jMenu4.add(getJMenuItem3());
		}
		return jMenu4;
	}
	/**
	 * This method initializes jMenuItem3	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem3() {
		if (jMenuItem3 == null) {
			jMenuItem3 = new JMenuItem();
			jMenuItem3.setText("CSV-Import");
			jMenuItem3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
				    JCSVImportFrm jcsvfrm = new JCSVImportFrm(sysCore);
				    jcsvfrm.setVisible(true);
				}
			});
		}
		return jMenuItem3;
	}
    /**
     * This method initializes jMenu5	
     * 	
     * @return javax.swing.JMenu	
     */    
    private JMenu getJMenu5() {
    	if (jMenu5 == null) {
    		jMenu5 = new JMenu();
    		jMenu5.setText("Anordnen");
    		jMenu5.add(getJMenuItem4());
    		jMenu5.add(getJMenuItem5());
    		jMenu5.add(getJMenuItem7());
    		jMenu5.add(getJMenuItem8());
    	}
    	return jMenu5;
    }
    /**
     * This method initializes jMenuItem4	
     * 	
     * @return javax.swing.JMenuItem	
     */    
    private JMenuItem getJMenuItem4() {
    	if (jMenuItem4 == null) {
    		jMenuItem4 = new JMenuItem();
    		jMenuItem4.setText("Nebeneinander");
    		jMenuItem4.addActionListener(new java.awt.event.ActionListener() { 
    			public void actionPerformed(java.awt.event.ActionEvent e) {    
                    tileFrames(ARRANGE);
    			}
    		});
    	}
    	return jMenuItem4;
    }
    /**
     * This method initializes jMenuItem5	
     * 	
     * @return javax.swing.JMenuItem	
     */    
    private JMenuItem getJMenuItem5() {
    	if (jMenuItem5 == null) {
    		jMenuItem5 = new JMenuItem();
    		jMenuItem5.setText("Horizontal");
    		jMenuItem5.addActionListener(new java.awt.event.ActionListener() { 
    			public void actionPerformed(java.awt.event.ActionEvent e) {    
                    tileFrames(HORIZONTAL);
    			}
    		});
    	}
    	return jMenuItem5;
    }
    /**
     * This method initializes jMenuItem7	
     * 	
     * @return javax.swing.JMenuItem	
     */    
    private JMenuItem getJMenuItem7() {
    	if (jMenuItem7 == null) {
    		jMenuItem7 = new JMenuItem();
    		jMenuItem7.setText("Vertikal");
    		jMenuItem7.addActionListener(new java.awt.event.ActionListener() { 
    			public void actionPerformed(java.awt.event.ActionEvent e) {    
                    tileFrames(VERTICAL);
    			}
    		});
    	}
    	return jMenuItem7;
    }
    /**
     * This method initializes jMenuItem8	
     * 	
     * @return javax.swing.JMenuItem	
     */    
    private JMenuItem getJMenuItem8() {
    	if (jMenuItem8 == null) {
    		jMenuItem8 = new JMenuItem();
    		jMenuItem8.setText("Cascadiert");
    		jMenuItem8.addActionListener(new java.awt.event.ActionListener() { 
    			public void actionPerformed(java.awt.event.ActionEvent e) {    
                    tileFrames(CASCADE);
    			}
    		});
    	}
    	return jMenuItem8;
    }
   }  //  @jve:decl-index=0:visual-constraint="10,10"