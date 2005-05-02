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
					   markerCol = Color.BLACK,
					   backGround = Color.DARK_GRAY;
	public final static Font DEFAULT_FONT = new Font("TimesRoman",Font.PLAIN,9),
	 								 tr10 = new Font("Arial",Font.PLAIN,10),
									 tr12 = new Font("Arial",Font.BOLD,12);
	public final static int[] margins = new int[4];
	private MosaicRectangel rec;
	private Vector colNames, rowNames, Rectangles, hash, fontPos, selectedRects, sortSelRects;
	private int actualXpos, actualYpos, absWidth, absHeight, gapX, gapY;
	private MosaicRectangel temp, temp2;
	private double dist;
	private boolean rectsFilled = false;
	private MosaicRectangel[] ArrayRectangles;
	private Polygon[] markers;
	public boolean markerSwitch = false;
	private AffineTransform restore;
	
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
		ArrayRectangles = new MosaicRectangel[Rectangles.size()];
		Rectangles.copyInto(ArrayRectangles);
	}
/////////////////////////////////////////////////////////////////////////////////////////	
	private MosaicRectangel calcSrects(MosaicRectangel mosai1, MosaicRectangel mosai2){
		int xxx;
		int yyy;
		double hehe;
		//mosi1.calcInts();
		
		//mosi1.setDrawAreaSize(mosi2.getIntW(), mosi2.getIntH());
		mosai1.setIntWidth(mosai2.getIntW()-6);
		hehe =(double) mosai1.getID2count()/mosai2.getID2count();
		
		System.out.println("mosai1.getID2count()="+mosai1.getID2count());
		System.out.println("mosai2.getID2count()="+mosai2.getID2count());
		System.out.println(hehe);
		
		hehe = (mosai2.getIntH()*hehe)-6;
		Double tempH = new Double(hehe);
		mosai1.setIntHeight(tempH.intValue());
		
		Point p = mosai2.getCoodinates();
		xxx = p.x+3;
		yyy = p.y + mosai2.getIntH()- mosai1.getIntH()-3;
		mosai1.setCoordinates(xxx, yyy);
		
		return mosai1;
	}
/////////////////////////////////////////////////////////////////////////////////////////	
	//check if first column contains every possible row category
	// if not --> print the row names in fixed Positions
	private boolean fontPositionCheck(){
		boolean fontCheck = false;
		for(int k=0; k<rowNames.size(); k++){
			if(!ArrayRectangles[0].getIdentifier1().equals(ArrayRectangles[k].getIdentifier1()))
				fontCheck = true;
		}	
		//System.out.println(fontCheck);
		return fontCheck;
	}
/////////////////////////////////////////////////////////////////////////////////////////	
	// calcualte right font Positions if fontPositionCheck returns true
	private void calcFontPos(){
		int t =(int) absHeight/rowNames.size();
		int yRun = absHeight - margins[1]-(int)t/2 +10;
		for(int i=0; i<rowNames.size(); i++){
			setFontPos((String)rowNames.get(i), 0, yRun, true);
			yRun = yRun - t;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
	// count numer of Recs in the same col
	private int getColCount(int f){
		int cnt = 0;
		for(int k=1; k<Rectangles.size()-f; k++){
			if(ArrayRectangles[f].getIdentifier1().equals(ArrayRectangles[f+k].getIdentifier1())){
				System.out.println(ArrayRectangles[f].getIdentifier1()+"    "+ArrayRectangles[f].getIdentifier1());
				cnt++;
			}
		}	
		System.out.println("COLCOUNT="+cnt);
		return cnt;
	}
/////////////////////////////////////////////////////////////////////////////////////////	
	// calculate markers for each rectangle
	private void calcMarkers(){
		Vector tempi = new Vector();
		markers = new Polygon[Rectangles.size()];
		int cnt=0;
		
		for(int j=0; j<fontPos.size(); j++){
			tempi = (Vector)fontPos.get(j);
			for(int i=0; i<ArrayRectangles.length; i++){
				String s = (String)tempi.get(0);
				if(s.equals(ArrayRectangles[i].getIdentifier2())){
					Integer ttt;
					int[] x = new int[3];
					int[] y = new int[3];
					x[0] = ArrayRectangles[i].getCoodinates().x;
					ttt = (Integer)tempi.get(2);
					y[0] = ttt.intValue();
					x[1] = x[0]+(int)absWidth/75;
					y[1] = y[0]+(int)absHeight/100;
					x[2] = x[1];
					y[2] = y[0]-(int)absHeight/100;
					
					markers[cnt] = new Polygon(x, y, 3);
					cnt++;
				}
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////	
/**	private AffineTransform getAffineTransform(int ix, int yps){
		AffineTransform affi = new AffineTransform();
		//affi.translate(-ix, -yps);
		affi.rotate(Math.toRadians(270));
		//affi.translate(ix, yps);
		
		return affi;
	} **/
	private AffineTransform getAffineTransform(int ix, int yps){
		AffineTransform affi = new AffineTransform();
		affi.rotate(Math.toRadians(270));
		
		AffineTransform affi1 = new AffineTransform();
		affi1.concatenate(affi);
		affi1.translate(-ix, -yps);
		
		return affi1;
	}
/////////////////////////////////////////////////////////////////////////////////////////	
	/**private boolean isRowName(String rowN){
		boolean uh = false;
		for(int i=0; i<rowNames.size(); i++){
			if(rowN.equals((String)rowNames.get(i)))
				uh = true;
		}
		return uh;
	}**/
/////////////////////////////////////////////////////////////////////////////////////////	
	public void paint(Graphics g){
		temp2 = new MosaicRectangel(5.1,5.1,"h&h");
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setPaint(backGround);
		//g2.fill(new Rectangle(0, 0, absWidth, absHeight));
		
		
		actualXpos = margins[0];
		actualYpos = margins[3];
		gapX = (int) ((absWidth-margins[0]-margins[2])/((colNames.size()-1)/dist))-1;
		gapY = (int) ((absHeight-margins[1]-margins[3])/(getColCount(0)/dist))-1;
		
		fontPos = new Vector();
		
		System.out.println(absWidth+"  "+colNames.size()+"  "+dist);
		System.out.println("gapX="+gapX+"  "+"gapY="+gapY);
		
	
		// draw rectangles in right order and on the right position
		for(int i=0; i<Rectangles.size(); i++){
			ArrayRectangles[i].setDrawAreaSize(absWidth-margins[0]-margins[2], absHeight-margins[1]-margins[3]);
			ArrayRectangles[i].calcInts();
			
			// check if this Rec is the only one in this col
			// if yes, then increase the Height of the Rec
			if(getColCount(i)==0 && actualYpos==margins[3]){
				ArrayRectangles[i].setIntHeight(absHeight-margins[1]-margins[3]-5);
			}
			ArrayRectangles[i].setCoordinates(actualXpos, actualYpos);
			Rectangles.set(i, ArrayRectangles[i]);		// save the modified rectangle in Vector
			
			g2.setPaint(recCol);
			g2.fill(ArrayRectangles[i].returnRect());
			
			//calculate x and y position of text
			if(actualXpos == margins[0]){
				int uepsilon = (int)actualYpos+(ArrayRectangles[i].getIntH()/2);
				if(actualYpos == margins[3] && fontPositionCheck()){
					calcFontPos();
				}
				else
					if(!fontPositionCheck())
						setFontPos(ArrayRectangles[i].getIdentifier2(),0, uepsilon, true);
			}
			//else{
				if(actualYpos == margins[3]){
					int ix = (int)actualXpos+(ArrayRectangles[i].getIntW()/2);
					
					//calc new YGap for each new col
					if(getColCount(i)!=0){
						gapY = (int) ((absHeight-margins[1]-margins[3])/(getColCount(i)/dist))-1;
					}
					setFontPos(ArrayRectangles[i].getIdentifier1(), ix-15, absHeight-10, false);
				}
			//}
			
			// calcualte new x and y position
			if(i<Rectangles.size()-1){
				//temp2 = (MosaicRectangel)Rectangles.get(i+1);
				if(!ArrayRectangles[i+1].getIdentifier1().equals(ArrayRectangles[i].getIdentifier1())){
					actualYpos=margins[3];
					actualXpos = actualXpos + ArrayRectangles[i].getIntW() + gapX;
				}
				else
					actualYpos = actualYpos + ArrayRectangles[i].getIntH() + gapY;
			}
			else
				actualYpos = actualYpos + ArrayRectangles[i].getIntH() + gapY;
		
				
			/**System.out.println("------------------------------------------");
			System.out.println(temp2.getIdentifier1()+"   "+temp.getIdentifier1());
			System.out.println("X="+actualXpos+"  Y="+actualYpos);**/
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
		
		if(markerSwitch){
			calcMarkers();
			for(int i=0; i<markers.length; i++){
				g2.setColor(markerCol);
				g2.fill(markers[i]);
			}
		}
		
		
		g2.setPaint(fontCol);
		g2.setFont(tr12);
		restore = g2.getTransform();
		//print text
		for(int i=0; i<fontPos.size(); i++){
			Vector fp = new Vector();
			fp = (Vector)fontPos.get(i);
			
			Integer xx = (Integer)fp.get(1);
			Integer yy = (Integer)fp.get(2);
			
			g2.drawString((String)fp.get(0), xx.intValue(), yy.intValue());
		}
		
		String shit = "Fuck You!";
		//g2.translate(-absWidth, -absHeight);
		//g2.translate(-200, -200);
		g2.rotate(Math.toRadians(45));
		//g2.translate(absWidth, absHeight);
		//g2.translate(200, 200);
		g2.drawString(shit, 0, 0);
		g2.fillRect(0, 0, 60, 40);
	}
/////////////////////////////////////////////////////////////////////////////////////////	
	//save position for each String
	public void setFontPos(String text, int x, int y, boolean bool){
		Vector temp = new Vector();
		Integer X = new Integer(x);
		Integer Y = new Integer(y);
		temp.add(text);
		temp.add(X);
		temp.add(Y);
		Boolean buhl = new Boolean(bool);
		temp.add(buhl);
		
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
		//fontPos.clear();
		if (fontPos != null) fontPos.clear();
		//fontCheck = false;
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
		//this.repaint();
	}
/////////////////////////////////////////////////////////////////////////////////////////	
	//now fill them
	public void fill(Vector vec){
		selectedRects = new Vector();
		selectedRects = vec;
		//sortSelRects();
	}
}
