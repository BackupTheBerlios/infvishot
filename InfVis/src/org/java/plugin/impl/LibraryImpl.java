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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.java.plugin.Documentation;
import org.java.plugin.Library;
import org.java.plugin.PluginDescriptor;
import org.java.plugin.PluginFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version $Id: LibraryImpl.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 */
class LibraryImpl extends IdentityImpl implements Library {
    private final PluginDescriptorImpl descriptor;
    private final PluginFragmentImpl fragment;
    private String path;
    private boolean isCodeLibrary;
    private List exports; // <String>
    private String docsPath;
    private DocumentationImpl doc;

    LibraryImpl(PluginDescriptorImpl descr, PluginFragmentImpl fragment,
            Node node) throws Exception {
        super(node);
        this.descriptor = descr;
        this.fragment = fragment;
        path = XmlUtil.getAttribute(node, "path");
        if ((path == null) || "".equals(path.trim())) {
            throw new Exception("library path is blank");
        }
        /*
        path = path.replace('\\', '/');
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        */
        isCodeLibrary = !"resources".equals(XmlUtil.getAttribute(node, "type"));
        NodeList nodes = XmlUtil.getNodeList(node, "export");
        exports = new LinkedList();
        for (int i = 0; i < nodes.getLength(); i++) {
            String exportPrefix = XmlUtil.getAttribute(nodes.item(i), "prefix");
            if ((exportPrefix == null) || "".equals(exportPrefix.trim())) {
                throw new Exception("export prefix is blank");
            }
            exportPrefix = exportPrefix.replace('\\', '.').replace('/', '.');
            if (exportPrefix.startsWith(".")) {
                exportPrefix = exportPrefix.substring(1);
            }
            exports.add(exportPrefix);
        }
        exports = Collections.unmodifiableList(exports);
        if (XmlUtil.isNodeExists(node, "doc")) {
            doc = new DocumentationImpl(this, XmlUtil.getNode(node, "doc"));
        }
    }

    /**
     * @see org.java.plugin.Library#getPath()
     */
    public String getPath() {
        return path;
    }

    /**
     * @see org.java.plugin.PluginElement#getDeclaringPluginDescriptor()
     */
    public PluginDescriptor getDeclaringPluginDescriptor() {
        return descriptor;
    }

    /**
     * @see org.java.plugin.PluginElement#getDeclaringPluginFragment()
     */
    public PluginFragment getDeclaringPluginFragment() {
        return fragment;
    }

    /**
     * @see org.java.plugin.Library#getExports()
     */
    public Collection getExports() {
        return exports;
    }

    /**
     * @see org.java.plugin.Library#isCodeLibrary()
     */
    public boolean isCodeLibrary() {
        return isCodeLibrary;
    }

    String getUniqueId() {
        return PluginRegistryImpl.makeUniqueId(descriptor.getId(), getId());
    }

    /**
     * @see org.java.plugin.Documentable#getDocumentation()
     */
    public Documentation getDocumentation() {
        return doc;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "{Library: uid=" + getUniqueId() + "}";
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
}
