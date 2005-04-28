package gui.mosaic;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

//TODO: Adjust round methods in MosaicArea (paint --> gapX&gapY) and MosaicRectanel setCoordinates


public class MosaicArea extends DrawMosaic{
	final static Color recCol = Color.BLUE,
					   filledRecCol = Color.RED,
					   fontCol = Color.BLACK,
					   backGround = Color.DARK_GRAY;
	public final static Font DEFAULT_FONT = new Font("TimesRoman",Font.PLAIN,9),
	 								 tr10 = new Font("Arial",Font.PLAIN,10),
									 tr12 = new Font("TimesRoman",Font.BOLD,12);
	public final static int[] margins = new int[4];
	private MosaicRectangel rec;
	private Vector colNames, rowNames, Rectangles, hash, fontPos, selectedRects, sortSelRects;
	private int actualXpos, actualYpos, absWidth, absHeight, gapX, gapY;
	private MosaicRectangel temp, temp2;
	private double dist;
	private boolean rectsFilled = false;
	
	public MosaicArea(int width, int height, Vector h, double distance){
		super(width, height);
		absWidth = width;
		absHeight = height;
		dist = distance;
		
		hash = new Vector();
		hash = (Vector)h.clone();
		//hash = h;
		
		margins[0]=15;	//Reihenfolge: links, unten, rechts, oben
		margins[1]=20;
		margins[2]=5;
		margins[3]=5;
		
		//super.setBackground(backGround);
		
		sortRects();
	}

/////////////////////////////////////////////////////////////////////////////////////////	
	//sort rectangles for output
	public void sortRects(){
		//System.out.println("hallo");
		
		colNames = new Vector();
		rowNames = new Vector();
		Rectangles = new Vector();
		
		// get all col and row names
		for(int i=0; i<hash.size(); i++){
			temp = (MosaicRectangel)hash.get(i);
			
			if(!colNames.contains(temp.getIdentifier1()))
				colNames.add(0, temp.getIdentifier1());
			if(!rowNames.contains(temp.getIdentifier2()))
				rowNames.add(0, temp.getIdentifier2());
			
			
			/**System.out.println("///////////////////////////////////////////////////////");
			System.out.println(hash.contains(temp.getIdentifier1()));
			System.out.println("ID1="+temp.getIdentifier1()+"   "+"ID2="+temp.getIdentifier2());
			System.out.println("colNames="+colNames+"   "+"rowNames="+rowNames);**/
		}
		
		
		String id1;
		String id2;
		
		//sort rectangles in the same order as col and rowNames
		for(int i=0; i<colNames.size(); i++){
			for(int j=0; j<rowNames.size(); j++){
				for(int y=0; y<hash.size(); y++){
					temp = (MosaicRectangel)hash.get(y);
					
					id1 = temp.getIdentifier1();
					id2 = temp.getIdentifier2();
					
					if(id1.equals((String)colNames.elementAt(i)) && id2.equals((String)rowNames.elementAt(j))){
						Rectangles.add(0, temp);
						//hash.remove(y);
						
						//System.out.println("i="+i+"  "+"j="+j+"  "+"y="+y);
						//System.out.println(Rectangles);
					}
					//hash.remove(y);
				}
			}
		}
		
	}

/////////////////////////////////////////////////////////////////////////////////////////	
	private MosaicRectangel calcSrects(MosaicRectangel mosai1, MosaicRectangel mosai2){
		int xxx;
		int yyy;
		double hehe;
		//mosi1.calcInts();
		
		//mosi1.setDrawAreaSize(mosi2.getIntW(), mosi2.getIntH());
		mosai1.setIntWidth(mosai2.getIntW()-6);
		hehe = mosai2.getID2count()/mosai1.getID2count();
		hehe = mosai2.getIntH()*hehe;
		Double tempH = new Double(hehe);
		mosai1.setIntHeight(tempH.intValue());
		
		Point p = mosai2.getCoodinates();
		xxx = p.x+3;
		yyy = p.y + mosai2.getIntH()- mosai1.getIntH()-3;
		mosai1.setCoordinates(xxx, yyy);
		
		return mosai1;
	}

/////////////////////////////////////////////////////////////////////////////////////////	
	public void paint(Graphics g){
		temp2 = new MosaicRectangel(5.1,5.1,"h&h");
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setPaint(backGround);
		//g2.fill(new Rectangle(0, 0, absWidth, absHeight));
		
		
		actualXpos = margins[0];
		actualYpos = margins[3];
		gapX = (int) ((absWidth-margins[0]-margins[2])/((colNames.size()-1)/dist))-1;
		gapY = (int) ((absHeight-margins[1]-margins[3])/((rowNames.size()-1)/dist))-1;
		
		fontPos = new Vector();
		
		System.out.println(absWidth+"  "+colNames.size()+"  "+dist);
		System.out.println("gapX="+gapX+"  "+"gapY="+gapY);
		
	
		// draw rectangles in right order and on the right position
		for(int i=0; i<Rectangles.size(); i++){
			temp = (MosaicRectangel)Rectangles.get(i);
			temp.setDrawAreaSize(absWidth-margins[0]-margins[2], absHeight-margins[1]-margins[3]);
			temp.calcInts();
			temp.setCoordinates(actualXpos, actualYpos);
			Rectangles.set(i, temp);		// save the modified rectangle in Vector
			
			g2.setPaint(recCol);
			g2.fill(temp.returnRect());
			
			//calculate x and y position of text
			if(actualXpos == margins[0]){
				int uepsilon = (int)actualYpos+(temp.getIntH()/2);
				setFontPos(temp.getIdentifier2(),0, uepsilon);
			}
			if(actualYpos == margins[3]){
				int ix = (int)actualXpos+(temp.getIntW()/2);
				setFontPos(temp.getIdentifier1(), ix-15, absHeight-10);
			}
			
			
			// calcualte new x and y position
			if(i<Rectangles.size()-1){
				temp2 = (MosaicRectangel)Rectangles.get(i+1);
				if(!temp2.getIdentifier1().equals(temp.getIdentifier1())){
					actualYpos=margins[3];
					actualXpos = actualXpos + temp.getIntW() + gapX;
				}
				else
					actualYpos = actualYpos + temp.getIntH() + gapY;
			}
			else
				actualYpos = actualYpos + temp.getIntH() + gapY;
		
				
			/**System.out.println("------------------------------------------");
			System.out.println(temp2.getIdentifier1()+"   "+temp.getIdentifier1());
			System.out.println("X="+actualXpos+"  Y="+actualYpos);**/
		}
		
		g2.setPaint(fontCol);
		g2.setFont(tr12);
		//print text
		for(int i=0; i<fontPos.size(); i++){
			Vector fp = new Vector();
			fp = (Vector)fontPos.get(i);
			
			Integer xx = (Integer)fp.get(1);
			Integer yy = (Integer)fp.get(2);
			
			g2.drawString((String)fp.get(0), xx.intValue(), yy.intValue());
		}
		
		//paint additional Recs inside of old Rects --> fill old Rects
		if(rectsFilled){
			g2.setPaint(filledRecCol);
			sortSelRects = new Vector();
			for(int j=0; j<selectedRects.size(); j++){
				for(int i=0; i<Rectangles.size(); i++){
					MosaicRectangel mosi1 = (MosaicRectangel)selectedRects.get(j);
					MosaicRectangel mosi2 = (MosaicRectangel)Rectangles.get(i);
					
					if(mosi1.getIdentifier1().equals(mosi2.getIdentifier1()) &&
					   mosi1.getIdentifier2().equals(mosi2.getIdentifier2())){
						
						mosi1 = calcSrects(mosi1, mosi2);
						
						g2.fill(mosi1.returnRect());						
						sortSelRects.add(mosi1);
					}
				}
			}
		}
		
	}
	
/////////////////////////////////////////////////////////////////////////////////////////	
	//save position for each String
	public void setFontPos(String text, int x, int y){
		Vector temp = new Vector();
		Integer X = new Integer(x);
		Integer Y = new Integer(y);
		temp.add(text);
		temp.add(X);
		temp.add(Y);
		
		fontPos.add(temp);
	}

/////////////////////////////////////////////////////////////////////////////////////////	
	public void refreshSize(){
		int [] sss = new int[2];
		sss = getWinSize();
		absWidth = sss[0];
		absHeight = sss[1];
		
		Rectangles.clear();
		colNames.clear();
		rowNames.clear();
		fontPos.clear();
		
		sortRects();
		
		System.out.println("*****************************************");
		System.out.println("*****************************************");
		System.out.println("*****************************************");
		repaint();
		//resetGr();
	}

/////////////////////////////////////////////////////////////////////////////////////////	
	//decide if rects should be brushed with data from Scatterplot or not
	public void setRectsFilled(boolean boo){
		rectsFilled = boo;
		this.repaint();
	}

/////////////////////////////////////////////////////////////////////////////////////////	
	//now fill them
	public void fill(Vector vec){
		selectedRects = new Vector();
		selectedRects = vec;
		//sortSelRects();
	}
}