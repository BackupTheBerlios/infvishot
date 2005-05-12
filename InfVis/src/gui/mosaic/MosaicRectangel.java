package gui.mosaic;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.*;

public class MosaicRectangel{
	private Rectangle rect;
	private String id1, id2;
	private double width, height;
	private int intWidth, intHeight, areaWidth, areaHeight, id1count, id2count, totalId1Count, totalId2Count;
	private boolean selected=false;
	private Polygon Marker;
	public boolean colDragged;
	
	public MosaicRectangel(double w, double h, String id1id2){
		id1 = id1id2.substring(1, id1id2.lastIndexOf('&'));
		id2 = id1id2.substring((id1id2.lastIndexOf('&')+1));
		//System.out.println("id1= "+id1+"   "+"id2= "+id2);
		
		width = w;
		height = h;
	}
	
/////////////////////////////////////////////////////////////////////
	//specify coordinates of rectangle by defining point in left top corner of rectangle
	public void setPoint(Point p){
		
	}
	
/////////////////////////////////////////////////////////////////////
	// save ID count
	public void setID1count(int cnt){
		id1count = cnt;
		//System.out.println("ID1 cnt ="+cnt);
	}
	
	public void setID2count(int cnt){
		id2count = cnt;
		//System.out.println("ID2 cnt ="+cnt);
	}
/////////////////////////////////////////////////////////////////////	
	// return ID count
	public int getID1count(){
		return id1count;
	}
	
	public int getID2count(){
		return id2count;
	}
/////////////////////////////////////////////////////////////////////	
	// save the total counts
	// set in ProcessData in void buildRectangle
	public void setTotalID1count(int cnt){
		totalId1Count = cnt;
	}
	
	public void setTotalId2count(int cnt){
		totalId2Count = cnt;
	}
/////////////////////////////////////////////////////////////////////	
	// return ID count
	public int getTotalID1count(){
		return totalId1Count;
	}
	
	public int getTotalID2count(){
		return totalId2Count;
	}
/////////////////////////////////////////////////////////////////////	
	//specify coordinates of rectangle by defining x and y coordinates in left top corner of rectangle
	public void setCoordinates(int x, int y){
		//calcInts();
		
		//System.out.println();
		//System.out.println("RectWidth="+intWidth+"  "+"RectHeight="+intHeight+"  x="+x+"  y="+y);
		
		rect = new Rectangle(x, y, intWidth, intHeight);
	}
/////////////////////////////////////////////////////////////////////
	// calculate intWidth and intHeight
	public void calcInts(){
		double tempW = width*areaWidth;
		double tempH = height*areaHeight;
		
		Double h = new Double(tempH);
		Double w = new Double(tempW);
		
		intWidth = w.intValue();
		intHeight = h.intValue();
	}
/////////////////////////////////////////////////////////////////////
	// set intWidth and intHeight
	public void setIntWidth(int we){
		intWidth = we;
	}
	
	public void setIntHeight(int he){
		intHeight = he;
	}
/////////////////////////////////////////////////////////////////////	
	// set width and height
	public void setHeight(double he){
		height = he;
	}
	
	public void setWidth(double we){
		width = we;
	}
/////////////////////////////////////////////////////////////////////	
	//get coodinates
	public Point getCoodinates(){
		return rect.getLocation();
	}
	
/////////////////////////////////////////////////////////////////////
	//return Identifiers
	public String getIdentifier1(){
		return id1;
	}
	
	public String getIdentifier2(){
		return id2;
	}
/////////////////////////////////////////////////////////////////////
	//return width and height
	public double getW(){
		return rect.getWidth();
	}
	
	public double getH(){
		return rect.getHeight();
	}
/////////////////////////////////////////////////////////////////////
	// return int width and height
	public int getIntW(){
		return intWidth;
	}
	
	public int getIntH(){
		return intHeight;
	}
/////////////////////////////////////////////////////////////////////
	//set the size of the area where the rectangles are displayed
	public void setDrawAreaSize(int ww, int hh){
		areaWidth = ww;
		areaHeight = hh;
		
		/**width = width*areaWidth;
		height = height*areaHeight;
		
		Double h = new Double(height);
		Double w = new Double(width);
		
		intWidth = w.intValue();
		intHeight = h.intValue();
		
		rect.resize(intWidth, intHeight);**/
	}
/////////////////////////////////////////////////////////////////////
	// return Rectangle
	public Rectangle returnRect(){
		return rect;
	}
/////////////////////////////////////////////////////////////////////
	//set boolean to specify whether a rec is marked or not
	public void setFlag(boolean b){
		selected = b;
	}

	//return actual status
	public boolean getFlag(){
		return selected;
	}
/////////////////////////////////////////////////////////////////////
	// create the Marker Polygon
	public void setMarker(int[] x, int[] y, int nr){
		Marker = new Polygon(x, y, nr);
	}
	
	// return the Marker Polygon
	public Polygon getMarker(){
		return Marker;
	}
	
/////////////////////////////////////////////////////////////////////
	//recalculate dimensions of Rectangle
	//public void setSize(int w, int h){
	//	rect.resize(w, h);
	//}
}