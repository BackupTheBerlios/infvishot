/**
 * Created on 25.03.2005
 * Project: InfVis
 * @author: tobias
 */

package gui.scatterplot;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import sys.helpers.TimeMeasureObject;
import sys.sql.managers.SQLMosaicDataListManager;
import sys.sql.managers.SQLScatterDataListManager;

public class ScatterArea extends DrawArea implements MouseListener, MouseMotionListener{

	protected int pointSize;
	protected double minX,maxX,minY,maxY;
	protected int[] margins;
	protected Color pointColor;
	protected int rubStartX,rubStartY,rubLastX,rubLastY,rubDiffX,rubDiffY;
	protected static double mx,bx,my,by;
	protected boolean mesh, box, tooltips;
	protected boolean rubInit, rubEnd, objectsEnclosed, holdSelected, transientActive;
	protected boolean useTransientMode;
	protected boolean hasValues;
	protected int[][] objects;
	protected int objW, objH;
	//protected ScatterTooltip stt;
	private SQLScatterDataListManager sqlspmanager = null;

	public final static Color DEFAULT_POINT_COLOR = new Color(50,50,200);
	public final static int DEFAULT_POINT_SIZE = 2; // should be even!
	public final static Font DEFAULT_FONT = new Font("TimesRoman",Font.PLAIN,9),
	 						 tr10 = new Font("Arial",Font.PLAIN,10),
							 tr12 = new Font("TimesRoman",Font.BOLD,12);
	
	protected final static String TOOLTIP_TRUE = "tooltipt";
	protected final static String TOOLTIP_FALSE = "tooltipt";
	protected final static String BOX_TRUE = "boxt";
	protected final static String BOX_FALSE = "boxt";
	protected final static String MESH_TRUE = "mesht";
	protected final static String MESH_FALSE = "meshf";
	
	public ScatterArea (int width, int height, boolean m, boolean b, boolean t,SQLScatterDataListManager _scp){
		super(width,height);
		sqlspmanager = _scp;
		
		initialize();
		mesh = m;
		box = b;
		tooltips = t;
	    margins = new int[4];
	    margins[0] = 30;
	    margins[1] = 56;
	    margins[2] = 20;
	    margins[3] = 30;
	    
	    useTransientMode = false;
	    hasValues = false;
	    
	    System.out.println("Breite: " + objW + " / Höhe: " + objH);
	    
		rubInit = false;
	    objectsEnclosed = false;
	    
	    super.setBackground(Color.black);
	    pointColor = DEFAULT_POINT_COLOR;
	    pointSize = DEFAULT_POINT_SIZE;
	    
	    //allObjects = new Vector();
	    
	    addMouseListener(this);
	    addMouseMotionListener(this);
	    ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	private void initialize() {
        this.addComponentListener(new java.awt.event.ComponentAdapter() { 
        	public void componentResized(java.awt.event.ComponentEvent e) {    
        		System.out.println("componentResized()"); // TODO Auto-generated Event stub componentResized()
        	}
        });
			
	}
	
	public void setSQLSPManager(SQLScatterDataListManager _sqlspmanager){
	    sqlspmanager = _sqlspmanager;
	}
	
	public void paint(Graphics g) {
		
	    g.setColor(Color.black);
	    g.fillRect(1,1,width-2,height-2);
	    
	    int yOrigin=height,
        	xOrigin=0,
			bottom=-margins[0],
			left=margins[1],
			top=margins[2],
			right=margins[3],
			xtick,ytick,
			xlabWidth,
			x1,y1,x2,y2,i,xA,yA;
	    
	    // draw axes and objects
	    g.setFont(tr10);
	    FontMetrics fm=getFontMetrics(tr10);

	    ytick=(int)((yOrigin+bottom-top)*.25);
	    xtick=(int)((width-left-right)*.25);
	    String[] yL = getLabels(minY,maxY,5);
	    String[] xL = getLabels(minX,maxX,5);
	    
	    for (i=0;i<5;i++) {
	    	g.setColor(Color.white);
	    	// create y-axis tick marks and ylabs
	    	g.drawLine(xOrigin+left-3, yOrigin+bottom-i*ytick, xOrigin+left, yOrigin+bottom-i*ytick);
	    	xlabWidth=44;
	    	g.drawString(yL[i], xOrigin+left-xlabWidth-4, yOrigin+bottom-i*ytick+4);

	    	// create x-axis tick marks and xlabs
	    	g.drawLine(xOrigin+left+i*xtick, yOrigin+bottom, xOrigin+left+i*xtick, yOrigin+bottom+4);
	    	xlabWidth=18;
	    	g.drawString(xL[i], xOrigin+left+i*xtick-xlabWidth, yOrigin+bottom+19);
	    	
		    // draw mesh if mesh==true
		    if (mesh){
		    	g.setColor(Color.darkGray);
		    	g.drawLine(xOrigin+left+1, yOrigin+bottom-i*ytick, width-right+8, yOrigin+bottom-i*ytick);
		    	g.drawLine(xOrigin+left+i*xtick, yOrigin+bottom, xOrigin+left+i*xtick, top-8);
		    }
	    }

	    g.setColor(Color.white);
	    
	    // draw x-axis & box above
	    g.drawLine(xOrigin+left,yOrigin+bottom,width-right+8,yOrigin+bottom);
	    if (box) g.drawLine(xOrigin+left,top-8,width-right+8,top-8);
	    
	    // draw y-axis & box right
	    g.drawLine(xOrigin+left,yOrigin+bottom,xOrigin+left,top-8);
	    if (box) g.drawLine(width-right+8,top-8,width-right+8,yOrigin+bottom);
	    
	    // draw Points
	    drawObjects(g);
	    
	    // TODO: Das richtig machen!
	    /*if (useTransientMode){
	    	g.setColor(Color.cyan.darker());
	    	drawRubRectangle(g, rubStartX, rubStartY, rubLastX, rubLastY);
	    }*/
	}

	public String getToolTipText(java.awt.event.MouseEvent evt) {
		String s = null;
		if (rubInit && rubEnd && objectsEnclosed && tooltips){
			
			int x = evt.getX();
			int y = evt.getY();
			
			if ((new Rectangle(Math.min(rubStartX,rubLastX),
							   Math.min(rubStartY,rubLastY),
							   Math.abs(rubLastX-rubStartX),
							   Math.abs(rubLastY-rubStartY)).contains(x,y))){
				s = " Number of Objects: " + getNumberOfEnclosedPoints()[0] + " \n" +
					" Uniquely plotted objects:" + getNumberOfEnclosedPoints()[1] + " ";	
			}
		}
		return s;
    }   
	
	// Shall a mesh be drawn?
	public void useMesh(boolean m){
		mesh = m;
		repaint();
	}
	
	public void changeMeshState(){
		if (mesh == true) { useMesh(false); return; }
		else if (mesh == false) {useMesh(true); return; }
	}
	
	// Shall a box be drawn?
	public void useBox(boolean b){
		box = b;
		repaint();
	}
	
	public void changeBoxState(){
		if (box == true) { useBox(false); return; }
		else if (box == false) {useBox(true); return; }
	}
	
	// Shall tooltips be shown?
	public void useTooltips(boolean t){
		tooltips = t;
		repaint();
	}
	
	public void changeTooltipsState(){
		if (tooltips == true) { useTooltips(false); return; }
		else if (tooltips == false) {useTooltips(true); return; }
	}
	
	// Get Labels in the right format
	public String[] getLabels (double min, double max, int anz){
		String[] sa = new String[anz];
		double[] da = new double[anz];

		for (int i=0; i<anz; i++){
			da[i] = (min+((max-min)/(anz-1))*i);
			
			java.text.DecimalFormat formatter = new java.text.DecimalFormat("0.00E0");   // exponent must be multiple of 2
		    sa[i] = formatter.format(da[i]);
		    
		    if (!(sa[i].substring(sa[i].length()-2, sa[i].length()-1).equals(new String("-"))))
		    	sa[i] = sa[i].substring(0,sa[i].indexOf("E")+1)+"+"+sa[i].substring(sa[i].indexOf("E")+1,sa[i].length());
	
			//System.out.println(sa[i]);
		}
		return sa;
	}
	
	public void calculateParameters(){
	    int yOrigin=height,
    		xOrigin=0,
			bottom=-margins[0],
			left=margins[1],
			top=margins[2],
			right=margins[3];

		// calculate Parameters
		mx=(width-right-left)/(maxX-minX);
	    bx=left-mx*minX+2;
	    my=(yOrigin+bottom-top)/(minY-maxY);
	    by=yOrigin+bottom-my*minY-2;
	}
	
	public void drawObjects(Graphics g){
		int maxValue = 1;
		
		for (int i = 0; i < width; i++) {
			for (int k = 0; k < height; k++){
				maxValue = Math.max(maxValue,objects[i][k]);
			}
		}
		
		for (int i = 0; i < width; i++) {
			for (int k = 0; k < height; k++){
				//System.out.println();
				//System.out.println(i);
				//System.out.println();
				//System.out.println(k);
				if(objects[i][k]!=0){
					//System.out.println("Point (" + i + "/" + k + "), Value: " + objects[i][k]);
					drawO(objects[i][k], i, k, maxValue, g);
				}
			}
		}
		//System.out.println("fertig");
	}
	
	public void drawO(int value, int x, int y, int maxV, Graphics g){
		//System.out.println("Object drawn: (" + x + "/" + y + ") with value " + value);
		setCol(value, maxV, g);
		g.drawOval(x,y,1,1);
		//g.setColor(Color.WHITE);
	}
	
	public void setCol(int val, int maxVal, Graphics g){
		Color c = new Color(255,255,255);	
		if (val < 0) c = Color.cyan.darker();
		else if (val > 0){
			for(int i=val; i>0; i--){
				c = redder(c, maxVal);
			}
		}	
		g.setColor(c);
	}
	
	public Color redder (Color c, int maxV){
		int step = Math.max(1, Math.round(255/maxV));
		return new Color(255,Math.max(0,c.getGreen()-step),Math.max(c.getBlue()-step,0));
	}
	
	//Harald
	public boolean setMinMax(){
	    this.setMinMax(minX,minY,maxX,maxY);
	    
	    return true;
	}
	
	public boolean setMinMax(double miX, double miY, double maX, double maY){
		minX = miX;
		minY = miY;
		maxX = maX;
		maxY = maY;
		
		objW = width-(margins[1]+margins[3]);
		objH = height-(margins[0]+margins[2]);
	    objects = new int[width][height];
	    
	    // Evtl. lässt sich das noch optimieren (mit Vektoren)
	    for (int i=0; i<width; i++){
	    	for (int k=0; k<height; k++){
	    		objects[i][k] = 0;
	    	}
	    }
	    
		return true;
	}
	
	public void newData(String id, double x, double y){
		boolean newVal = false;
		
		calculateParameters();
		//System.out.println(convertX(x) + "/" + convertY(y));
		objects[convertX(x)][convertY(y)]++;
		
		//repaint();
	}
	
	public static double[] getParameters() {
 	   return (new double[]{mx,bx,my,by});
	}
	
	public static int convertX(double x){
		double[] p = getParameters();
		return (int)(x*p[0]+p[1]);
	}
	
	public static int convertY(double y){
		double[] p = getParameters();
		return (int)(y*p[2]+p[3]);
	}
	
	public static int convRevX(double x){
		double[] p = getParameters();
		return (int)(x/p[0]-p[1]/p[0]);
	}
	
	public static int convRevY(double y){
		double[] p = getParameters();
		return (int)(y/p[2]-p[3]/p[2]);
	}
	
	public void mousePressed(MouseEvent evt) {
		int x        = evt.getX();
		int y        = evt.getY();
		int modifier = evt.getModifiers();
		
		if (!(modifier == 18)){
			// Reset Objects:
			for (int i=0; i<width; i++){
				for (int k=0; k<height; k++){
					if (objects[i][k] < 0) objects[i][k]*=(-1);
				}
			}
		}
		
		System.out.println(x+"/"+y + " with modifier: " + modifier);
		
		rubInit = false;
		rubEnd = false;
		holdSelected = false;
		objectsEnclosed = false;
		transientActive = false;
		
		// nur maustaste, ohne tastatur!
		if (modifier == 16 || modifier == 18){
		
			int yOrigin=height,
    			xOrigin=0,
				bottom=-margins[0],
				left=margins[1],
				top=margins[2],
				right=margins[3];
	    
			rubInit = false;
	    
			if (x < width-right+8 && x > xOrigin+left && y < yOrigin+bottom && y > top-8){
				rubInit = true;
				rubStartX = x;
				rubStartY = y;
				rubLastX = x;
				rubLastY = y;
			}
			
			if (modifier == 18) {
				holdSelected = true; // Beim zurücksetzen wird diese Variable vorher abgefragt
			}
		}
		
		if (modifier == 24){
			Graphics g = getGraphics();
			g.setXORMode(Color.green.darker());
			drawRubRectangle(g,x,y,x+(Math.abs(rubLastX-rubStartX)),y+(Math.abs(rubStartY-rubLastY)));
			rubDiffX = (Math.abs(rubLastX-rubStartX));
			rubDiffY = (Math.abs(rubStartY-rubLastY));
			transientActive = true;
			rubInit = true;
		}
	}
	
	public void mouseDragged(MouseEvent evt) {
	    int x = evt.getX();
	    int y = evt.getY();

      	Graphics g = getGraphics();
      	g.setXORMode(Color.cyan.darker());
	    
	    if (rubInit && (!transientActive)){

	    	drawRubRectangle(g,rubStartX, rubStartY, rubLastX, rubLastY);
	      	drawRubRectangle(g,rubStartX, rubStartY, x, y);

	      	rubLastX = x;
	      	rubLastY = y;
	    }
	    
	    else if (rubInit && transientActive){
	    	g.setXORMode(Color.green.darker());
	    	int xL = x, yL = y;
	    	//drawRubRectangle(g,xL, yL, xL+rubDiffX, yL+rubDiffY);
	    	drawRubRectangle(g,x, y, x+rubDiffX, y+rubDiffY);
	    	System.out.println("dragged");
	    }
	    
	}
	
	private void drawRubRectangle(Graphics g, int startX, int startY, int stopX, int stopY ) {
	  	int x, y, w, h;
	  	int yOrigin=height,
    		xOrigin=0,
			bottom=-margins[0],
			left=margins[1],
			top=margins[2],
			right=margins[3];
	  	x = Math.min(startX, stopX);
	  	y = Math.min(startY, stopY);
	  	w = Math.abs(startX - stopX);
	  	h = Math.abs(startY - stopY);

	  	if ((x+w) >= (width-right+8)) w = w-(x+w-(width-right+7));
	  	if (x <= (xOrigin+left)) { x = xOrigin+left+1; w = Math.abs(x - Math.max(startX,stopX)); }
	  	if ((y+h) >= (yOrigin+bottom)) h = h-(y+h-(yOrigin+bottom))-1;
	  	if (y <= (top-8)) { y = top-8+1; h = Math.abs(y - Math.max(startY,stopY)); }
	  	g.drawRect(x, y, w, h);
	}

	public void mouseReleased(MouseEvent e) {
		// System.out.println("Rect: " + rubStartX + "/" + rubStartY + " and " + rubLastX + "/" + rubLastY);
		if (rubInit) { rubEnd = true; checkEnclosedPoints(); /*setTransient();*/ }
	}
	
	public void checkEnclosedPoints(){
		System.out.println("Rectangle: minX: " + convRevX(Math.min(rubStartX,rubLastX)) + 
									", maxX: " + convRevX(Math.max(rubStartX,rubLastX)) + 
									", minY: " + convRevY(Math.max(rubStartY,rubLastY)) + 
									", maxY: " + convRevY(Math.min(rubStartY,rubLastY)));

		int tempMinX = convRevX(Math.min(rubStartX,rubLastX));
	    int tempMaxX = convRevX(Math.max(rubStartX,rubLastX));
	    int tempMinY = convRevY(Math.max(rubStartY,rubLastY));
	    int tempMaxY = convRevY(Math.min(rubStartY,rubLastY));
	    
	    
		if (sqlspmanager != null) {
		    sqlspmanager.setBounds(tempMinX,tempMinY,tempMaxX,tempMaxY);
		    sqlspmanager.loadData();
		    
		    TimeMeasureObject tmo = new TimeMeasureObject();
		    tmo.start();
		    
		    for (int i=0; i<sqlspmanager.getDataArray().length; i++){
		        int arrayX = Math.max(0,convertX(sqlspmanager.getDataArray()[i][0]));
	            arrayX = Math.min(width-1,convertX(sqlspmanager.getDataArray()[i][0]));
	            
	            int arrayY = Math.max(0,convertY(sqlspmanager.getDataArray()[i][1]));
	            arrayY = Math.min(height-1,convertY(sqlspmanager.getDataArray()[i][1]));
	            
	            if (objects[arrayX][arrayY] > 0){
	                objects[arrayX][arrayY]=(-1)*objects[arrayX][arrayY];
	                objectsEnclosed = true;
	            }
		    }
		    
		    tmo.stop();
		    
		    //Mosaic
		    TimeMeasureObject tmo1 = new TimeMeasureObject();
		    tmo1.start();
		    sqlspmanager.getSysCore().getMainFrm().imosaicfrm.fillMosaic(tempMinX,tempMinY,tempMaxX,tempMaxY,sqlspmanager.getStringDataArray());
		    sqlspmanager.getSysCore().getMainFrm().imosaicfrm.mainWindow.setSQLManager(new SQLMosaicDataListManager(sqlspmanager.getSysCore(),sqlspmanager.getSysCore().getMainFrm().isettingsfrm.jComboBox.getSelectedItem().toString(),sqlspmanager.getSysCore().getMainFrm().imosaicfrm.jComboBox.getSelectedItem().toString(), sqlspmanager.getSysCore().getMainFrm().imosaicfrm.jComboBox1.getSelectedItem().toString(),sqlspmanager.getSysCore().getMainFrm().iscatterfrm.jComboBoxXAxis.getSelectedItem().toString(),sqlspmanager.getSysCore().getMainFrm().iscatterfrm.jComboBoxYAxis.getSelectedItem().toString(),false));
		    tmo1.stop();
		    
		    sqlspmanager.getSysCore().getMainFrm().iperformancefrm.addTimeRow(2,sqlspmanager.getTime().getTimeDiff(),tmo.getTimeDiff(),tmo1.getTimeDiff(),"Select Scatterplot",sqlspmanager.getDataArray().length);
		}
		else {
		    	    
		    
		    for (int i=tempMinX; i<tempMaxX; i++){
		        for (int k=tempMinY; k<tempMaxY; k++){
		            
		            int arrayX /*= Math.max(0,convertX(i));
		            arrayX*/ = Math.min(width-1,Math.max(0,convertX(i))/*convertX(i)*/);
		            
		            int arrayY /*= Math.max(0,convertY(k));
		            arrayY*/ = Math.min(height-1,Math.max(0,convertY(k))/*convertY(k)*/);
		            
		            if (objects[arrayX][arrayY] > 0){
		                objects[arrayX][arrayY]=(-1)*objects[arrayX][arrayY];
		                objectsEnclosed = true;
		            }
		        }
		    }
		}
		repaint();
	}
	
	public void checkEnclosedPoints(double[][] _data){
		
	    for (int i=0; i<width; i++){
			for (int k=0; k<height; k++){
				if (objects[i][k] < 0) objects[i][k]*=(-1);
			}
		}

	    objectsEnclosed = false;
	    
	    for (int i=0; i<_data.length; i++){
	        int arrayX = Math.max(0,convertX(_data[i][0]));
	        arrayX = Math.min(width-1,convertX(_data[i][0]));
	        
	        int arrayY = Math.max(0,convertY(_data[i][1]));
	        arrayY = Math.min(height-1,convertY(_data[i][1]));
	        
	        if (objects[arrayX][arrayY] > 0){
	            objects[arrayX][arrayY]=(-1)*objects[arrayX][arrayY];
	            objectsEnclosed = true;
	        }
	    }
	    
	    repaint();
	}
	
	public int[] getNumberOfEnclosedPoints(){
		int[] numb = new int[2];
		numb[0]=0;  // enclosed points
		numb[1]=0;  // enclosed points that are plotted at different positions
		
		for (int i = 0; i < width; i++) {
			for (int k = 0; k < height; k++){
				if (objects[i][k] < 0) {
					numb[0] += ((-1)*objects[i][k]);
					numb[1]++ ;
				}
			}
		}
		return numb;
	}
	
    public void mouseEntered(MouseEvent e) {
    	return;
    	// TODO Auto-generated method stub
	}

	public void mouseExited(MouseEvent e) {
		return;
		// TODO Auto-generated method stub
	}
	public void mouseClicked(MouseEvent e) {
		return;
		// TODO Auto-generated method stub
	}

	public void mouseMoved(MouseEvent e) {
		return;
		// TODO Auto-generated method stub
	}
	
	/*public void setTransient(){
		useTransientMode = true;
		System.out.println("Transient!");
	}*/
	
	// TRANSIENT IDEEN
	/*
	 * ==> Harald muss key-Event ('t' an mich übergeben), d.h. Methode setTransient() aufrufen
	 * ==> in setTransient() rect gemalt, falls Mausklick hinein boolean gesetzt
	 * - bei Harry Timer (3 sec), wenn dann bool noch false ==> unsetTransient() (rect entfernen)
	 * - draggen: Maus-Aussehen Drag (Oli), Rechteck draggen
	 * - bei maus-release: ganz normal!
	 * 
	 * 
	 */
}
