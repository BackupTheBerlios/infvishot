package gui.mosaic;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

import sys.helpers.TimeMeasureObject;
import sys.sql.managers.SQLMosaicDataListManager;
import sys.sql.managers.SQLScatterDataListManager;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MainWindow  extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
	MosaicArea mos;
    private int catCnt = 10;
    
	//private Vector h;
	private double distance;
	JFrame frame;
	private DataObject Daten, selected;
	private String[][] datas, selectedData;
	private String[] names, selectedDataNames;
	private ProcessData prozi, filledProzi;
	private SQLMosaicDataListManager sqlmd = null;
	//private DataCount DataCounter;
	//public boolean RectsFilled = false;
	//public double dist = 0.1;
	
	public void setSQLManager(SQLMosaicDataListManager _sqlm){
	    sqlmd = _sqlm;
	}
	
	public MainWindow(boolean _showmarker){
	    datas = new String [2][7];
		names = new String[2];
		
		
		datas[0][0] = "Franz";
		datas[1][0] = "gscheit";
		datas[0][1] = "Oliver";
		datas[1][1] = "blöd";
		datas[0][2] = "Herbert";
		datas[1][2] = "blöd";
		datas[0][3] = "Franz";
		datas[1][3] = "gscheit";
		datas[0][4] = "Franz";
		datas[1][4] = "gscheit";
		datas[0][5] = "Oliver";
		datas[1][5] = "gscheit";
		datas[0][6] = "Franz";
		datas[1][6] = "blöd";
		
		names[0] = "Spalte1";
		names[1] = "Spalte2";
		
		initialize();
		
		this.Markers(_showmarker);
	}
	
	public MainWindow(String[][] data, String[] nam, boolean _showmarker){
		datas = data;
		names = nam;
		
		initialize();
		
		this.Markers(_showmarker);
	}
	
	public void setData(String[][] data, String[] nam){
	    datas = data;
		names = nam;
		
		this.removeAll();
		initialize();
		resizeMos();
		this.updateUI();
	}
	
	
    
	public void initialize(){
        Daten = new DataObject(names, datas);
        prozi = new ProcessData(Daten, names[0], names[1], catCnt);
        
        //frame = new JFrame("Mosaic Plot");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mos = new MosaicArea(550, 500, prozi.getVector(), prozi.dist){
            public JToolTip createToolTip()
            {
                return new JMultiLineTooltip();
            }
        };
        mos.addMouseListener(this);
        mos.addMouseMotionListener(this);
        enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK);
        
        
        this.add(mos);
        
        //frame.getContentPane().setLayout(new BorderLayout()); 
        //frame.getContentPane().add(this, BorderLayout.CENTER);
        
        //Display the window.
        //frame.pack();
        //frame.setVisible(true);
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() { 
            public void componentResized(java.awt.event.ComponentEvent e) {    
                resizeMos();
            }
        });

    }
    
    
	public void resizeMos(){
		/**frame.remove(this);
		this.remove(mos);
		Dimension dd = new Dimension();
		dd = this.getSize();
		mos = new MosaicArea((int)dd.width, (int)dd.height, h, distance);
		this.add(mos);
		frame.getContentPane().add(this, BorderLayout.CENTER);
		frame.pack();
        frame.setVisible(true);**/
		//System.out.println(frame.getSize());
		mos.setPreferredSize(this.getSize());
	    mos.setSize(this.getSize());
	    mos.setPrefSize(this.getSize());
	    
	    mos.refreshSize();
	}
    
	//set catCnt to specifiy how many categories are allowed in the plot
	// default value for catCnt is 10
	public void setCatCnt(int c){
	    catCnt = c;
	}
	
	public void fillRects(String[][] selData, String[] selDataNam){
        selectedData = selData;
        selectedDataNames = selDataNam;

        selected = new DataObject(selectedDataNames, selectedData);
        filledProzi = new ProcessData(selected, selectedDataNames[0],selectedDataNames[1], catCnt);
        mos.fill(filledProzi.getVector());
        mos.setRectsFilled(true);
        mos.resetSelected();
        mos.repaint();
        this.updateUI();
        //resizeMos();
        //resizeMos();
	}
	
	//enable or disable Markers
    public void Markers(boolean b){
        mos.markerSwitch = b;
        mos.repaint();
        this.updateUI();

    }
	
    public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked
(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		this.updateUI();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered
(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited
(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed
(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent evt) {
		// TODO Auto-generated method stub
	    if(evt.getModifiers() == 4){
			mos.repaint();
			this.updateUI();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased
(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent evt) {
	    if(evt.getModifiers() == 16 || evt.getModifiers() == 17 || evt.getModifiers() == 18){
	        //Query = mos.getQuery();
	        if(mos.Query != null){
	            mos.Query[0][0] = names[0];
	            mos.Query[1][0] = names[1];
	            //this.updateUI();
	            mos.drawRubRec(mos.getGraphics());
	        }
	        
	        Vector tmpv = new Vector();
	        if (returnQuery() != null && sqlmd != null){
	            for (int i=0; i<returnQuery().length; i++){
	                if (returnQuery()[i] == null)
	                    continue;
	                StringBuffer sb1 = new StringBuffer();
	                
	                for (int j=1; j<returnQuery()[i].length; j++){
	                    if (returnQuery()[i][j] == null)
	                        continue;
	                    //    System.out.println(returnQuery()[i][j] + "i:" + i + " j: " + j + " " + returnQuery()[i].length);
	                    
	                    String tmp_s = "";
	                    if (j < returnQuery()[i].length) {
	                        tmp_s = " OR ";
	                    }
	                    sb1.append(returnQuery()[i][0] + "='" + returnQuery()[i][j] + "' " +  tmp_s);
	                }
	                
	                // System.out.println("(" + sb1.toString() + " 1=2)");
	                
	                tmpv.addElement("(" + sb1.toString() + " 1=2)");
	            }
	        }
	        
	        if (tmpv.size() > 0) {
	            StringBuffer sb = new StringBuffer();
	            
	            for (int i=0; i<tmpv.size(); i++){
	                sb.append(tmpv.elementAt(i) + " AND ");
	            }
	            
	            String o_query = "(" +  sb.toString() + " 1=1)";
	            
	            //System.out.println(o_query);
	            
	            sqlmd.loadData(o_query);
	            
	            TimeMeasureObject tmo1 = new TimeMeasureObject();
	            tmo1.start();
	            SQLScatterDataListManager tmpssdlm = new SQLScatterDataListManager(sqlmd.getSysCore(),sqlmd.getSysCore().getMainFrm().isettingsfrm.jComboBox.getSelectedItem().toString(),sqlmd.getSysCore().getMainFrm().iscatterfrm.jComboBoxXAxis.getSelectedItem().toString(),sqlmd.getSysCore().getMainFrm().iscatterfrm.jComboBoxYAxis.getSelectedItem().toString(),sqlmd.getSysCore().getMainFrm().imosaicfrm.jComboBox.getSelectedItem().toString(),sqlmd.getSysCore().getMainFrm().imosaicfrm.jComboBox1.getSelectedItem().toString(),false);
	            sqlmd.getSysCore().getMainFrm().iscatterfrm.jScatterplotPanel.setSQLSPManager(tmpssdlm);
	            sqlmd.getSysCore().getMainFrm().iscatterfrm.jScatterplotPanel.getScatterArea().checkEnclosedPoints(sqlmd.getDoubleDataArray());
	            // sqlmd.getSysCore().getMainFrm().iscatterfrm.jScatterplotPanel.updateUI();
	            tmo1.stop();
	            
	            int tmp_ll = 0;
	            try {
	                if (sqlmd.getDoubleDataArray()[0] == null) {
	                    tmp_ll = 0;
	                }
	                else {
	                    tmp_ll = sqlmd.getDoubleDataArray()[0].length;
	                }
	            }
	            catch (Exception exc){
	                
	            }
	            sqlmd.getSysCore().getMainFrm().iperformancefrm.addTimeRow(1,sqlmd.getTime().getTimeDiff(),tmo1.getTimeDiff(),0,"PaintScatter",tmp_ll);
	            
	            sqlmd.getSysCore().getMainFrm().iperformancefrm.mosaicSelection = o_query;
	            //sqlmd.getSysCore().getMainFrm().iperformancefrm.scatterBounds = null;
	        }
	    }
	    
	    mos.setRectsFilled(false);
	    mos.repaint();
        this.updateUI();
	}
	
	// returns actual Query
	public String[][] returnQuery(){
		return mos.Query;
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged
(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent evt) {
	    if(evt.getModifiers() == 16 || evt.getModifiers() == 17 || evt.getModifiers() == 18){
			mos.drawRubRec(mos.getGraphics());
			mos.repaint();
			this.updateUI();
		}
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved
(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
////////////////////////////////////////////////////////////////////////////////////////
	// change two cols
	public void changeCols(String s1, String s2){
		mos.changeColPos(s1, s2);
		mos.repaint();
		this.updateUI();
	}
	
	public void changeRows(String s1, String s2){
		mos.changeRowPos(s1, s2);
		mos.repaint();
		this.updateUI();
	}
	
	//get all ColNames
	public Vector getAllColnames(){
		return mos.colNames;
	}
	
	//get all RowNames
	public Vector getAllRownames(){
		return mos.rowNames;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////	

}