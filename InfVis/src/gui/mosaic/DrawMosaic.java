package gui.mosaic;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class DrawMosaic extends JPanel {
	
	protected Dimension size;
	protected int width, height;
	
	//protected Image img;
	protected Graphics gr;
	
	public DrawMosaic(int w, int h){
		size = new Dimension(w,h);
		width = w;
		height = h;
		
		BufferedImage bi = new BufferedImage(getPreferredSize().width, getPreferredSize().height,BufferedImage.TYPE_INT_ARGB);
		gr  = bi.getGraphics();	
	}
	
	public void resetGr(){
	    BufferedImage bi = new BufferedImage(getPreferredSize().width, getPreferredSize().height,BufferedImage.TYPE_INT_ARGB);
		gr  = bi.getGraphics();
	}
	
	/* Canvas Functions */
	public Dimension getPreferredSize() {
	    return(size);
	}

	public Dimension getMinimumSize() {
	    return(size);
	}
	
	/**public void resetSize(){
		size.setSize(ww,hh);
		width = ww;
		height = hh;
		
		BufferedImage bi = new BufferedImage(getPreferredSize().width, getPreferredSize().height,BufferedImage.TYPE_INT_ARGB);
		gr  = bi.getGraphics();
	}**/
	public void setPrefSize(Dimension _d){
	    size = _d;
	    width = (int)_d.getWidth();
	    height = (int)_d.getHeight();
	}
	
	public int[] getWinSize(){
		int[] winSize = new int[2];
		winSize[0] = width;
		winSize[1] = height;
		
		return winSize;
	}
}
