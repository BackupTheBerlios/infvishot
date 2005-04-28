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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.java.plugin.Documentation;
import org.java.plugin.PluginAttribute;
import org.java.plugin.PluginDescriptor;
import org.java.plugin.PluginFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version $Id: PluginAttributeImpl.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 */
class PluginAttributeImpl extends IdentityImpl implements PluginAttribute {
    private final PluginDescriptorImpl descriptor;
    private final PluginFragmentImpl fragment;
    private String value;
    private List subAttributes;
    private String docsPath;
    private DocumentationImpl doc;

    PluginAttributeImpl(PluginDescriptorImpl descr, PluginFragmentImpl fragment,
            Node node) throws Exception {
        super(node);
        this.descriptor = descr;
        this.fragment = fragment;
        value = XmlUtil.getAttribute(node, "value");
        if (value == null) {
            value = "";
        }
        NodeList nodes = XmlUtil.getNodeList(node, "attribute");
        subAttributes = new ArrayList(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            subAttributes.add(new PluginAttributeImpl(descr, fragment,
                    nodes.item(i)));
        }
        subAttributes = Collections.unmodifiableList(subAttributes);
        if (XmlUtil.isNodeExists(node, "doc")) {
            doc = new DocumentationImpl(this, XmlUtil.getNode(node, "doc"));
        }
    }

    /**
     * @see org.java.plugin.PluginAttribute#getDeclaringPluginDescriptor()
     */
    public PluginDescriptor getDeclaringPluginDescriptor() {
        return descriptor;
    }
    
    /**
     * @see org.java.plugin.PluginAttribute#getDeclaringPluginFragment()
     */
    public PluginFragment getDeclaringPluginFragment() {
        return fragment;
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
     * @see org.java.plugin.PluginAttribute#getSubAttribute(java.lang.String)
     */
    public PluginAttribute getSubAttribute(String id) {
        PluginAttributeImpl result = null;
        for (Iterator it = subAttributes.iterator(); it.hasNext();) {
            PluginAttributeImpl param = (PluginAttributeImpl)it.next();
            if (param.getId().equals(id)) {
                if (result == null) {
                    result = param;
                } else {
                    throw new IllegalArgumentException(
                        "more than one attribute with ID " + id
                        + " defined in plug-in " + descriptor.getUniqueId());
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
     * @see org.java.plugin.PluginAttribute#getSubAttributes()
     */
    public Collection getSubAttributes() {
        return subAttributes;
    }
    
    /**
     * @see org.java.plugin.PluginAttribute#getSubAttributes(java.lang.String)
     */
    public PluginAttribute[] getSubAttributes(String id) {
        List result = new LinkedList();
        for (Iterator it = subAttributes.iterator(); it.hasNext();) {
            PluginAttributeImpl param = (PluginAttributeImpl)it.next();
            if (param.getId().equals(id)) {
                result.add(param);
            }
        }
        return (PluginAttributeImpl[])result.toArray(new PluginAttributeImpl[result.size()]);
    }
    
    /**
     * @see org.java.plugin.PluginAttribute#getValue()
     */
    public String getValue() {
        return value;
    }
    
    /**
     * @see org.java.plugin.Documentable#getDocumentation()
     */
    public Documentation getDocumentation() {
        return doc;
    }
}
