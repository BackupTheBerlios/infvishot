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

import org.w3c.dom.Node;

import org.java.plugin.Documentation;
import org.java.plugin.PluginDescriptor;
import org.java.plugin.PluginFragment;
import org.java.plugin.PluginPrerequisite;
import org.java.plugin.Version;

/**
 * @version $Id: PluginPrerequisiteImpl.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 */
class PluginPrerequisiteImpl implements PluginPrerequisite {
    static boolean matches(Version source, Version target, String match) {
        if (source == null) {
            return true;
        }
        if (MATCH_EQUAL.equals(match)) {
            return target.equals(source);
        } else if (MATCH_EQUIVALENT.equals(match)) {
            return target.isEquivalentTo(source);
        } else if (MATCH_COMPATIBLE.equals(match)) {
            return target.isCompatibleWith(source);
        } else if (MATCH_GREATER_OR_EQUAL.equals(match)) {
            return target.isGreaterOrEqualTo(source);
        }
        return target.isCompatibleWith(source);
    }
    
    private final PluginDescriptorImpl descriptor;
    private final PluginFragmentImpl fragment;
    private String pluginId;
    private boolean isExported;
    private boolean isOptional;
    private Version pluginVersion;
    private String match;
    private String docsPath;
    private DocumentationImpl doc;
    private String id;
    
    PluginPrerequisiteImpl(PluginDescriptorImpl descr,
            PluginFragmentImpl fragment, Node node) throws Exception {
        super();
        this.descriptor = descr;
        this.fragment = fragment;
        pluginId = XmlUtil.getAttribute(node, "plugin-id");
        if ((pluginId == null) || "".equals(pluginId.trim())) {
            throw new Exception("prerequisite plug-in ID is blank in plug-in " + descr.getId());
        }
        if (descr.getId().equals(pluginId)) {
            throw new Exception("prerequisite plug-in ID equals to declaring plug-in ID in plug-in " + descr.getId());
        }
        id = XmlUtil.getAttribute(node, "id");
        if ((id == null) || (id.length() == 0)) {
            id = "prerequisite:" + pluginId;
        }
        isExported = "true".equals(XmlUtil.getAttribute(node, "exported"));
        isOptional = "true".equals(XmlUtil.getAttribute(node, "optional"));
        String versionStr = XmlUtil.getAttribute(node, "plugin-version");
        if (versionStr != null) {
            pluginVersion = Version.parse(versionStr);
        }
        match = XmlUtil.getAttribute(node, "match");
        if (XmlUtil.isNodeExists(node, "doc")) {
            doc = new DocumentationImpl(this, XmlUtil.getNode(node, "doc"));
        }
    }

    /**
     * @see org.java.plugin.PluginPrerequisite#getPluginId()
     */
    public String getPluginId() {
        return pluginId;
    }

    /**
     * @see org.java.plugin.PluginPrerequisite#getPluginVersion()
     */
    public Version getPluginVersion() {
        return pluginVersion;
    }

    /**
     * @see org.java.plugin.PluginPrerequisite#getDeclaringPluginDescriptor()
     */
    public PluginDescriptor getDeclaringPluginDescriptor() {
        return descriptor;
    }

    /**
     * @see org.java.plugin.PluginPrerequisite#getDeclaringPluginFragment()
     */
    public PluginFragment getDeclaringPluginFragment() {
        return fragment;
    }

    /**
     * @see org.java.plugin.PluginPrerequisite#isOptional()
     */
    public boolean isOptional() {
        return isOptional;
    }

    /**
     * @see org.java.plugin.PluginPrerequisite#matches()
     */
    public boolean matches() {
        PluginDescriptor descr = null;
        try {
            descr = this.descriptor.getRegistry().getPluginDescriptor(pluginId);
        } catch (IllegalArgumentException iae) {
            return false;
        }
        return matches(pluginVersion, descr.getVersion(), match);
    }

    /**
     * @see org.java.plugin.PluginPrerequisite#isExported()
     */
    public boolean isExported() {
        return isExported;
    }

    /**
     * @see org.java.plugin.Identity#getId()
     */
    public String getId() {
        return id;
    }
    
    /**
     * @see org.java.plugin.Documentable#getDocsPath()
     */
    public String getDocsPath() {
        if (docsPath == null) {
            docsPath = (fragment != null) ? fragment.getDocsPath()
                    : descriptor.getDocsPath();
        }
        return docsPath;
    }
    
    /**
     * @see org.java.plugin.Documentable#getDocumentation()
     */
    public Documentation getDocumentation() {
        return doc;
    }

    String getUniqueId() {
        return PluginRegistryImpl.makeUniqueId(descriptor.getId(), getId());
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "{Prerequisite: uid=" + getUniqueId() + "}";
    }
}
