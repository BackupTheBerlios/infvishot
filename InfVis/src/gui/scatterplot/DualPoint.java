/**
 * Created on 25.03.2005
 * Project: InfVis
 * @author: tobias
 */

package gui.scatterplot;

import java.awt.*;

public class DualPoint extends DualObject{
	
	protected double x, y;
	
	public DualPoint(String id, double xValue, double yValue, DrawArea d){
		super(id,d);
		x = xValue;
		y = yValue;
		anzGleich = 1;
	}
	
	public void draw(Graphics g){
			
		// optain the parameters mx,bx,my,by
		//double p[] = ScatterArea.getParameters();
		int size = ScatterArea.DEFAULT_POINT_SIZE;
		
		//System.out.println(id + ": " + anzGleich);

		setColor(g);
		g.drawOval(ScatterArea.convertX(x)-(int)Math.ceil(size/2),ScatterArea.convertY(y)-(int)Math.ceil(size/2),size,size);

	}
	
	/*public void drawK5(Graphics g){
		int size = ScatterArea.DEFAULT_POINT_SIZE;
		int dir = 0;
		setColor(g);
		g.drawOval(ScatterArea.convertX(x)-(int)Math.ceil(size/2),ScatterArea.convertY(y)-(int)Math.ceil(size/2),size,size);
		size = Math.max(1,size);
		for (int i=1; i<=anzGleich; i++){
			switch(dir){
				case 0: g.drawOval(ScatterArea.convertX(x)-(int)Math.ceil(size/2)+size,ScatterArea.convertY(y)-(int)Math.ceil(size/2),size,size); dir++; break;
				case 1: g.drawOval(ScatterArea.convertX(x)-(int)Math.ceil(size/2),ScatterArea.convertY(y)-(int)Math.ceil(size/2)-size,size,size); dir++; break;
				case 2: g.drawOval(ScatterArea.convertX(x)-(int)Math.ceil(size/2),ScatterArea.convertY(y)-(int)Math.ceil(size/2)+size,size,size); dir++; break;
				case 3: g.drawOval(ScatterArea.convertX(x)-(int)Math.ceil(size/2)-size,ScatterArea.convertY(y)-(int)Math.ceil(size/2),size,size); dir++; break;
				//case 4: g.drawOval(ScatterArea.convertX(x)-(int)Math.ceil(size/2)+size,ScatterArea.convertY(y)-(int)Math.ceil(size/2)-size,size,size); dir++; break;
				//case 5: g.drawOval(ScatterArea.convertX(x)-(int)Math.ceil(size/2)-size,ScatterArea.convertY(y)-(int)Math.ceil(size/2)+size,size,size); dir++; break;
				//case 6: g.drawOval(ScatterArea.convertX(x)-(int)Math.ceil(size/2)+size,ScatterArea.convertY(y)-(int)Math.ceil(size/2)+size,size,size); dir++; break;
				//case 7: g.drawOval(ScatterArea.convertX(x)-(int)Math.ceil(size/2)-size,ScatterArea.convertY(y)-(int)Math.ceil(size/2)-size,size,size); dir++; break;
			}
		}
	}*/
	
	public void drawK10(Graphics g){
		// TODO
	}
	
	public void drawG10(Graphics g){
		// TODO
	}

}
