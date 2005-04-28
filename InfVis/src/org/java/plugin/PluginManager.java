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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.impl.PluginRegistryImpl;

/**
 * JPF "home" class, the entry point to framework API. It is expected that
 * only one instance of this class will be created per framework.
 * @version $Id: PluginManager.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
 */
public final class PluginManager {
    static final boolean DUMP_ENABLED = true;

    private static final Log log = LogFactory.getLog(PluginManager.class);
    
    /**
     * Creates new instance of standard implementation of plug-in registry.
     * This implementation supports plug-in manifest files prepared according
     * to <a href="{@docRoot}/../plugin_0_2.dtd">plug-in DTD</a>.
     * @return new registry instance
     */
    public static PluginRegistry createStandardRegistry() {
        return new PluginRegistryImpl();
    }
    
    /**
     * Creates new instance of plug-in manager using
     * {@link #createStandardRegistry() standard implementation of plug-in registry}
     * and {@link StandardPathResolver standard path resolver}.
     * @param pluginLocations map with plug-in manifests and plug-in context folders
     * @return new plug-in manager instance
     * @throws Exception
     */
    public static PluginManager createStandardManager(Map pluginLocations)
            throws Exception {
        Map normalizedLocations = new HashMap();
        for (Iterator it = pluginLocations.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            normalizedLocations.put(obj2url(key), obj2url(pluginLocations.get(key)));
        }
        PluginRegistry registry = createStandardRegistry();
        Map processedPlugins = registry.register((URL[])normalizedLocations.keySet().toArray(new URL[normalizedLocations.size()]));
        StandardPathResolver pathResolver = new StandardPathResolver();
        for (Iterator it = processedPlugins.keySet().iterator(); it.hasNext();) {
            URL manifestUrl = (URL)it.next();
            pathResolver.registerContext(
                    (Identity)processedPlugins.get(manifestUrl),
                    (URL)normalizedLocations.get(manifestUrl));
        }
        return new PluginManager(pathResolver, registry);
    }
    
    private static URL obj2url(Object o) throws Exception {
        if (o instanceof URL) {
            return (URL)o;
        } else if (o instanceof File) {
            return ((File)o).toURL();
        } else if (o instanceof String) {
            return new URL((String)o);
        } else if (o instanceof URI) {
            return ((URI)o).toURL();
        } else {
            throw new Exception("unsupported object type - " + o.getClass().getName());
        }
    }

    private final PathResolver pathResolver;
    private final PluginRegistry registry;
    private final Map activePlugins = new HashMap(); // <plugin-id, Plugin>
    private final Set activatingPlugins = new HashSet(); // <plugin-id>
    private final Set badPlugins = new HashSet(); // <plugin-id>
    private final List activationLog = new LinkedList(); // <plugin-id>
    private final Map classLoaders = new HashMap(); // <plugin-id, PluginClassLoader>

    /**
     * Creates instance of plug-in manager for given registry.
     * @param pathResolver some implementation of path resolver interface
     * @param registry some implementation of plug-in registry interface
     */
    public PluginManager(PathResolver pathResolver, PluginRegistry registry) {
        this.pathResolver = pathResolver;
        this.registry = registry;
    }

    /**
     * @return registry, used by this manager
     */
    public PluginRegistry getRegistry() {
        return registry;
    }
    
    /**
     * @return path resolver
     */
    public PathResolver getPathResolver() {
        return pathResolver;
    }
    
    /**
     * Looks for plug-in with given ID and activates it if it is not activated yet.
     * Note that this method will never return <code>null</code>.
     * @param id plug-in ID
     * @return found plug-in
     * @throws Exception if plug-in can't be found or activated
     */
    public Plugin getPlugin(String id) throws Exception {
        Plugin result = (Plugin)activePlugins.get(id);
        if (result != null) {
            return result;
        }
        if (badPlugins.contains(id)) {
            throw new Exception("plug-in " + id
                    + " disabled as it wasn't properly initialized");
        }
        PluginDescriptor descr = registry.getPluginDescriptor(id);
        if (descr == null) {
            throw new IllegalArgumentException("unknown plug-in ID - " + id);
        }
        return result = activatePlugin(descr);
    }

    /**
     * Activates plug-in with given ID if it is not activated yet.
     * @param id plug-in ID
     * @throws Exception if plug-in can't be found or activated
     */
    public void activatePlugin(String id) throws Exception {
        if (activePlugins.containsKey(id)) {
            return;
        }
        if (badPlugins.contains(id)) {
            throw new Exception("plug-in " + id
                    + " disabled as it wasn't properly initialized");
        }
        PluginDescriptor descr = registry.getPluginDescriptor(id);
        if (descr == null) {
            throw new IllegalArgumentException("unknown plug-in ID - " + id);
        }
        activatePlugin(descr);
    }

    /**
     * Looks for plug-in, given object belongs to.
     * @param obj
     * @return plug-in or <code>null</code> if given object doesn't belong
     *         to any plug-in (possibly it is part of "host" application)
     * @throws Exception if object belongs to "bad" plug-in
     */
    public Plugin getPluginFor(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        ClassLoader clsLoader;
        if (obj instanceof Class) {
            clsLoader = ((Class)obj).getClassLoader();
        } else if (obj instanceof ClassLoader) {
            clsLoader = (ClassLoader)obj;
        } else {
            clsLoader = obj.getClass().getClassLoader();
        }
        if (!(clsLoader instanceof PluginClassLoader)) {
            return null;
        }
        PluginDescriptor descr = ((PluginClassLoader)clsLoader)
                .getPluginDescriptor();
        Plugin result = (Plugin)activePlugins.get(descr.getId());
        if (result != null) {
            return result;
        }
        if (badPlugins.contains(descr.getId())) {
            throw new Exception("plug-in " + descr.getId()
                    + " disabled as it wasn't properly initialized");
        }
        throw new IllegalStateException("can't get plug-in " + descr);
    }

    /**
     * @param descr plug-in descriptor
     * @return <code>true</code> if plug-in with given descriptor is activated
     */
    public boolean isPluginActivated(PluginDescriptor descr) {
        return activePlugins.containsKey(descr.getId());
    }

    boolean isPluginActivating(PluginDescriptor descr) {
        return activatingPlugins.contains(descr.getId());
    }
    
    /**
     * Returns instance of plug-in's class loader and not tries to activate plug-in.
     * Use this method if you need to get access to plug-in resources and don't want
     * to cause plug-in activation.
     * @param descr plug-in descriptor
     * @return class loader instance for plug-in with given descriptor
     */
    public ClassLoader getPluginClassLoader(PluginDescriptor descr) {
        ClassLoader result = (ClassLoader)classLoaders.get(descr.getId());
        if (result != null) {
            return result;
        }
        synchronized (this) {
            result = (ClassLoader)classLoaders.get(descr.getId());
            if (result != null) {
                return result;
            }
            result = new PluginClassLoader(this, descr,
                    getClass().getClassLoader());
            classLoaders.put(descr.getId(), result);
        }
        return result;
    }
    
    /**
     * Shuts down the framework.
     * <br>
     * Calling this method will deactivate all active plug-ins
     * in order, revers to order they was activated. It also releases all resources
     * allocated by this manager (class loaders, plug-in descriptors etc.).
     */
    public synchronized void shutdown() {
        log.debug("shutting down...");
        dump();
        List reversedLog = new ArrayList(activationLog);
        Collections.reverse(reversedLog);
        for (Iterator it = reversedLog.iterator(); it.hasNext();) {
            String id = (String)it.next();
            PluginDescriptor descr = registry.getPluginDescriptor(id);
            if (descr == null) {
                log.warn("can't find descriptor for plug-in " + id + " to deactivate plug-in");
                continue;
            }
            deactivatePlugin(descr);
        }
        activationLog.clear();
        badPlugins.clear();
        dump();
        activatingPlugins.clear();
        classLoaders.clear();
        log.info("shutdown done");
    }
    
    private synchronized Plugin activatePlugin(PluginDescriptor descr)
            throws Exception {
        Plugin result = (Plugin)activePlugins.get(descr.getId());
        if (result != null) {
            return result;
        }
        if (badPlugins.contains(descr.getId())) {
            throw new Exception("plug-in " + descr.getId()
                    + " disabled as it wasn't properly initialized");
        }
        if (activatingPlugins.contains(descr.getId())) {
            throw new Exception("dependencies loop detected during" +
                    " activation of plug-in " + descr.getId());
        }
        activatingPlugins.add(descr.getId());
        try {
            try {
                checkPrerequisites(descr);
            } catch (Exception e) {
                badPlugins.add(descr.getId());
                throw e;
            }
            String className = descr.getPluginClassName();
            if ((className == null) || "".equals(className.trim())) {
                result = new EmptyPlugin(this, descr);
            } else {
                Class pluginClass;
                try {
                    pluginClass = getPluginClassLoader(descr).loadClass(
                            className);
                } catch (ClassNotFoundException cnfe) {
                    badPlugins.add(descr.getId());
                    throw new Exception("can't find plug-in class "
                            + className, cnfe);
                }
                try {
                    result = (Plugin)pluginClass.getConstructor(
                            new Class[] {getClass(),
                                    PluginDescriptor.class}).newInstance(
                                            new Object[] {this, descr});
                } catch (InvocationTargetException ite) {
                    badPlugins.add(descr.getId());
                    Throwable t = ite.getTargetException();
                    throw new Exception("can't create class instance" +
                            " for plug-in " + descr.getId(),
                            (t != null) ? t : ite);
                } catch (Exception e) {
                    badPlugins.add(descr.getId());
                    throw new Exception("can't create class instance" +
                            " for plug-in " + descr.getId(), e);
                }
            }
            try {
                result.start();
            } catch (Exception e) {
                badPlugins.add(descr.getId());
                throw new Exception("can't start plug-in " + descr.getId(), e);
            }
            activePlugins.put(descr.getId(), result);
            activationLog.add(descr.getId());
            log.info("plug-in started - " + descr.getId());
            return result;
        } finally {
            activatingPlugins.remove(descr.getId());
        }
    }
    
    private void checkPrerequisites(PluginDescriptor descr) throws Exception {
        for (Iterator it = descr.getPrerequisites().iterator(); it.hasNext();) {
            PluginPrerequisite pre = (PluginPrerequisite)it.next();
            if (activatingPlugins.contains(pre.getPluginId())) {
                continue;
            }
            if (badPlugins.contains(pre.getPluginId())) {
                if (pre.isOptional()) {
                    continue;
                }
                throw new Exception("plug-in " + descr.getId()
                        + " requires plug-in " + pre.getPluginId()
                        + " which failed activation");
            }
            if (!pre.matches()) {
                if (pre.isOptional()) {
                    continue;
                }
                throw new Exception("plug-in " + descr.getId()
                        + " requires plug-in " + pre.getPluginId()
                        + " which has incompatible version");
            }
            Plugin plugin = activatePlugin(registry.getPluginDescriptor(
                    pre.getPluginId()));
            if (plugin == null) {
                if (pre.isOptional()) {
                    continue;
                }
                throw new Exception("can't activate plug-in "
                        + pre.getPluginId());
            }
        }
    }

    private synchronized void deactivatePlugin(PluginDescriptor descr) {
        Plugin plugin = (Plugin)activePlugins.get(descr.getId());
        if (plugin == null) {
            log.warn("plug-in " + descr.getId() + " not found in active plug-ins list, skipping deactivation");
            return;
        }
        try {
            if (plugin.isActive()) {
                plugin.stop();
            }
            log.info("plug-in stopped - " + descr.getId());
        } catch (Exception e) {
            log.error("error while stopping plug-in " + descr.getId(), e);
        }
        activePlugins.remove(descr.getId());
    }
    
    private void dump() {
        if (!DUMP_ENABLED || !log.isDebugEnabled()) {
            return;
        }
        StringBuffer buf = new StringBuffer("PLUGIN MANAGER DUMP:\r\n");
        buf.append("-------------- DUMP BEGIN -----------------\r\n");
        buf.append("\tActive plug-ins: " + activePlugins.size()).append("\r\n");
        for (Iterator it = activePlugins.values().iterator(); it.hasNext();) {
            buf.append("\t\t")
                .append(it.next())
                .append("\r\n");
        }
        buf.append("\tActivating plug-ins: "
                + activatingPlugins.size()).append("\r\n");
        for (Iterator it = activatingPlugins.iterator(); it.hasNext();) {
            buf.append("\t\t")
                .append(it.next())
                .append("\r\n");
        }
        buf.append("\tBad plug-ins: " + badPlugins.size()).append("\r\n");
        for (Iterator it = badPlugins.iterator(); it.hasNext();) {
            buf.append("\t\t")
                .append(it.next())
                .append("\r\n");
        }
        buf.append("\tActivation log: " + activationLog.size()).append("\r\n");
        for (Iterator it = activationLog.iterator(); it.hasNext();) {
            buf.append("\t\t")
                .append(it.next())
                .append("\r\n");
        }
        buf.append("Memory TOTAL/FREE/MAX: ")
            .append(Runtime.getRuntime().totalMemory())
            .append("/")
            .append(Runtime.getRuntime().freeMemory())
            .append("/")
            .append(Runtime.getRuntime().maxMemory())
            .append("\r\n");
        buf.append("-------------- DUMP END -----------------");
        log.debug(buf.toString());
    }
}
