/*****************************************************************************
 * Java Plug-in Framework (JPF)
 * Copyright (C) 2004 Dmitry Olshansky
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *****************************************************************************/
package org.java.plugin;

import java.util.StringTokenizer;

/**
 * This class represents a plug-in version identifier.
 * <br>
 * TODO: implement {@link java.lang.Comparable} interface.
 * @version $Id: Version.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
 */
public final class Version {
    /**
     * Version identifier parts separator.
     */
    public static final char SEPARATOR = '.';
    
    /**
     * Parses given string as version identifier. All missing parts will be
     * initialized to 0 or empty string. Parsing starts from left side of the
     * string.
     * @param str version identifier as string
     * @return version identifier object
     */
    public static Version parse(String str) {
        int major = 0;
        int minor = 0;
        int build = 0;
        String name = "";
        StringTokenizer st = new StringTokenizer(str, "" + SEPARATOR, false);
        if (st.hasMoreTokens()) {
            major = Integer.parseInt(st.nextToken(), 10);
            if (st.hasMoreTokens()) {
                minor = Integer.parseInt(st.nextToken(), 10);
                if (st.hasMoreTokens()) {
                    build = Integer.parseInt(st.nextToken(), 10);
                    if (st.hasMoreTokens()) {
                        name = st.nextToken();
                    }
                }
            }
        }
        return new Version(major, minor, build, name);
    }

    private int major;
    private int minor;
    private int build;
    private String name;
    private String toString;
    
    /**
     * Creates version identifier object from given parts. No validation
     * performed during object instantiation, all values become parts of
     * version identifier as they are.
     * @param major major version number
     * @param minor minor version number
     * @param build build number
     * @param name build name
     */
    public Version(int major, int minor, int build, String name) {
        super();
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.name = name;
        if (this.name == null) {
            this.name = "";
        }
    }

    /**
     * @return build number
     */
    public int getBuild() {
        return build;
    }

    /**
     * @return major version number
     */
    public int getMajor() {
        return major;
    }

    /**
     * @return minor version number
     */
    public int getMinor() {
        return minor;
    }
    
    /**
     * @return build name
     */
    public String getName() {
        return name;
    }

    /**
     * Compares two version identifiers to see if this one is
     * greater than or equal to the argument.
     * <p>
     * A version identifier is considered to be greater than or equal
     * if its major component is greater than the argument major 
     * component, or the major components are equal and its minor component
     * is greater than the argument minor component, or the
     * major and minor components are equal and its build component is
     * greater than the argument build component, or all components are equal.
     * </p>
     *
     * @param other the other version identifier
     * @return <code>true</code> if this version identifier
     *         is compatible with the given version identifier, and
     *         <code>false</code> otherwise
     */
    public boolean isGreaterOrEqualTo(Version other) {
        if (other == null) {
            return false;
        }
        if (major > other.major) {
            return true;
        }
        if ((major == other.major) && (minor > other.minor)) {
            return true;
        }
        if ((major == other.major) && (minor == other.minor)
                && (build > other.build)) {
            return true;
        }
        if ((major == other.major) && (minor == other.minor)
                && (build == other.build) && name.equalsIgnoreCase(other.name)) {
            return true;
        }
        return false;
    }

    /**
     * Compares two version identifiers for compatibility.
     * <p>
     * A version identifier is considered to be compatible if its major 
     * component equals to the argument major component, and its minor component
     * is greater than or equal to the argument minor component.
     * If the minor components are equal, than the build component of the
     * version identifier must be greater than or equal to the build component
     * of the argument identifier.
     * </p>
     *
     * @param other the other version identifier
     * @return <code>true</code> if this version identifier
     *         is compatible with the given version identifier, and
     *         <code>false</code> otherwise
     */
    public boolean isCompatibleWith(Version other) {
        if (other == null) {
            return false;
        }
        if (major != other.major) {
            return false;
        }
        if (minor > other.minor) {
            return true;
        }
        if (minor < other.minor) {
            return false;
        }
        if (build > other.build) {
            return true;
        }
        return false;
    }

    /**
     * Compares two version identifiers for equivalency.
     * <p>
     * Two version identifiers are considered to be equivalent if their major 
     * and minor components equal and are at least at the same build level 
     * as the argument.
     * </p>
     *
     * @param other the other version identifier
     * @return <code>true</code> if this version identifier
     *         is equivalent to the given version identifier, and
     *         <code>false</code> otherwise
     */
    public boolean isEquivalentTo(Version other) {
        if (other == null) {
            return false;
        }
        if (major != other.major) {
            return false;
        }
        if (minor != other.minor) {
            return false;
        }
        if (build >= other.build) {
            return true;
        }
        return false;
    }

    /**
     * Compares two version identifiers for order using multi-decimal
     * comparison. 
     *
     * @param other the other version identifier
     * @return <code>true</code> if this version identifier
     *         is greater than the given version identifier, and
     *         <code>false</code> otherwise
     */
    public boolean isGreaterThan(Version other) {
        if (other == null) {
            return false;
        }
        if (major > other.major) {
            return true;
        }
        if (major < other.major) {
            return false;
        }
        if (minor > other.minor) {
            return true;
        }
        if (minor < other.minor) {
            return false;
        }
        if (build > other.build) {
            return true;
        }
        return false;

    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return toString().hashCode();
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Version)) {
            return false;
        }
        Version other = (Version)obj;
        if ((major != other.major) || (minor != other.minor)
                || (build != other.build) || !name.equalsIgnoreCase(other.name)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the string representation of this version identifier. 
     * The result satisfies
     * <code>vi.equals(new Version(vi.toString()))</code>.
     * @return the string representation of this plug-in version identifier
     */
    public String toString() {
        if (toString == null) {
            toString = "" + major + SEPARATOR + minor + SEPARATOR + build
                + ("".equals(name)? "" : SEPARATOR + name);
        }
        return toString;
    }
}
