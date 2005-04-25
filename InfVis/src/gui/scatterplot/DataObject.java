/**
 * Created on 25.03.2005
 * Project: InfVis
 * @author: tobias
 */

package gui.scatterplot;

public class DataObject {
	
	private double[] x,sortx;
	private double[] summary=new double[6];
	private boolean isSorted=false;
	public double[] five=new double[5];
	private int n;
	private double mean,variance,stdev;
	private double median,min,Q1,Q3,max;

	public DataObject(double[] data) {
		x=(double[])data.clone();
		n=x.length;
	}
	
	public double max() {
	    if(!isSorted) sortx=sort();
	    return(sortx[n-1]);
	}

	public double min() {
	    if(!isSorted) sortx=sort();
	    return(sortx[0]);
	}
		  
	public double[] sort() {
	    sortx=(double[])x.clone();
	    int incr=(int)(n*.5);
	    while (incr >= 1) {
	      for (int i=incr;i<n;i++) {
	        double temp=sortx[i];
	        int j=i;
	        while (j>=incr && temp<sortx[j-incr]) {
	          sortx[j]=sortx[j-incr];
	          j-=incr;
	        }
	        sortx[j]=temp;
	      }
	      incr/=2;
	    }
	    isSorted=true;
	    return(sortx);
	}

	public double[] getData() {
	    return(x);
	}

	public int size() {
	    return (n);
	}

	public double elementAt(int index) {
	    double element=0;
	    try {
	      element=x[index];
	    }
	    catch(ArrayIndexOutOfBoundsException e) {
	      System.out.println("Index "+ index +" does not exist in data.");
	    }
	    return(element);
	}
	
	public int indexOf(double element) {
	   int index=-1;
	   for(int i=0;i<n;i++)
	     if(Math.abs(x[i]-element)<1e-6) index=i;
	   return(index);
	}

}
