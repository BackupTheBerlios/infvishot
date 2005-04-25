/**
 * Created on 25.03.2005
 * Project: InfVis
 * @author: tobias
 */

package gui.scatterplot;

import java.awt.*;
import java.awt.event.*;

/*
 * Created on 20.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Tobias
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ScatterPoint extends Button implements ActionListener{
	private boolean isSelected;
	private double xValue, yValue;
	private String identifier;

	protected final static String POINT_SELECTED = "point_selected";
	
	public ScatterPoint(String id, double x, double y){
		xValue = x;
		yValue = y;
		identifier = id;
		isSelected = false;
		
		this.setActionCommand(POINT_SELECTED);
		this.addActionListener(this);
	}
	
	public void plot(Graphics g, double bx, double by, double mx, double my){
	    int xA = (int)(mx*xValue+bx);
	    int yA = (int)(my*yValue+by);
	    if (isSelected) g.setColor(Color.red);
	    else g.setColor(Color.black);
	    g.drawOval(xA,yA,2,2);
	}

	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();

		if (POINT_SELECTED.equals(cmd)) {
			isSelected = true;
			System.out.println("Point (" + xValue + "/" + yValue + ") selected!");
			//plot(g,bx,by,mx,my);
		}
	}
}
