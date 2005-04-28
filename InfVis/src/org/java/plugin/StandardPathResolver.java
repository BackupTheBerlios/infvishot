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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Standard simple implementation of path resolver. For resolving it uses
 * plug-in element registration (see {@link #registerContext(Identity, URL)})
 * procedure.
 * @version $Id: StandardPathResolver.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
 */
public class StandardPathResolver implements PathResolver {
    /**
     * Makes logging service available for descending classes.
     */
    protected final Log log = LogFactory.getLog(getClass());

    private Map urlMap = new HashMap(); // <pluginUniqueId or fragmentUniqueId, home URI>
    
    /**
     * Registers "home" URL for given {@link PluginDescriptor} or
     * {@link PluginFragment}.
     * @param idt {@link PluginDescriptor} or {@link PluginFragment}
     * @param url "home" URL for a given plug-in or plug-in fragment
     */
    public void registerContext(Identity idt, URL url) {
        String uid;
        if (idt instanceof PluginDescriptor) {
            uid = ((PluginDescriptor)idt).getUniqueId();
        } else if (idt instanceof PluginFragment) {
            uid = ((PluginFragment)idt).getUniqueId();
        } else {
            throw new IllegalArgumentException("unsupported identity class "
                    + idt.getClass().getName());
        }
        urlMap.put(uid, url);
        log.debug("context URL " + url + " registered for " + idt + " with key " + uid);
    }

    /**
     * @see org.java.plugin.PathResolver#resolvePath(org.java.plugin.Identity, java.lang.String)
     */
    public URL resolvePath(Identity identity, String path) {
        PluginDescriptor descr = null;
        PluginFragment fragment = null;
        if (identity instanceof PluginDescriptor) {
            descr = (PluginDescriptor)identity;
        } else if (identity instanceof PluginFragment) {
            fragment = (PluginFragment)identity;
        } else if (identity instanceof PluginElement) {
            PluginElement element = (PluginElement)identity;
            descr = element.getDeclaringPluginDescriptor();
            fragment = element.getDeclaringPluginFragment();
        } else {
            throw new IllegalArgumentException("unknown identity class "
                    + identity.getClass().getName());
        }
        if ((descr == null) && (fragment == null)) {
            throw new IllegalArgumentException("given identity " + identity
                    + " doesn't contains references to plug-in descriptor"
                    + " or plug-in fragment");
        }
        return resolvePath((fragment != null)
                ? fragment.getUniqueId() : descr.getUniqueId(), path);
    }

    /**
     * Resolves given path against plug-in or plug-in fragment home
     * (context) folder.
     * @param uniqueId unique ID of plug-in or plug-in fragment
     * @param path path to be resolved
     * @return resolved URL
     */
    protected URL resolvePath(String uniqueId, String path) {
        URL homeUrl = (URL)urlMap.get(uniqueId);
        if (homeUrl == null) {
            throw new IllegalArgumentException("unknown plug-in or" +
                    " plug-in fragment unique ID - " + uniqueId);
        }
        if ("".equals(path) || "/".equals(path)) {
            return homeUrl;
        }
        /*
        path = path.replace('\\', '/');
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        */
        try {
            return new URL(homeUrl, path);
        } catch (MalformedURLException mue) {
            log.error("can't create URL in context of " + homeUrl + " and path " + path, mue);
            throw new IllegalArgumentException("path " + path
                    + " in context of " + homeUrl
                    + " cause creation of malformed URL");
        }
    }

    /**
     * @see org.java.plugin.PathResolver#isResourceExists(java.net.URL)
     */
    public boolean isResourceExists(URL url) {
        try {
            url.openConnection().connect();
            log.debug("resource " + url + " exists");
            return true;
        } catch (IOException ioe) {
            log.debug("resource " + url + " doesn't exist, error - " + ioe);
            return false;
        }
    }
}
