package gui.mosaic;

import java.util.Vector;


public class DataCount{
	private String[] col1;
	private String[] col2;
	private Vector elements = new Vector();
	private Vector elementCount = new Vector();
	
	
	public DataCount(String[] colone, String[] coltwo){
		col1 = colone;
		col2 = coltwo;
	}
	
///////////////////////////////////////////////////////////////	
	//	 save how often spefific element occurs
	public void countElements(){
		for(int i=0; i<col1.length; i++){
			if(col1[i]!=null && col2[i]!=null){
				counter(col1[i]);
				counter("&"+col1[i]+"&"+col2[i]);
			}
		}
	}
	
///////////////////////////////////////////////////////////////	
	// help Method to count elements
	private void counter(String el){
		int pos;
		
		pos = elements.indexOf(el);
		if(pos==-1){
			elements.add(0,el);
			Integer x = new Integer(1);
			elementCount.add(0,x);
		}
		else{
			Integer z = (Integer)elementCount.get(pos);
			int j = z.intValue()+1;
			Integer y = new Integer(j);
			
			elementCount.set(pos,y);
		}
	}
	
////////////////////////////////////////////////////////////////	
	//return one of the found elements
	public String getcountElement(int position){
		String eleme;
		eleme = (String)elements.get(position);
		
		return eleme;
	}
	
/////////////////////////////////////////////////////////////////	
	//return count of one of the found elements
	public int getCount(int position){
		Integer x = (Integer)elementCount.get(position);
		int y = x.intValue();
		
		return y;
	}
	
/////////////////////////////////////////////////////////////////	
	//return number of counted elements
	public int getCountNumber(){
		return elements.size();
	}
	
/////////////////////////////////////////////////////////////////	
	//return number of rectangles
	public int getRectNumber(){
		String actualEl;
		int cnt1 = 0;
		int cnt2 = 0;
		
		for (int i=0; i<elements.size(); i++){
			actualEl = (String)elements.get(i);
			
			if(actualEl.charAt(0) == '&')
				cnt2 += 1;
			else
				cnt1 +=1;
		}
		int x = cnt1*cnt2;
		return x;
	}

//////////////////////////////////////////////////////////////////
	
	
//////////////////////////////////////////////////////////////////
	//sort in alphabetic order
	public void sort(){
		
	}
}
