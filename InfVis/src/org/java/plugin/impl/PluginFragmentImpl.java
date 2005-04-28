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
package org.java.plugin.impl;

import java.net.URL;

import org.java.plugin.Documentation;
import org.java.plugin.PluginDescriptor;
import org.java.plugin.PluginFragment;
import org.java.plugin.PluginRegistry;
import org.java.plugin.Version;
import org.w3c.dom.Node;

/**
 * @version $Id: PluginFragmentImpl.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 */
class PluginFragmentImpl extends IdentityImpl implements PluginFragment {
    private Version version;
    private final PluginRegistryImpl registry;
    private String vendor;
    private DocumentationImpl doc;
    private String pluginId;
    private Version pluginVersion;
    private String match;
    private String docsPath;
    private final URL location;

    PluginFragmentImpl(PluginRegistryImpl registry, Node node, URL location)
            throws Exception {
        super(node);
        this.registry = registry;
        this.location = location;
        vendor = XmlUtil.getAttribute(node, "vendor");
        if (vendor == null) {
            vendor = "";
        }
        String versionStr = XmlUtil.getAttribute(node, "version");
        if (versionStr == null) {
            throw new Exception("plug-in fragment version is NULL");
        }
        version = Version.parse(versionStr);
        pluginId = XmlUtil.getAttribute(node, "plugin-id");
        if ((pluginId == null) || "".equals(pluginId.trim())) {
            throw new Exception("prerequisite plug-in ID is blank in plug-in fragment " + getId());
        }
        if (getId().equals(pluginId)) {
            throw new Exception("prerequisite plug-in ID equals to declaring ID in plug-in fragment " + getId());
        }
        versionStr = XmlUtil.getAttribute(node, "plugin-version");
        if (versionStr != null) {
            pluginVersion = Version.parse(versionStr);
        }
        match = XmlUtil.getAttribute(node, "match");
        docsPath = XmlUtil.getAttribute(node, "docs-path");
        if ((docsPath == null) || "".equals(docsPath.trim())) {
            docsPath = "";
        }
        /*
        docsPath = docsPath.replace('\\', '/');
        if (docsPath.startsWith("/")) {
            docsPath = docsPath.substring(1);
        }
        */
        if (XmlUtil.isNodeExists(node, "doc")) {
            doc = new DocumentationImpl(this, XmlUtil.getNode(node, "doc"));
        }
    }

    /**
     * @see org.java.plugin.PluginFragment#getUniqueId()
     */
    public String getUniqueId() {
        return PluginRegistryImpl.makeUniqueId(getId(), version);
    }

    /**
     * @see org.java.plugin.PluginFragment#getVendor()
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @see org.java.plugin.PluginFragment#getVersion()
     */
    public Version getVersion() {
        return version;
    }

    /**
     * @see org.java.plugin.PluginFragment#getPluginId()
     */
    public String getPluginId() {
        return pluginId;
    }

    /**
     * @see org.java.plugin.PluginFragment#getPluginVersion()
     */
    public Version getPluginVersion() {
        return pluginVersion;
    }

    /**
     * @see org.java.plugin.PluginFragment#getRegistry()
     */
    public PluginRegistry getRegistry() {
        return registry;
    }

    /**
     * @see org.java.plugin.PluginFragment#matches(org.java.plugin.PluginDescriptor)
     */
    public boolean matches(PluginDescriptor descr) {
        return PluginPrerequisiteImpl.matches(pluginVersion, descr.getVersion(), match);
    }

    /**
     * @see org.java.plugin.Documentable#getDocumentation()
     */
    public Documentation getDocumentation() {
        return doc;
    }

    /**
     * @see org.java.plugin.PluginFragment#getDocsPath()
     */
    public String getDocsPath() {
        return docsPath;
    }

    /**
     * @see org.java.plugin.PluginFragment#getLocation()
     */
    public URL getLocation() {
        return location;
    }
}
