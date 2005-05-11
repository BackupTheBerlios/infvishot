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


public class MosaicArea extends DrawMosaic implements MouseListener, MouseMotionListener{
	final static Color recCol = Color.BLUE,
					   recDarkCol = Color.BLUE.darker(),	
					   filledRecCol = Color.RED,
					   filledRecdarkCol = Color.RED.darker(),
					   fontCol = Color.BLACK,
					   markerCol = Color.BLACK,
					   backGround = Color.DARK_GRAY;
	public final static Font DEFAULT_FONT = new Font("TimesRoman",Font.PLAIN,9),
	 								 tr10 = new Font("Arial",Font.PLAIN,10),
									 tr12 = new Font("Arial",Font.BOLD,12);
	public final static int[] margins = new int[4];
	private MosaicRectangel rec;
	private Vector colNames, rowNames, Rectangles, hash, fontPos, selectedRects, sortSelRects;
	private int actualXpos, actualYpos, absWidth, absHeight, gapX, gapY, 
				rubXStart, rubXEnd, rubYStart, rubYEnd, initXRubStart, initYRubStart;
	private MosaicRectangel temp, temp2;
	private double dist;
	private MosaicRectangel[] ArrayRectangles, selARecs;
	//private Polygon[] markers;
	private boolean rectsFilled = false, rubInit = false, ControlPressed = false, ShiftPressed = false;
	public boolean markerSwitch = false;
	private AffineTransform restore;
	private Rectangle rubRect;
	public String[][] Query;
	
	public MosaicArea(int width, int height, Vector h, double distance){
		super(width, height);
		absWidth = width;
		absHeight = height;
		dist = distance;
		
		hash = new Vector();
		hash = (Vector)h.clone();
		//hash = h;
		
		addMouseListener(this);
	    addMouseMotionListener(this);
		
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
		
		//System.out.println("mosai1.getID2count()="+mosai1.getID2count());
		//System.out.println("mosai2.getID2count()="+mosai2.getID2count());
		//System.out.println(hehe);
		
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
				//System.out.println(ArrayRectangles[f].getIdentifier1()+"    "+ArrayRectangles[f].getIdentifier1());
				cnt++;
			}
		}	
		//System.out.println("COLCOUNT="+cnt);
		return cnt;
	}
/////////////////////////////////////////////////////////////////////////////////////////	
	// calculate markers for each rectangle
	private void calcMarkers(){
		Vector tempi = new Vector();
		//markers = new Polygon[ArrayRectangles.length];
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
					
					//markers[cnt] = new Polygon(x, y, 3);
					ArrayRectangles[i].setMarker(x, y, 3);
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
		
		//System.out.println(absWidth+"  "+colNames.size()+"  "+dist);
		//System.out.println("gapX="+gapX+"  "+"gapY="+gapY);
		
	
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
			
			//if rec intersects RubRect paint recs darker
			if(ArrayRectangles[i].getFlag())g2.setPaint(recDarkCol);
			else g2.setPaint(recCol);
			
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
		
		/**
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
						mosi1.setFlag(mosi2.getFlag());
						
						if(mosi1.getFlag()) g2.setPaint(filledRecdarkCol);
						
						g2.fill(mosi1.returnRect());						
						sortSelRects.add(mosi1);
					}
				}
			}
		}
		**/
		
		//paint additional Recs inside of old Rects --> fill old Rects
		if(rectsFilled){
			g2.setPaint(filledRecCol);
			sortSelRects = new Vector();
			for(int j=0; j<selectedRects.size(); j++){
				for(int i=0; i<ArrayRectangles.length; i++){
					MosaicRectangel mosi1 = (MosaicRectangel)selectedRects.get(j);
					//MosaicRectangel mosi2 = (MosaicRectangel)Rectangles.get(i);
					
					if(mosi1.getIdentifier1().equals(ArrayRectangles[i].getIdentifier1()) &&
					   mosi1.getIdentifier2().equals(ArrayRectangles[i].getIdentifier2())){
						
						mosi1 = calcSrects(mosi1, ArrayRectangles[i]);
						//mosi1.setFlag(ArrayRectangles[i].getFlag());
						
						if(ArrayRectangles[i].getFlag()) g2.setPaint(filledRecdarkCol);
						else g2.setPaint(filledRecCol);
						
						g2.fill(mosi1.returnRect());						
						sortSelRects.add(mosi1);
					}
				}
			}
		}
		
	/**	if(markerSwitch){
			calcMarkers();
			for(int i=0; i<markers.length; i++){
				g2.setColor(markerCol);
				g2.fill(markers[i]);
			}
		}**/
		
		if(markerSwitch){
			calcMarkers();
			for(int i=0; i<ArrayRectangles.length; i++){
				g2.setColor(markerCol);
				g2.fill(ArrayRectangles[i].getMarker());
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
		// draw RubRect
		if(rubInit && rubRect!=null){
			g2.draw(rubRect);
			//rubRect.reshape(0,0,0,0);
			//g2.clearRect(rubRect.x, rubRect.y, rubRect.width, rubRect.height);
		}
		
		/**
		String shit = "Fuck You!";
		//g2.translate(-absWidth, -absHeight);
		//g2.translate(-200, -200);
		g2.rotate(Math.toRadians(45));
		//g2.translate(absWidth, absHeight);
		//g2.translate(200, 200);
		g2.drawString(shit, 0, 0);
		g2.fillRect(0, 0, 60, 40);**/
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
		
		//System.out.println("*****************************************");
		//System.out.println("*****************************************");
		//System.out.println("*****************************************");
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

/* (non-Javadoc)
 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
 */
public void mouseClicked(MouseEvent evt) {
	if(evt.getButton()==2){
		if(markerSwitch == false) markerSwitch = true;
		else markerSwitch = false;
		this.repaint();
		/**JPopupMenu popMenu = new JPopupMenu();
		JMenuItem markers = new JMenuItem("show  markers");
		popMenu.add(markers);
		popMenu.setLocation(evt.getX(), evt.getY());
		popMenu.show();**/
	}
	
	
	/**
	int x = evt.getX();
	System.out.println(x);
	if(markerSwitch == false) markerSwitch = true;
	else markerSwitch = false;
	this.repaint();
	**/
}

/* (non-Javadoc)
 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
 */
public void mouseEntered(MouseEvent evt) {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
 */
public void mouseExited(MouseEvent evt) {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
 */
public void mousePressed(MouseEvent evt) {
	//System.out.println("MODIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII="+evt.getModifiers());
	if(evt.getModifiers()==18){
		ControlPressed=true;
	}
	if(evt.getModifiers()==17){
		ShiftPressed=true;
	}
	initXRubStart = evt.getX();
	initYRubStart = evt.getY();
	rubXStart = initXRubStart;
	rubYStart = initYRubStart;
	rubInit = true;
}

/* (non-Javadoc)
 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
 */
public void mouseReleased(MouseEvent evt) {
	if(rubRect!=null){
		// reset all flags for new selection if neither Control nor Shift is pressed
		if(!ControlPressed && !ShiftPressed){
			for(int i=0; i<ArrayRectangles.length; i++){
				ArrayRectangles[i].setFlag(false);
			}
		}
		int cnt=0;		
		// which recs does the rubRect intersect
		for(int i=0; i<ArrayRectangles.length; i++){
			if(initXRubStart!=evt.getX() && initYRubStart!=evt.getY()){
				if(ArrayRectangles[i].returnRect().intersects(rubRect)){
					//if control is pressed invert the flag
					if(ControlPressed){
						boolean boh = ArrayRectangles[i].getFlag();
						ArrayRectangles[i].setFlag(!boh);
					}
					// if nothing shift is pressed set the flag true 
					else ArrayRectangles[i].setFlag(true);
					cnt++;
					//System.out.println("种种种种种种种种种种种种种种种种种种种种种种种种种种种种=  "+cnt);
				}
			}
			// if rubRect is null get the actual Mouse Position and check if one Rec conains this point
			else{
				//System.out.println(evt.getX()+ evt.getY());
				if(ArrayRectangles[i].returnRect().contains(evt.getX(), evt.getY())){
					if(ControlPressed){
						boolean boh = ArrayRectangles[i].getFlag();
						ArrayRectangles[i].setFlag(!boh);
					}
					else ArrayRectangles[i].setFlag(true);
				}
			}
		}
		// create Array with length of counted data +1 for column names
		//Query = new String[2][cnt+1];
		Query = new String[2][ArrayRectangles.length+1];
		// first row is empty for Names --> filled in MainWindow, Mouse released
		int cnt2 = 1;
		for(int i=0; i<ArrayRectangles.length; i++){
			if(ArrayRectangles[i].getFlag()){
				Query[0][cnt2] = ArrayRectangles[i].getIdentifier1();
				Query[1][cnt2] = ArrayRectangles[i].getIdentifier2();
				cnt2++;
			}
		}
		
		/**for(int i=0; i<Query[0].length; i++){
			if(Query[0][i] != null && Query[1][i] != null){
				System.out.println("Query["+i+"][0]= "+Query[0][i]+"Query["+i+"][1] ="+Query[1][i]);
				//System.out.println(Query.length);
				//System.out.println(Query[0].length);
			//	System.out.println(Query[0][10]);
			}
		}**/
		ControlPressed = false;
		ShiftPressed = false;
		rubInit = false;
		//this.repaint();
		//this.updateUI();
	}
	//this.repaint();
	//this.updateUI();
}

//public String[][] getQuery(){
//	return Query;
//}

/* (non-Javadoc)
 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
 */
public void mouseDragged(MouseEvent evt) {
	/**if(evt.getButton()==1){**/
		if(evt.getX() < initXRubStart){
			rubXEnd = initXRubStart;
			rubXStart = evt.getX();
		}
		if(evt.getY() < initYRubStart){
			rubYEnd = initYRubStart;
			rubYStart = evt.getY();
		}
		if(evt.getX() > initXRubStart){
			rubXEnd = evt.getX();
			rubXStart = initXRubStart;
		}
		if(evt.getY() > initYRubStart){
			rubYEnd = evt.getY();
			rubYStart = initYRubStart;
		}
		//this.repaint();
		//System.out.println("YESYESYESYESYESYESYESYESYESYESYESYESYESYESYESYESYESYESYESYESYES");
		Graphics g = getGraphics();
		//this.repaint();
		//this.updateUI();
		//drawRubRec(g);
		
	//}
}

public void drawRubRec(Graphics g){
	int www = rubXEnd - rubXStart;
	int hhh = rubYEnd - rubYStart;
	rubRect = new Rectangle (rubXStart, rubYStart, www, hhh);
	//g.drawRect(rubXStart, rubYStart, www, hhh);
	//System.out.println("HAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAH");
}

/* (non-Javadoc)
 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
 */
public void mouseMoved(MouseEvent evt) {
	// TODO Auto-generated method stub
	
}


}