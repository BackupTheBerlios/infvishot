package gui.mosaic;

import java.util.Vector;

public class DataObject{
	private String[] Names=null;
	private String[][] Data=null;
	private int rows;
	private int cols;
	private Vector[] elements=null;
	private Vector[] elementCount=null;
	
	public DataObject(String[] Nam, String[][] Dat){
		Names = Nam;
		Data = Dat;
		if (Data.length > 0){
		    rows=Data[0].length;
		}
		else {
		    rows = 0;
		}
		cols=Data.length;
	}
	
	//return whole row at specified position
	public Object[] getRow(int pos){
		String[] row = new String[cols];
		
		for(int i=0; i<cols; i++){
			row[i] = Data[i][pos];
		}
		return row;
	}
	
	//return whole col at specified position
	public Object[] getCol(int pos){
		String[] col = new String[rows];
		
		for(int i=0; i<rows; i++){
			col[i] = Data[pos][i];
		}
		return col;
	}
	
	//return number of cols of Data Object
	public int getColNum(){
		int x;
		x = cols;
		return x;
	}
	
	//return numer of rows
	public int getRowNum(){
		int x;
		x = rows;
		return x;
	}
	
	//return index of col
	public int getPos(String name){
		int pos;
		int i = 0;
		
		while(Names[i]!=name){
			i++;
		}
		return i;
	}
	
	// save how often spefific element occurs
	public void countElements(String el){
		int pos;
		pos = elements[0].indexOf(el);
		
		if(pos==-1){
			elements[0].add(el);
			Integer x = new Integer(1);
			elementCount[0].add(0,x);
		}
		else{
			Integer z = (Integer)elementCount[0].get(pos);
			int i = z.intValue()+1;
			Integer y = new Integer(i);
			
			elementCount[0].set(pos, y);
		}
	}
	
	//return one of the found elements
	public String getcountElement(int position){
		String eleme;
		eleme = (String)elements[0].get(position);
		
		return eleme;
	}
	
	//return count of one of the found elements
	public int getCount(int position){
		Integer x = (Integer)elementCount[0].get(position);
		int y = x.intValue();
		
		return y;
	}
	
	//return number of counted elements
	public int getCountNumber(){
		return elements.length;
	}
}
