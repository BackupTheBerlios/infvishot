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
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @version $Id: PluginResourceLoader.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
 */
class PluginResourceLoader extends URLClassLoader {
    private static final Log log = LogFactory.getLog(PluginResourceLoader.class);

    static PluginResourceLoader get(PluginManager manager, PluginDescriptor descr) {
        List urls = new LinkedList();
        for (Iterator it = descr.getLibraries().iterator(); it.hasNext();) {
            Library lib = (Library)it.next();
            if (lib.isCodeLibrary()) {
                continue;
            }
            urls.add(manager.getPathResolver().resolvePath(lib, lib.getPath()));
        }
        if (PluginManager.DUMP_ENABLED && log.isDebugEnabled()) {
            StringBuffer buf = new StringBuffer();
            buf.append("Resource URL's populated for plug-in " + descr + ":\r\n");
            for (Iterator it = urls.iterator(); it.hasNext();) {
                buf.append("\t");
                buf.append(it.next());
                buf.append("\r\n");
            }
            log.debug(buf.toString());
        }
        if (urls.isEmpty()) {
            return null;
        }
        return new PluginResourceLoader((URL[])urls.toArray(new URL[urls.size()]));
    }

    /**
     * Creates loader instance configured to load resources only from given URLs.
     * @param urls array of resource URLs
     */
    public PluginResourceLoader(URL[] urls) {
        super(urls);
    }

    /**
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    protected Class findClass(String name) throws ClassNotFoundException {
        throw new ClassNotFoundException(name);
    }

    /**
     * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
     */
    protected Class loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        throw new ClassNotFoundException(name);
    }
}
