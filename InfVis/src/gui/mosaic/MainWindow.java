package gui.mosaic;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MainWindow  extends JPanel implements ActionListener, MouseListener {
	MosaicArea mos;
	//private Vector h;
	private double distance;
	JFrame frame;
	private DataObject Daten, selected;
	private String[][] datas, selectedData;
	private String[] names, selectedDataNames;
	private ProcessData prozi, filledProzi;
	//private DataCount DataCounter;
	//public boolean RectsFilled = false;
	//public double dist = 0.1;
	
	public MainWindow(){
	    datas = new String [2][7];
		names = new String[2];
		
		
		datas[0][0] = "Franz";
		datas[1][0] = "gscheit";
		datas[0][1] = "Oliver";
		datas[1][1] = "blöd";
		datas[0][2] = "Herbert";
		datas[1][2] = "blöd";
		datas[0][3] = "Franz";
		datas[1][3] = "gscheit";
		datas[0][4] = "Franz";
		datas[1][4] = "gscheit";
		datas[0][5] = "Oliver";
		datas[1][5] = "gscheit";
		datas[0][6] = "Franz";
		datas[1][6] = "blöd";
		
		names[0] = "Spalte1";
		names[1] = "Spalte2";
		
		initialize();
	}
	
	public MainWindow(String[][] data, String[] nam){
		datas = data;
		names = nam;
		
		initialize();
	}
	
	public void setData(String[][] data, String[] nam){
	    datas = data;
		names = nam;
		
		initialize();
		this.repaint();
	}
	
	public void initialize(){
		Daten = new DataObject(names, datas);
		prozi = new ProcessData(Daten, names[0], names[1]);
		/*
		frame = new JFrame("Mosaic Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        */
		
        mos = new MosaicArea(550, 500, prozi.getVector(), prozi.dist);
        this.add(mos);
        
      /*  frame.getContentPane().setLayout(new BorderLayout()); 
        frame.getContentPane().add(this, BorderLayout.CENTER);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        */
        this.addComponentListener(new java.awt.event.ComponentAdapter() { 
			public void componentResized(java.awt.event.ComponentEvent e) {    
				resizeMos();
			}
		});
	}
	
	public void resizeMos(){
		/**frame.remove(this);
		this.remove(mos);
		Dimension dd = new Dimension();
		dd = this.getSize();
		mos = new MosaicArea((int)dd.width, (int)dd.height, h, distance);
		this.add(mos);
		frame.getContentPane().add(this, BorderLayout.CENTER);
		frame.pack();
        frame.setVisible(true);**/
		//System.out.println(frame.getSize());
		mos.setPreferredSize(this.getSize());
	    mos.setSize(this.getSize());
	    mos.setPrefSize(this.getSize());
	    
	    mos.refreshSize();
	}
	
	public void fillRects(String[][] selData, String[] selDataNam){
		selectedData = selData;
		selectedDataNames = selDataNam;
		
		selected = new DataObject(selectedDataNames, selectedData);
		filledProzi = new ProcessData(selected, selectedDataNames[0], selectedDataNames[1]);
		mos.fill(filledProzi.getVector());
		mos.setRectsFilled(true);
		
	}
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
