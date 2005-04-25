/**
 * Created on 25.03.2005
 * Project: InfVis
 * @author: tobias
 */

package gui.scatterplot;

import java.awt.*;

abstract class DualObject {
	
	protected String id;
	protected int state;
	protected DrawArea draw;
	protected int anzGleich;
	
	public static final int NORMAL = 1;
	public static final int SELECTED = 2;
	public static final int INACTIVE = 3;
	
	abstract public void draw(Graphics g);
	
	public DualObject(String identifier, DrawArea d){
		id = identifier;
		draw = d;
		setState(NORMAL);
	}
	
	public void setState(int i){
		state = i;
	}
	
	public void setColor(Graphics g){
		switch(state){
			case NORMAL:
				{
					Color c = new Color(255,255,255);
					for(int i=anzGleich;i>0;i--){
						c = redder(c);
					}
					g.setColor(c);
				}
				break;
			case SELECTED:
				g.setColor(Color.cyan.darker()); break;
			case INACTIVE:
				g.setColor(Color.gray); break;
			default:
				System.out.println("DualObject::setColor() Failed to generate Color!");
		}
	}
	
	public Color redder (Color c){
		return new Color(255,Math.max(0,c.getGreen()-10),Math.max(c.getBlue()-10,0));
	}

}
