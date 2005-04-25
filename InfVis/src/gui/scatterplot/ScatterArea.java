/**
 * Created on 25.03.2005
 * Project: InfVis
 * @author: tobias
 */

package gui.scatterplot;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ScatterArea extends DrawArea implements MouseListener, MouseMotionListener{

	protected int pointSize;
	protected double minX,maxX,minY,maxY;
	protected int[] margins;
	protected Color pointColor;
	protected Vector allObjects;
	protected int rubStartX,rubStartY,rubLastX,rubLastY;
	protected static double mx,bx,my,by;
	protected boolean mesh, box, tooltips;
	protected boolean rubInit, rubEnd, objectsEnclosed;
	//protected ScatterTooltip stt;

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
	
	public ScatterArea (int width, int height, boolean m, boolean b, boolean t){
		super(width,height);
			initialize();
	    mesh = m;
		box = b;
		tooltips = t;
	    margins = new int[4];
	    margins[0] = 30;
	    margins[1] = 56;
	    margins[2] = 20;
	    margins[3] = 30;
	    rubInit = false;
	    objectsEnclosed = false;
	    super.setBackground(Color.black);
	    pointColor = DEFAULT_POINT_COLOR;
	    pointSize = DEFAULT_POINT_SIZE;
	    //stt = new ScatterTooltip();
	    
	    allObjects = new Vector();
	    
	    addMouseListener(this);
	    addMouseMotionListener(this);
	    ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        this.addComponentListener(new java.awt.event.ComponentAdapter() { 
        	public void componentResized(java.awt.event.ComponentEvent e) {    
        		System.out.println("componentResized()"); // TODO Auto-generated Event stub componentResized()
        	}
        });
			
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
	    bx=left-mx*minX;
	    my=(yOrigin+bottom-top)/(minY-maxY);
	    by=yOrigin+bottom-my*minY;
	}
	
	public void drawObjects(Graphics g){
		for(int i = 0; i < allObjects.size(); i++) {
		    DualObject obj = (DualObject) allObjects.elementAt(i);
		    obj.draw(g);
		}
	}
	
	public void newData(String id, double x, double y){
		boolean newVal = false;
		int size = allObjects.size();
		
		if (size != 0) {
			
			if (minX > x) { minX = x; minX -= (maxX-minX)/10; } // TODO: um Faktor verkleinern
			if (maxX < x) { maxX = x; maxX += (maxX-minX)/10; } // TODO: um Faktor vergrößern
			if (minY > y) { minY = y; minY -= (maxY-minY)/10; } // TODO: um Faktor verkleinern
			if (maxY < y) { maxY = y; maxY += (maxY-minY)/10; } // TODO: um Faktor vergrößern
			
			calculateParameters();
			
			boolean isDiff = true;
		/*	DualPoint obj;
			for(int i = 0; i < size; i++) {
				obj = (DualPoint) allObjects.elementAt(i);			
				if (convertX(obj.x) == convertX(x) && convertY(obj.y) == convertY(y)) {
					// In this case they are both going to be plottet on the same point
					isDiff = false;
					obj.anzGleich++; // Dont plot new point but increment anzGleich
					break;
				}
			}
		*/			
			if (isDiff){
				DualPoint dp = new DualPoint(id,x,y,this);
				allObjects.add(dp);				
			}
		}
		else {
			DualPoint dp = new DualPoint(id,x,y,this);
			allObjects.add(dp);
			minX = x-Math.abs(x)/10;
			maxX = x+Math.abs(x)/10;
			minY = y-Math.abs(y)/10;
			maxY = y+Math.abs(y)/10;
		}		
	//	repaint(); TODO:Harald
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
	
	public void mousePressed(MouseEvent evt) {
		int x        = evt.getX();
		int y        = evt.getY();
		int modifier = evt.getModifiers();
		
		// System.out.println(x+"/"+y + " with modifier: " + modifier);
		
		rubEnd = false;
		objectsEnclosed = false;
		
		if (modifier == 16){
		
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
		}
	}
	
	public void mouseDragged(MouseEvent evt) {
	    int x = evt.getX();
	    int y = evt.getY();
	      
	    if (rubInit){
	      	Graphics g = getGraphics();
	      	g.setXORMode(Color.cyan.darker());
	      	
	      	// x < width-right+8 && x > xOrigin+left && y < yOrigin+bottom && y > top-8
	      	
	      	drawRubRectangle(g,rubStartX, rubStartY, rubLastX, rubLastY);
	      	drawRubRectangle(g,rubStartX, rubStartY, x, y);

	      	rubLastX = x;
	      	rubLastY = y;
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

	public void mouseReleased(MouseEvent arg0) {
		// System.out.println("Rect: " + rubStartX + "/" + rubStartY + " and " + rubLastX + "/" + rubLastY);
		if (rubInit) { rubEnd = true; checkEnclosedPoints(); }
	}
	
	public void checkEnclosedPoints(){
		for(int i = 0; i < allObjects.size(); i++) {
		    DualPoint obj = (DualPoint) allObjects.elementAt(i);
		    obj.setState(DualObject.NORMAL);
		    double[] p = getParameters();
		    
		    if (Math.min(rubStartX,rubLastX) <= convertX(obj.x) &&
		    	convertX(obj.x) <= Math.max(rubStartX,rubLastX) &&
		    	Math.min(rubStartY,rubLastY) <= convertY(obj.y) &&
		    	convertY(obj.y) <= Math.max(rubStartY,rubLastY)){
		    		obj.setState(DualObject.SELECTED);
		    		//System.out.println("Object enclosed: " + obj.id);
		    		objectsEnclosed = true;
		    }
		}
		repaint();
	}
	
	public int[] getNumberOfEnclosedPoints(){
		int[] numb = new int[2];
		numb[0]=0;
		numb[1]=0;
		for(int i = 0; i < allObjects.size(); i++) {
		    DualPoint obj = (DualPoint) allObjects.elementAt(i);
		    double[] p = getParameters();
		    
		    if (Math.min(rubStartX,rubLastX) <= convertX(obj.x) &&
		    	convertX(obj.x) <= Math.max(rubStartX,rubLastX) &&
		    	Math.min(rubStartY,rubLastY) <= convertY(obj.y) &&
		    	convertY(obj.y) <= Math.max(rubStartY,rubLastY)){
		    		numb[0] += obj.anzGleich;
		    		numb[1] += 1;
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

	public void mouseMoved(MouseEvent evt) {
		
		if (rubInit && rubEnd && tooltips && objectsEnclosed){
			
			int x = evt.getX();
			int y = evt.getY();
			int[] ep = getNumberOfEnclosedPoints();
			
			if ((new Rectangle(Math.min(rubStartX,rubLastX),
							   Math.min(rubStartY,rubLastY),
							   Math.abs(rubLastX-rubStartX),
							   Math.abs(rubLastY-rubStartY)).contains(x,y))){
					//stt.setLocation(x+80,y+80);
					//stt.addLine("Number of Objects: " + ep[0]);
					//stt.addLine("Uniquely plotted Objects" + ep[1]);
					//stt.show(true);
			}
			//else stt.show(false);
		}
	}
	
	public void resetAllObjects(){
	    allObjects.clear();
	}
}
