package gui.mosaic;

import java.util.Vector;


public class ProcessData{
	private DataObject data;
	private int c1, c2;
	private String[] col1, col2;
	private Vector hash;
	private DataCount DCounter;
	private int width = 0;
	private int height = 0;
	public double dist = 0.1;   // distance between each Rectangle --> TODO: mit diesem Wert experimentieren
	//public int catCnt=10;


	
	public ProcessData(DataObject dat, String s1, String s2, int cate){
		data = dat;			
		c1 = data.getPos(s1);
		c2 = data.getPos(s2);
		
		DCounter = new DataCount((String[])data.getCol(c1)/**.clone()**/, (String[])data.getCol(c2)/**.clone()**/, cate);	
		//DCounter.catCnt = cate;
		DCounter.countElements();
		
		calculate();		
		hash = new Vector();
		
		for(int i=0; i<DCounter.getCountNumber(); i++){
			calculateDimensions(DCounter.getcountElement(i), DCounter.getCount(i));
		}
		
	}
	
	
////////////////////////////////////////////////////////////////////////////////	
//	calculate absolute width
	public void calculate(){
		String actualEl;
		
		for(int i=0; i<DCounter.getCountNumber(); i++){
			actualEl = (String)DCounter.getcountElement(i);
			
			if(actualEl.charAt(0)!= '&')
				width += DCounter.getCount(i);						
		}	
	}
	
////////////////////////////////////////////////////////////////////////////////	
	//calculate width and height for each rectangle
	public void calculateDimensions(String el, int hei){
		String IdElement;
		String temp;
		double w = 0;
		double h = hei;
		height = hei;
		boolean run = true;
		
		if(el.charAt(0) == '&'){
			IdElement = el;
			
			int i =1;
			while(run){
				temp = DCounter.getcountElement(i);
				
				if(temp.charAt(0) != '&' &&
				   temp.equals(el.substring(1, el.lastIndexOf('&')))) {		
					w = DCounter.getCount(i);
					height = DCounter.getCount(i);
					run = false;				
				}
				i++;
			}
			double tempw = w;
			double temph = h;
			
			w = (w/width);			
			w = w-(w*dist);
			h = (h/height);
			h = h-(h*dist);
			
			buildRectangle(w, h, IdElement, tempw, temph);
		}
	}
	
////////////////////////////////////////////////////////////////////////////////	
	// create rectangle and save in a Vector
	public void buildRectangle(double wid, double heigh, String key, double we, double ha){
		Double eidi1 = new Double(we);
		Double eidi2 = new Double(ha);
		
		MosaicRectangel rec = new MosaicRectangel(wid, heigh, key);
		rec.setID1count(eidi1.intValue());
		rec.setID2count(eidi2.intValue());
		rec.setTotalID1count(width);
		//System.out.println(key.substring(1));
		hash.add(rec);
	}

////////////////////////////////////////////////////////////////////////////////	
	// return Vector with rectangles
	public Vector getVector(){
		return hash;
	}
	
////////////////////////////////////////////////////////////////////////////////	
	// for IntegerValues
	public void findValue(){
		
	}
	
	
}