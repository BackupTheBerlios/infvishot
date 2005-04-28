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
import org.java.plugin.Identity;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version $Id: DocumentationImpl.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 */
class DocumentationImpl implements Documentation {
    private Identity identity;
    private String caption;
    private String text;
    private List references;
    
    DocumentationImpl(Identity identity, Node node) throws Exception {
        this.identity = identity;
        caption = XmlUtil.getAttribute(node, "caption");
        if ((caption == null) || "".equals(caption.trim())) {
            caption = "";
        }
        NodeList nodes = XmlUtil.getNodeList(node, "doc-ref");
        references = new LinkedList();
        for (int i = 0; i < nodes.getLength(); i++) {
            references.add(new ReferenceImpl(nodes.item(i)));
        }
        references = Collections.unmodifiableList(references);
        Node textNode = XmlUtil.getNode(node, "doc-text");
        text = (textNode == null) ? "" : XmlUtil.getValue(textNode);
    }

    /**
     * @see org.java.plugin.Documentation#getCaption()
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @see org.java.plugin.Documentation#getText()
     */
    public String getText() {
        return text;
    }

    /**
     * @see org.java.plugin.Documentation#getReferences()
     */
    public Collection getReferences() {
        return references;
    }

    /**
     * @see org.java.plugin.Documentation#getDeclaringIdentity()
     */
    public Identity getDeclaringIdentity() {
        return identity;
    }
    
    private static class ReferenceImpl implements Reference {
        private String caption;
        private String path;

        ReferenceImpl(Node node) {
            caption = XmlUtil.getAttribute(node, "caption");
            if ((caption == null) || "".equals(caption.trim())) {
                caption = "";
            }
            path = XmlUtil.getAttribute(node, "path");
            if ((path == null) || "".equals(path.trim())) {
                path = "";
            }
            /*
            path = path.replace('\\', '/');
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            */
        }

        /**
         * @see org.java.plugin.Documentation.Reference#getCaption()
         */
        public String getCaption() {
            return caption;
        }

        /**
         * @see org.java.plugin.Documentation.Reference#getRef()
         */
        public String getRef() {
            return path;
        }
    }
}
