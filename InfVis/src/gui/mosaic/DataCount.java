package gui.mosaic;

import java.util.Vector;


public class DataCount{
	private String[] col1;
	private String[] col2;
	private Vector elements = new Vector();
	private Vector elementCount = new Vector();
	private int catCnt;
	private int id1cnt=1, id2cnt=1;
	
	
	public DataCount(String[] colone, String[] coltwo, int c){
		col1 = colone;
		col2 = coltwo;
		
		catCnt = c;
	}
	
///////////////////////////////////////////////////////////////	
	//	 save how often spefific element occurs
	public void countElements(){
		for(int i=0; i<col1.length; i++){
			if(id1cnt > catCnt || id2cnt > catCnt*id1cnt) {
				System.out.println("too many categories found --> stopped counting");
				break;					// if too many categories are found stop count
			}
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
			if(el.charAt(0)=='&') id2cnt++;		// increase id2cnt if new id2 element is found
			else id1cnt++;						// increase id2cnt if new id1 element is found
			
		//	System.out.println("id1cnt= "+id1cnt+"   id2cnt= "+id2cnt+"   catCnt= "+catCnt+"   catCnt*id1cnt= "+(catCnt*id1cnt));
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
