/**
 * Created on 25.03.2005
 * Project: InfVis
 * @author: tobias
 */

package gui.scatterplot;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Dimension;
import java.util.*;
import javax.swing.*;

import sys.main.*;
import sys.sql.managers.SQLScatterDataListManager;

import sys.helpers.SimpleDataObject;

public class JScatterplotPanel extends JPanel implements ActionListener, MouseListener {
    private static SysCore sysCore = null;
	public Vector dispData = null;
	private JPanel scatterPane = null;
	private JPanel scatterGrPane = null;
	private ScatterArea scap;
	private ScatterContext scat;
	private SQLScatterDataListManager sqlspmanager = null;
	
	public JScatterplotPanel(Dimension _d,SysCore _sysCore,SQLScatterDataListManager _scp) {
		super();
		sqlspmanager = _scp;
		sysCore = _sysCore;
		initialize(_d);		
	}
	
	public ScatterArea getScatterArea(){
	    return scap;
	}
	
	private void initialize(Dimension _d){
		//this.setContentPane(getMyContentPane());
		//this.setContentPane(getScatterPane());
		//this.setName("Testfenster");
		//this.setDefaultCloseOperation(
			//WindowConstants.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setBounds(45, 25, (int)_d.getWidth(),(int)_d.getHeight());
		//this.setTitle("ScatterPlot");
		this.add(getScatterPane(), java.awt.BorderLayout.CENTER);
		//this.setResizable(true);
		this.addComponentListener(new java.awt.event.ComponentAdapter() { 
			public void componentResized(java.awt.event.ComponentEvent e) {    
				resizeScap();
			}
		});
		fillWithData();
	}
	
	private void resizeScap(){
	    scap.setPreferredSize(this.getSize());
	    scap.setSize(this.getSize());
	    scap.setPrefSize(this.getSize());
	//    scap.resetAllObjects();
	    scap.setMinMax();
	    fillWithData();
	    //scap.resetGr();
	    //scap.paint(scap.gr);
	    //scap.updateUI();
	    //System.out.println(this.getWidth() + " " + scap.getWidth());
	    
	    //getScatterGrPane().updateUI();
	    //BufferedImage bi = new BufferedImage(getPreferredSize().width, getPreferredSize().height,BufferedImage.TYPE_INT_ARGB);
		//gr  = bi.getGraphics();
	    //this.updateUI();
	}
	
	private JPanel getScatterPane() {
		if(scatterPane == null) {
			scatterPane = new JPanel();
			scatterPane.setLayout(new BorderLayout());
			scatterPane.add(getScatterGrPane(), java.awt.BorderLayout.CENTER);
		}
		return scatterPane;
	}
	
	private JPanel getScatterGrPane() {
		if(scatterGrPane == null) {
			scatterGrPane = new JPanel();
			
			scatterGrPane.removeAll();
			try {
			    this.remove(scap);
			}
			catch (Exception exc){
			    
			}
			
			
			// Create new Scatterplot, set Box & Mesh & Tooltips visible
			scap = new ScatterArea(this.getWidth(),this.getHeight(),true,true,true,sqlspmanager){
				public JToolTip createToolTip()
				{
					return new JMultiLineTooltip();
				}
			};
			scat = new ScatterContext(this);
			scap.setLayout(new BorderLayout());
			scap.setBackground(new java.awt.Color(255,102,255));
			
			scap.addMouseListener(this);
			scat.addActionListener(this);
			
			enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK);
			
			add(scat);
			scatterGrPane.add(scap, java.awt.BorderLayout.CENTER);
		}
		return scatterGrPane;
	}
	
	public void fillWithData(){
	    //scap.resetAllObjects();
	    if (sysCore.isDebug())
	        System.out.println("Start fillWithData()...");
	    
	    if (sqlspmanager == null) {
	    	
	    	scap.setMinMax(0,0,10000,10000);
	    	
	        scap.newData("id1",50.333,10);
	        scap.newData("id2",20.455,0.445);
	        scap.newData("id3",30,30);
	        scap.newData("id4",40,138);
	    
	        for (int i=5; i<8000; i++){
	            scap.newData("id"+i,/*Math.cos(i)*/i,i);
	            //System.out.println("processing point: id" + i);
	        }
	    }
	    else {
	        /*
	         //Refresher thread
	    xx = new RefreshThread(this,sysCore);
	    xx.setWaitingTime((long)(jSpinField.getValue())*1000l);
	    Thread tr = new Thread(xx);
        tr.start();
	         */
	        /*
	        for (int i=0; i<dispData.size(); i++){
	            SimpleDataObject sdo = (SimpleDataObject)dispData.elementAt(i);
	            scap.newData(sdo.getID(),sdo.getX(),sdo.getY());
	        }
	        */
	        
	        double[] tmp_bounds = sqlspmanager.getBounds();
		 
	        scap.setMinMax(tmp_bounds[0],tmp_bounds[1],tmp_bounds[2],tmp_bounds[3]);
	        
	        for (int i=0; i<sqlspmanager.getDataArray().length; i++){
	            scap.newData("id" + i, sqlspmanager.getDataArray()[i][0], sqlspmanager.getDataArray()[i][1]);
	        }
	        
	    }
	    
	    if (sysCore.isDebug())
	        System.out.println("End fillWithData()...");
	    
		// TODO: Bei Größenänderung Daten neu schicken !!!
	}
		
	public void fillWithData(double[] tmp_bounds, double[][] _data){
	    //scap.resetAllObjects();
	    if (sysCore.isDebug())
	        System.out.println("Start fillWithData()...");
	    
	    scap.setMinMax(tmp_bounds[0],tmp_bounds[1],tmp_bounds[2],tmp_bounds[3]);
	    
	    for (int i=0; i<_data.length; i++){
	        //System.out.println(_data[i][0] + " und " + _data[i][1]);
	        scap.newData("id" + i, _data[i][0], _data[i][1]);
	    }
	    
	    if (sysCore.isDebug())
	        System.out.println("End fillWithData()...");
	    
	}
	
	public void setSQLSPManager(SQLScatterDataListManager _sqlspmanager){
	    sqlspmanager = _sqlspmanager;
	    scap.setSQLSPManager(sqlspmanager);
	}
	
	public void mouseClicked(MouseEvent evt) {
		int x = evt.getX();
		int y = evt.getY();
		int modifier = evt.getModifiers();
		
		if (modifier == 4){
			//System.out.println("Rechtsklick in "+evt.getComponent());
			scat.show(evt.getComponent(),x,y);
		}
		
	}

	public void actionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
	}
	
	public void mouseEntered(MouseEvent evt) {
		// TODO Auto-generated method stub
	}

	public void mouseExited(MouseEvent evt) {
		// TODO Auto-generated method stub
	}

	public void mousePressed(MouseEvent evt) {
		// TODO Auto-generated method stub
	}

	public void mouseReleased(MouseEvent evt) {
		// TODO Auto-generated method stub
	}
	
	public class ScatterContext extends PopupMenu {
		
		public ScatterContext(ActionListener list) {
			
				CheckboxMenuItem tooltip = new CheckboxMenuItem ("Show tooltips",true);
				tooltip.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e){
						scap.changeTooltipsState();
					}
				});
				if (tooltip.getState()==true) tooltip.setActionCommand(ScatterArea.TOOLTIP_TRUE);
				else tooltip.setActionCommand(ScatterArea.TOOLTIP_FALSE);
				add(tooltip);
				
				addSeparator();
				
				CheckboxMenuItem box = new CheckboxMenuItem ("Show box",true);
				box.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e){
						scap.changeBoxState();
					}
				});
				add(box);
				
				CheckboxMenuItem mesh = new CheckboxMenuItem ("Show mesh",true);
				mesh.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e){
						scap.changeMeshState();
					}
				});
				add(mesh);
			}
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
