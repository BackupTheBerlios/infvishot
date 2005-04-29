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
 * ---------------------
 * AxisPackageTests.java
 * ---------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Bill Kelemen;
 *
 * $Id: AxisPackageTests.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 *
 * Changes:
 * --------
 * 26-Mar-2003 : Version 1 (DG);
 * 25-May-2003 : Added SegmentedTimelineTests (BK);
 * 17-Feb-2004 : Added extra tests (DG);
 *
 */

package org.jfree.chart.axis.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A collection of tests for the com.jrefinery.chart.axis package.
 * <P>
 * These tests can be run using JUnit (http://www.junit.org).
 *
 * @author David Gilbert
 */
public class AxisPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return the test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.axis");
        suite.addTestSuite(AxisLocationTests.class);
        suite.addTestSuite(AxisSpaceTests.class);
        suite.addTestSuite(AxisTests.class);
        suite.addTestSuite(CategoryAxisTests.class);
        suite.addTestSuite(CategoryAxis3DTests.class);
        suite.addTestSuite(CategoryLabelPositionTests.class);
        suite.addTestSuite(CategoryLabelPositionsTests.class);
        suite.addTestSuite(ColorBarTests.class);
        suite.addTestSuite(CyclicNumberAxisTests.class);
        suite.addTestSuite(DateAxisTests.class);
        suite.addTestSuite(DateTickUnitTests.class);
        suite.addTestSuite(LogarithmicAxisTests.class);
        suite.addTestSuite(MarkerAxisBandTests.class);
        suite.addTestSuite(NumberAxisTests.class);
        suite.addTestSuite(NumberAxis3DTests.class);
        suite.addTestSuite(SegmentedTimelineTests.class);
        suite.addTestSuite(SymbolicAxisTests.class);
        suite.addTestSuite(ValueAxisTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     *
     * @param name  the suite name.
     */
    public AxisPackageTests(String name) {
        super(name);
    }

}
