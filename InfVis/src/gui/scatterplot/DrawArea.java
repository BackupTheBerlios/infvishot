/**
 * Created on 25.03.2005
 * Project: InfVis
 * @author: tobias
 */

package gui.scatterplot;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class DrawArea extends JPanel {
	
	protected Dimension size;
	protected int width, height;
	
	//protected Image img;
	protected Graphics gr;
	
	public DrawArea(int w, int h){
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
	
	public void setPrefSize(Dimension _d){
	    size = _d;
	    width = (int)_d.getWidth();
	    height = (int)_d.getHeight();
	}
}
