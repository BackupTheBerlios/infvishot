/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------------------
 * StatisticalBarRenderer.java
 * ---------------------------
 * (C) Copyright 2002-2004, by Pascal Collet and Contributors.
 *
 * Original Author:  Pascal Collet;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: StatisticalBarRenderer.java,v 1.1 2005/04/28 16:29:18 harrym_nu Exp $
 *
 * Changes
 * -------
 * 21-Aug-2002 : Version 1, contributed by Pascal Collet (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Changes to dataset interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 05-Feb-2003 : Updates for new DefaultStatisticalCategoryDataset (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 06-Oct-2003 : Corrected typo in exception message (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.CategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that handles the drawing a bar plot where
 * each bar has a mean value and a standard deviation line.
 *
 * @author Pascal Collet
 */
public class StatisticalBarRenderer extends BarRenderer
                                    implements CategoryItemRenderer, 
                                               Cloneable, PublicCloneable, Serializable {

    /**
     * Default constructor.
     */
    public StatisticalBarRenderer() {
        super();
    }

    /**
     * Draws the bar with its standard deviation line range for a single (series, category) data
     * item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param data  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset data,
                         int row,
                         int column) {


        // defensive check
        if (!(data instanceof StatisticalCategoryDataset)) {
            throw new IllegalArgumentException("StatisticalBarRenderer.drawCategoryItem()"
                + " : the data should be of type StatisticalCategoryDataset only.");
        }
        StatisticalCategoryDataset statData = (StatisticalCategoryDataset) data;

        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            drawHorizontalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, statData, 
                               row, column);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            drawVerticalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, statData, 
                             row, column);
        }
    }
                
    /**
     * Draws an item for a plot with a horizontal orientation.
     * 
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawHorizontalItem(Graphics2D g2,
                                      CategoryItemRendererState state,
                                      Rectangle2D dataArea,
                                      CategoryPlot plot,
                                      CategoryAxis domainAxis,
                                      ValueAxis rangeAxis,
                                      StatisticalCategoryDataset dataset,
                                      int row,
                                      int column) {
                                     
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        
        // BAR Y
        double rectY = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, 
                                                   xAxisLocation);

        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = dataArea.getHeight() * getItemMargin()
                               / (categoryCount * (seriesCount - 1));
            rectY = rectY + row * (state.getBarWidth() + seriesGap);
        }
        else {
            rectY = rectY + row * state.getBarWidth();
        }

        // BAR X
        Number meanValue = dataset.getMeanValue(row, column);

        double value = meanValue.doubleValue();
        double base = 0.0;
        double lclip = getLowerClip();
        double uclip = getUpperClip();

        if (uclip <= 0.0) {  // cases 1, 2, 3 and 4
            if (value >= uclip) {
                return; // bar is not visible
            }
            base = uclip;
            if (value <= lclip) {
                value = lclip;
            }
        }
        else if (lclip <= 0.0) { // cases 5, 6, 7 and 8
            if (value >= uclip) {
                value = uclip;
            }
            else {
                if (value <= lclip) {
                    value = lclip;
                }
            }
        }
        else { // cases 9, 10, 11 and 12
            if (value <= lclip) {
                return; // bar is not visible
            }
            base = getLowerClip();
            if (value >= uclip) {
               value = uclip;
            }
        }

        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transY1 = rangeAxis.valueToJava2D(base, dataArea, yAxisLocation);
        double transY2 = rangeAxis.valueToJava2D(value, dataArea, yAxisLocation);
        double rectX = Math.min(transY2, transY1);

        double rectHeight = state.getBarWidth();
        double rectWidth = Math.abs(transY2 - transY1);

        Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
        Paint seriesPaint = getItemPaint(row, column);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (state.getBarWidth() > 3) {
            g2.setStroke(getItemStroke(row, column));
            g2.setPaint(getItemOutlinePaint(row, column));
            g2.draw(bar);
        }

        // standard deviation lines
        double valueDelta = dataset.getStdDevValue(row, column).doubleValue();
        double highVal = rangeAxis.valueToJava2D(
            meanValue.doubleValue() + valueDelta, dataArea, yAxisLocation
        );
        double lowVal = rangeAxis.valueToJava2D(
            meanValue.doubleValue() - valueDelta, dataArea, yAxisLocation
        );

        Line2D line = null;
        line = new Line2D.Double(lowVal, rectY + rectHeight / 2.0d, 
                                 highVal, rectY + rectHeight / 2.0d);
        g2.draw(line);
        line = new Line2D.Double(highVal, rectY + rectHeight * 0.25, 
                                 highVal, rectY + rectHeight * 0.75);
        g2.draw(line);
        line = new Line2D.Double(lowVal, rectY + rectHeight * 0.25, 
                                 lowVal, rectY + rectHeight * 0.75);
        g2.draw(line);
    }

    /**
     * Draws an item for a plot with a vertical orientation.
     * 
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawVerticalItem(Graphics2D g2,
                                    CategoryItemRendererState state,
                                    Rectangle2D dataArea,
                                    CategoryPlot plot,
                                    CategoryAxis domainAxis,
                                    ValueAxis rangeAxis,
                                    StatisticalCategoryDataset dataset,
                                    int row,
                                    int column) {
                                     
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        
        // BAR X
        double rectX = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, 
                                                   xAxisLocation);

        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = dataArea.getWidth() * getItemMargin()
                               / (categoryCount * (seriesCount - 1));
            rectX = rectX + row * (state.getBarWidth() + seriesGap);
        }
        else {
            rectX = rectX + row * state.getBarWidth();
        }

        // BAR Y
        Number meanValue = dataset.getMeanValue(row, column);

        double value = meanValue.doubleValue();
        double base = 0.0;
        double lclip = getLowerClip();
        double uclip = getUpperClip();

        if (uclip <= 0.0) {  // cases 1, 2, 3 and 4
            if (value >= uclip) {
                return; // bar is not visible
            }
            base = uclip;
            if (value <= lclip) {
                value = lclip;
            }
        }
        else if (lclip <= 0.0) { // cases 5, 6, 7 and 8
            if (value >= uclip) {
                value = uclip;
            }
            else {
                if (value <= lclip) {
                    value = lclip;
                }
            }
        }
        else { // cases 9, 10, 11 and 12
            if (value <= lclip) {
                return; // bar is not visible
            }
            base = getLowerClip();
            if (value >= uclip) {
               value = uclip;
            }
        }

        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transY1 = rangeAxis.valueToJava2D(base, dataArea, yAxisLocation);
        double transY2 = rangeAxis.valueToJava2D(value, dataArea, yAxisLocation);
        double rectY = Math.min(transY2, transY1);

        double rectWidth = state.getBarWidth();
        double rectHeight = Math.abs(transY2 - transY1);

        Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
        Paint seriesPaint = getItemPaint(row, column);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (state.getBarWidth() > 3) {
            g2.setStroke(getItemStroke(row, column));
            g2.setPaint(getItemOutlinePaint(row, column));
            g2.draw(bar);
        }

        // standard deviation lines
        double valueDelta = dataset.getStdDevValue(row, column).doubleValue();
        double highVal = rangeAxis.valueToJava2D(
            meanValue.doubleValue() + valueDelta, dataArea, yAxisLocation
        );
        double lowVal = rangeAxis.valueToJava2D(
            meanValue.doubleValue() - valueDelta, dataArea, yAxisLocation
        );

        Line2D line = null;
        line = new Line2D.Double(rectX + rectWidth / 2.0d, lowVal,
                                 rectX + rectWidth / 2.0d, highVal);
        g2.draw(line);
        line = new Line2D.Double(rectX + rectWidth / 2.0d - 5.0d, highVal,
                                 rectX + rectWidth / 2.0d + 5.0d, highVal);
        g2.draw(line);
        line = new Line2D.Double(rectX + rectWidth / 2.0d - 5.0d, lowVal,
                                 rectX + rectWidth / 2.0d + 5.0d, lowVal);
        g2.draw(line);
    }

}
