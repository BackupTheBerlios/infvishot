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

import java.net.URL;
import java.util.Collection;

/**
 * Main interface to get access to all meta-information for particular
 * plug-in, described in plug-in manifest file.
 * @see <a href="{@docRoot}/../plugin_0_2.dtd">plug-in DTD for standard registry implmentation</a>
 * @see org.java.plugin.PluginRegistry
 * @version $Id: PluginDescriptor.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
 */
public interface PluginDescriptor extends Identity, Documentable {
    /**
     * Returns combination of plug-in ID and version identifier that is unique
     * within whole set of registered plug-ins.
     * @return unique ID for this plug-in
     */
    String getUniqueId();

    /**
     * @return vendor as specified in manifest file or empty string
     */
    String getVendor();
	
	/**
     * @return plug-in version identifier as specified in manifest file
	 */
    Version getVersion();

    /**
     * Returns collection of all top level attributes defined in manifest.
     * @return collection of {@link PluginAttribute} objects
     */
    Collection getAttributes();
    
    /**
     * @param id ID of attribute to look for
     * @return attribute with given ID
     */
    PluginAttribute getAttribute(String id);

    /**
     * @param id ID of attribute to look for
     * @return collection of all attributes with given ID
     */
    PluginAttribute[] getAttributes(String id);
	
    /**
     * Returns collection of all prerequisites defined in manifest.
     * @return collection of {@link PluginPrerequisite} objects
     */
	Collection getPrerequisites();
	
    /**
     * Returns collection of all extension points defined in manifest.
     * @return collection of {@link ExtensionPoint} objects
     */
    Collection getExtensionPoints();
	
    /**
     * Returns collection of all extensions defined in manifest.
     * @return collection of {@link Extension} objects
     */
    Collection getExtensions();
	
    /**
     * Returns collection of all libraries defined in manifest.
     * @return collection of {@link Library} objects
     */
    Collection getLibraries();

	/**
     * @return plug-ins registry
	 */
    PluginRegistry getRegistry();
    
    /**
     * @return plug-in class name as specified in manifest file or <code>null</code>
     */
    String getPluginClassName();
    
    /**
     * Returns collection of plug-in fragments which contributes to this plug-in.
     * One plug-in fragment may contribute to several versions of the same
     * plug-in, according to it's manifest.
     * @return collection of {@link PluginFragment} objects
     */
    Collection getFragments();
    
    /**
     * @return location from which this plug-in was registered
     */
    URL getLocation();
}