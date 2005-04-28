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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.Identity;
import org.w3c.dom.Node;

/**
 * @version $Id: IdentityImpl.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 */
abstract class IdentityImpl implements Identity {
    /**
     * Makes logging service available for descending classes.
     */
    protected final Log log = LogFactory.getLog(getClass());

    private String id;

    /**
     * Constructs object from provided XML node.
     * @param node plug-in element node
     * @throws Exception
     */
    protected IdentityImpl(Node node) throws Exception {
        id = XmlUtil.getAttribute(node, "id");
        if ((id == null) || "".equals(id.trim())) {
            throw new Exception("ID attribute is blank in node " + node.getNodeName());
        }
    }

    /**
     * @see org.java.plugin.Identity#getId()
     */
    public String getId() {
        return id;
    }
}
