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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.java.plugin.Documentation;
import org.java.plugin.PluginAttribute;
import org.java.plugin.PluginDescriptor;
import org.java.plugin.PluginRegistry;
import org.java.plugin.Version;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version $Id: PluginDescriptorImpl.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 */
class PluginDescriptorImpl extends IdentityImpl implements PluginDescriptor {
    private final PluginRegistryImpl registry;
    private String vendor;
    private Version version;
    private String pluginClassName;
    private String docsPath;
    private Map pluginPrerequisites;
    private Map libraries;
    private Map extensionPoints;
    private Map extensions;
    private DocumentationImpl doc;
    private List fragments;
    private List attributes;
    private final URL location;

    PluginDescriptorImpl(PluginRegistryImpl registry, Node node, URL location) throws Exception {
        super(node);
        this.registry = registry;
        this.location = location;
        vendor = XmlUtil.getAttribute(node, "vendor");
        if (vendor == null) {
            vendor = "";
        }
        String versionStr = XmlUtil.getAttribute(node, "version");
        if (versionStr == null) {
            throw new Exception("plug-in version is NULL");
        }
        version = Version.parse(versionStr);
        pluginClassName = XmlUtil.getAttribute(node, "class");
        if ((pluginClassName != null) && "".equals(pluginClassName.trim())) {
            pluginClassName = null;
        }
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
        
        attributes = new LinkedList();
        fragments = new LinkedList();
        pluginPrerequisites = new HashMap();
        libraries = new HashMap();
        extensionPoints = new HashMap();
        extensions = new HashMap();
        
        processAttributes(null, node);
        processPrerequisites(null, node);
        processLibraries(null, node);
        processExtensionPoints(null, node);
        processExtensions(null, node);
    }
    
    void registerFragment(PluginFragmentImpl fragment, Node node) throws Exception {
        fragments.add(fragment);
        processAttributes(fragment, node);
        processPrerequisites(fragment, node);
        processLibraries(fragment, node);
        processExtensionPoints(fragment, node);
        processExtensions(fragment, node);
    }
    
    private void processAttributes(PluginFragmentImpl fragment, Node node) throws Exception {
        NodeList nodes = XmlUtil.getNodeList(node, "attributes/attribute");
        for (int i = 0; i < nodes.getLength(); i++) {
            attributes.add(new PluginAttributeImpl(this, fragment, nodes.item(i)));
        }
    }
    
    private void processPrerequisites(PluginFragmentImpl fragment, Node node) throws Exception {
        NodeList importNodes = XmlUtil.getNodeList(node, "requires/import");
        for (int i = 0; i < importNodes.getLength(); i++) {
            PluginPrerequisiteImpl pluginPrerequisite =
                new PluginPrerequisiteImpl(this, fragment, importNodes.item(i));
            if (pluginPrerequisites.containsKey(
                    pluginPrerequisite.getPluginId())) {
                throw new Exception("duplicate imports of plug-in "
                    + pluginPrerequisite.getPluginId()
                    + " found in plug-in " + getId());
            }
            pluginPrerequisites.put(pluginPrerequisite.getPluginId(),
                pluginPrerequisite);
        }
        //pluginPrerequisites = Collections.unmodifiableMap(pluginPrerequisites);
    }
    
    private void processLibraries(PluginFragmentImpl fragment, Node node)
            throws Exception {
        NodeList libNodes = XmlUtil.getNodeList(node, "runtime/library");
        for (int i = 0; i < libNodes.getLength(); i++) {
            LibraryImpl lib =
                new LibraryImpl(this, fragment, libNodes.item(i));
            if (libraries.containsKey(lib.getId())) {
                throw new Exception("duplicate library ID "
                    + lib.getId() + " found in plug-in " + getId());
            }
            libraries.put(lib.getId(), lib);
        }
        //libraries = Collections.unmodifiableList(libraries);
    }
    
    private void processExtensionPoints(PluginFragmentImpl fragment, Node node) throws Exception {
        NodeList extPointNodes = XmlUtil.getNodeList(node, "extension-point");
        for (int i = 0; i < extPointNodes.getLength(); i++) {
            ExtensionPointImpl extensionPoint =
                new ExtensionPointImpl(this, fragment, extPointNodes.item(i));
            if (extensionPoints.containsKey(extensionPoint.getId())) {
                throw new Exception("duplicate extension point ID "
                    + extensionPoint.getId() + " found in plug-in " + getId());
            }
            extensionPoints.put(extensionPoint.getId(), extensionPoint);
        }
        //extensionPoints = Collections.unmodifiableMap(extensionPoints);
    }
    
    private void processExtensions(PluginFragmentImpl fragment, Node node) throws Exception {
        NodeList extNodes = XmlUtil.getNodeList(node, "extension");
        for (int i = 0; i < extNodes.getLength(); i++) {
            ExtensionImpl extension = new ExtensionImpl(this, fragment,
                    extNodes.item(i));
            if (extensions.containsKey(extension.getId())) {
                throw new Exception("duplicate extension ID "
                    + extension.getId() + " found in plug-in " + getId());
            }
            if (!getId().equals(extension.getExtendedPluginId())
                    && !pluginPrerequisites.containsKey(
                            extension.getExtendedPluginId())) {
                throw new Exception("plug-in " + extension.getExtendedPluginId()
                        + " declared in extension " + extension.getId()
                        + " not found in prerequisites in plug-in " + getId());
            }
            extensions.put(extension.getId(), extension);
        }
        //extensions = Collections.unmodifiableMap(extensions);
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getUniqueId()
     */
    public String getUniqueId() {
        return PluginRegistryImpl.makeUniqueId(getId(), version);
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getVendor()
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getVersion()
     */
    public Version getVersion() {
        return version;
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getPrerequisites()
     */
    public Collection getPrerequisites() {
        return Collections.unmodifiableCollection(pluginPrerequisites.values());
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getExtensionPoints()
     */
    public Collection getExtensionPoints() {
        return Collections.unmodifiableCollection(extensionPoints.values());
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getExtensions()
     */
    public Collection getExtensions() {
        return Collections.unmodifiableCollection(extensions.values());
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getLibraries()
     */
    public Collection getLibraries() {
        return Collections.unmodifiableCollection(libraries.values());
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getRegistry()
     */
    public PluginRegistry getRegistry() {
        return registry;
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getPluginClassName()
     */
    public String getPluginClassName() {
        return pluginClassName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "{PluginDescriptor: uid=" + getUniqueId() + "}";
    }

    /**
     * @see org.java.plugin.Documentable#getDocumentation()
     */
    public Documentation getDocumentation() {
        return doc;
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getFragments()
     */
    public Collection getFragments() {
        return Collections.unmodifiableCollection(fragments);
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getAttribute(java.lang.String)
     */
    public PluginAttribute getAttribute(String id) {
        PluginAttributeImpl result = null;
        for (Iterator it = attributes.iterator(); it.hasNext();) {
            PluginAttributeImpl attr = (PluginAttributeImpl)it.next();
            if (attr.getId().equals(id)) {
                if (result == null) {
                    result = attr;
                } else {
                    throw new IllegalArgumentException(
                        "more than one attribute with ID " + id
                        + " defined in plug-in " + getUniqueId());
                }
            }
        }
        /*
        if (result == null) {
            throw new IllegalArgumentException("attribute with ID " + id
                + " not found in plug-in " + getUniqueId());
        }
        */
        return result;
    }
    
    /**
     * @see org.java.plugin.PluginDescriptor#getAttributes()
     */
    public Collection getAttributes() {
        return Collections.unmodifiableCollection(attributes);
    }
    
    /**
     * @see org.java.plugin.PluginDescriptor#getAttributes(java.lang.String)
     */
    public PluginAttribute[] getAttributes(String id) {
        List result = new LinkedList();
        for (Iterator it = attributes.iterator(); it.hasNext();) {
            PluginAttributeImpl param = (PluginAttributeImpl)it.next();
            if (param.getId().equals(id)) {
                result.add(param);
            }
        }
        return (PluginAttributeImpl[])result.toArray(new PluginAttributeImpl[result.size()]);
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getDocsPath()
     */
    public String getDocsPath() {
        return docsPath;
    }

    /**
     * @see org.java.plugin.PluginDescriptor#getLocation()
     */
    public URL getLocation() {
        return location;
    }
}
