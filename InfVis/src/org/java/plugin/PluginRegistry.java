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
import java.util.Map;

/**
 * Root interface to get access to all meta-information about discovered
 * plug-ins. All objects accessible from the registry are immutable. You
 * can imagine registry as a read-only storage of full information about
 * discovered plug-ins. There is only one exception from this rule: internal
 * state of registry, plug-in descriptors and plug-in elements can be modified
 * indirectly by {@link #register(URL[]) registering} new plug-ins with this
 * registry.
 * @version $Id: PluginRegistry.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
 */
public interface PluginRegistry {
    /**
     * Registers plug-ins and manifests in this registry.
     * @param manifests array of manifest locations
     * @return map where keys are URL's and values are registered plug-ins
     *         or plug-in fragments, URL's for unprocessed manifests are
     *         not included
     */
    Map register(URL[] manifests);
    
    /**
     * Returns descriptor of plug-in with given ID.
     * <br>
     * If plug-in descriptor with given ID can't be found or such plug-in
     * exists but is damaged this method have to throw an
     * {@link IllegalArgumentException}. In other words, this method
     * shouldn't return <code>null</code>.
     * @param pluginId plug-id ID
     * @return plug-in descriptor
     */
    PluginDescriptor getPluginDescriptor(String pluginId);
    
    /**
     * Returns collection of descriptors of all plug-ins that was successfully
     * populated by this registry.
     * @return collection of {@link PluginDescriptor} objects
     */
    Collection getPluginDescriptors();
	
    /**
     * Looks for extension point. This method have throw an
     * {@link IllegalArgumentException} if requested extension point
     * can't be found or is in invalid state.
     * @param pluginId plug-in ID
     * @param pointId extension point ID
     * @return plug-in extension point
     * @see ExtensionPoint#isValid()
     */
    ExtensionPoint getExtensionPoint(String pluginId, String pointId);

    /**
     * Looks for extension point.
     * @param uniqueId extension point unique ID
     * @return plug-in extension point
     * @see #getExtensionPoint(String, String)
     */
    ExtensionPoint getExtensionPoint(String uniqueId);
    
    /**
     * Returns collection of plug-in descriptors that was discovered by registry
     * but not available as valid plug-ins as they have older versions.
     * @param pluginId plug-in ID
     * @return collection of {@link PluginDescriptor} objects
     */
    Collection getOldPluginDescriptors(String pluginId);

    /**
     * Returns collection of descriptors of all plug-in fragments that was
     * successfully populated by this registry.
     * @return collection of {@link PluginFragment} objects
     */
    Collection getPluginFragments();

    /**
     * Performs integrity check of all valid plug-ins and generates result as
     * collection of standard report items.
     * @param pathResolver optional path resolver
     * @return integrity check report
     */
    IntegrityCheckReport checkIntegrity(PathResolver pathResolver);
    
    /**
     * Extracts plug-in ID from some unique identifier.
     * @param uniqueId unique ID
     * @return plug-in ID
     */
    String extractPluginId(String uniqueId);

    /**
     * Extracts plug-in element ID from some unique identifier.
     * @param uniqueId unique ID
     * @return element ID
     */
    String extractId(String uniqueId);
    
    /**
     * Extracts plug-in version identifier from some unique identifier.
     * @param uniqueId unique ID
     * @return plug-in version identifier
     */
    Version extractVersion(String uniqueId);
}
