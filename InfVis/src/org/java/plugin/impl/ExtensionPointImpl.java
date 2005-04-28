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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.java.plugin.Documentation;
import org.java.plugin.Extension;
import org.java.plugin.ExtensionPoint;
import org.java.plugin.PluginDescriptor;
import org.java.plugin.PluginFragment;
import org.java.plugin.IntegrityCheckReport.ReportItem;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version $Id: ExtensionPointImpl.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 */
class ExtensionPointImpl extends IdentityImpl implements ExtensionPoint {
    private final PluginDescriptorImpl descriptor;
    private final PluginFragmentImpl fragment;
    private Map connectedExtensions;
    private List parameterDefinitions;
    private DocumentationImpl doc;
    private String multiplicity;
    private Boolean isValid;
    private String docsPath;

    ExtensionPointImpl(PluginDescriptorImpl descr, PluginFragmentImpl fragment,
            Node node) throws Exception {
        super(node);
        this.descriptor = descr;
        this.fragment = fragment;
        multiplicity = XmlUtil.getAttribute(node, "extension-multiplicity");
        NodeList nodes = XmlUtil.getNodeList(node, "parameter-def");
        parameterDefinitions = new ArrayList(nodes.getLength());
        Set names = new HashSet();
        for (int i = 0; i < nodes.getLength(); i++) {
            ParameterDefinitionImpl def = new ParameterDefinitionImpl(null, nodes.item(i));
            if (names.contains(def.getId())) {
                throw new Exception("duplicate parameter definition name attribute "
                    + def.getId() + " found for extension point "
                    + getId() + " in plug-in " + descr.getId());
            }
            names.add(def.getId());
            parameterDefinitions.add(def);
        }
        parameterDefinitions = Collections.unmodifiableList(parameterDefinitions);
        if (XmlUtil.isNodeExists(node, "doc")) {
            doc = new DocumentationImpl(this, XmlUtil.getNode(node, "doc"));
        }
    }

    /**
     * @see org.java.plugin.ExtensionPoint#getUniqueId()
     */
    public String getUniqueId() {
        return PluginRegistryImpl.makeUniqueId(descriptor.getId(), getId());
    }
    
    /**
     * @see org.java.plugin.ExtensionPoint#getMultiplicity()
     */
    public String getMultiplicity() {
        return multiplicity;
    }

    private void collectConnectedExtensions() {
        connectedExtensions = new HashMap();
        for (Iterator it = descriptor.getRegistry().getPluginDescriptors().iterator(); it.hasNext();) {
            PluginDescriptor descr = (PluginDescriptor)it.next();
            for (Iterator it2 = descr.getExtensions().iterator(); it2.hasNext();) {
                Extension ext = (Extension)it2.next();
                if (getId().equals(ext.getExtendedPointId())) {
                    if (ext.isValid()) {
                        connectedExtensions.put(ext.getUniqueId(), ext);
                    } else {
                        log.warn("extension " + ext.getUniqueId() + " is invalid and doesn't connected to extension point " + getUniqueId());
                    }
                }
            }
        }
        connectedExtensions = Collections.unmodifiableMap(connectedExtensions);
    }

    /**
     * @see org.java.plugin.ExtensionPoint#getConnectedExtensions()
     */
    public Collection getConnectedExtensions() {
        if (connectedExtensions == null) {
            collectConnectedExtensions();
        }
        return connectedExtensions.values();
    }

    /**
     * @see org.java.plugin.ExtensionPoint#getConnectedExtension(java.lang.String)
     */
    public Extension getConnectedExtension(String uniqueId) {
        if (connectedExtensions == null) {
            collectConnectedExtensions();
        }
        Extension result = (Extension)connectedExtensions.get(uniqueId);
        if (result == null) {
            throw new IllegalArgumentException("extension " + uniqueId
                + " not connected to point " + getUniqueId());
        }
        return result;
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
     * @see org.java.plugin.ExtensionPoint#isValid()
     */
    public boolean isValid() {
        if (isValid == null) {
            validate();
        }
        return isValid.booleanValue();
    }
    
    Collection validate() {
        List result = new LinkedList();
        if (EXT_MULT_ANY.equals(getMultiplicity())) {
            isValid = Boolean.TRUE;
        } else if (EXT_MULT_ONE.equals(getMultiplicity())) {
            isValid = Boolean.valueOf(getConnectedExtensions().size() == 1);
            if (!isValid.booleanValue()) {
                result.add(new IntegrityChecker.ReportItemImpl(
                        ReportItem.SEVERITY_ERROR, this,
                        ReportItem.ERROR_INVALID_EXTENSION_POINT,
                        "too many or too few extensions connected to extension point "
                        + getUniqueId()));
            }
        } else {
            // MULT_ONE_PER_PLUGIN case
            isValid = Boolean.TRUE;
            Set foundPlugins = new HashSet();
            for (Iterator it = getConnectedExtensions().iterator(); it.hasNext();) {
                String pluginId = ((Extension)it.next())
                        .getDeclaringPluginDescriptor().getId();
                if (!foundPlugins.add(pluginId)) {
                    isValid = Boolean.FALSE;
                    result.add(new IntegrityChecker.ReportItemImpl(
                            ReportItem.SEVERITY_ERROR, this,
                            ReportItem.ERROR_INVALID_EXTENSION_POINT,
                            "too many extensions connected to extension point "
                            + getUniqueId()));
                    break;
                }
            }
        }
        return result;
    }
    
    /**
     * @see org.java.plugin.ExtensionPoint#getParameterDefinitions()
     */
    public Collection getParameterDefinitions() {
        return parameterDefinitions;
    }
    
    /**
     * @see org.java.plugin.ExtensionPoint#getParameterDefinition(java.lang.String)
     */
    public ParameterDefinition getParameterDefinition(String id) {
        for (Iterator it = parameterDefinitions.iterator(); it.hasNext();) {
            ParameterDefinitionImpl def = (ParameterDefinitionImpl)it.next();
            if (def.getId().equals(id)) {
                return def;
            }
        }
        throw new IllegalArgumentException("parameter definition with ID " + id
            + " not found in extension point " + getUniqueId());
    }

    /**
     * @see org.java.plugin.Documentable#getDocumentation()
     */
    public Documentation getDocumentation() {
        return doc;
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
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "{ExtensionPoint: uid=" + getUniqueId() + "}";
    }

    private class ParameterDefinitionImpl extends IdentityImpl implements ParameterDefinition {
        private String type;
        private String defMultiplicity;
        private List subDefinitions;
        private DocumentationImpl defDoc;
        private final ParameterDefinitionImpl superDefinition;

        ParameterDefinitionImpl(ParameterDefinitionImpl superDefinition,
                Node node) throws Exception {
            super(node);
            this.superDefinition = superDefinition;
            type = XmlUtil.getAttribute(node, "type");
            checkType();
            defMultiplicity = XmlUtil.getAttribute(node, "multiplicity");
            checkMultiplicity();
            if (TYPE_ANY.equals(type)) {
                subDefinitions = Collections.EMPTY_LIST;
            } else {
                NodeList nodes = XmlUtil.getNodeList(node, "parameter-def");
                subDefinitions = new ArrayList(nodes.getLength());
                Set names = new HashSet();
                for (int i = 0; i < nodes.getLength(); i++) {
                    ParameterDefinitionImpl def = new ParameterDefinitionImpl(this, nodes.item(i));
                    if (names.contains(def.getId())) {
                        throw new Exception("duplicate parameter definition ID attribute "
                            + def.getId() + " found for extension point "
                            + ExtensionPointImpl.this.getId() + " in plug-in "
                            + ExtensionPointImpl.this.getDeclaringPluginDescriptor().getId());
                    }
                    names.add(def.getId());
                    subDefinitions.add(def);
                }
                subDefinitions = Collections.unmodifiableList(subDefinitions);
            }
            if (XmlUtil.isNodeExists(node, "doc")) {
                defDoc = new DocumentationImpl(this, XmlUtil.getNode(node, "doc"));
            }
        }
        
        private void checkType() throws Exception {
            if (!TYPE_STRING.equals(type)
                && !TYPE_BOOLEAN.endsWith(type)
                && !TYPE_NUMBER.equals(type)
                && !TYPE_DATE.equals(type)
                && !TYPE_TIME.equals(type)
                && !TYPE_DATETIME.equals(type)
                && !TYPE_NULL.equals(type)
                && !TYPE_ANY.equals(type)
                && !TYPE_PLUGIN_ID.equals(type)
                && !TYPE_EXTENSION_POINT_ID.equals(type)
                && !TYPE_EXTENSION_ID.equals(type)) {
            throw new Exception("parameter definition type attribute "
                + type + " is invalid for extension point "
                + ExtensionPointImpl.this.getId() + " in plug-in "
                + ExtensionPointImpl.this.getDeclaringPluginDescriptor().getId());
            }
        }

        private void checkMultiplicity() throws Exception {
            if (!MULT_ONE.equals(defMultiplicity)
                && !MULT_ANY.equals(defMultiplicity)
                && !MULT_NONE_OR_ONE.equals(defMultiplicity)
                && !MULT_ONE_OR_MORE.equals(defMultiplicity)) {
                throw new Exception("parameter definition multiplicity attribute "
                    + defMultiplicity + " is invalid for extension point "
                    + ExtensionPointImpl.this.getId() + " in plug-in "
                    + ExtensionPointImpl.this.getDeclaringPluginDescriptor().getId());
            }
        }

        /**
         * @see org.java.plugin.ExtensionPoint.ParameterDefinition#getDeclaringExtensionPoint()
         */
        public ExtensionPoint getDeclaringExtensionPoint() {
            return ExtensionPointImpl.this;
        }

        /**
         * @see org.java.plugin.PluginElement#getDeclaringPluginDescriptor()
         */
        public PluginDescriptor getDeclaringPluginDescriptor() {
            return ExtensionPointImpl.this.getDeclaringPluginDescriptor();
        }

        /**
         * @see org.java.plugin.PluginElement#getDeclaringPluginFragment()
         */
        public PluginFragment getDeclaringPluginFragment() {
            return ExtensionPointImpl.this.getDeclaringPluginFragment();
        }

        /**
         * @see org.java.plugin.Documentable#getDocsPath()
         */
        public String getDocsPath() {
            return ExtensionPointImpl.this.getDocsPath();
        }

        /**
         * @see org.java.plugin.ExtensionPoint.ParameterDefinition#getMultiplicity()
         */
        public String getMultiplicity() {
            return defMultiplicity;
        }

        /**
         * @see org.java.plugin.ExtensionPoint.ParameterDefinition#getSubDefinitions()
         */
        public Collection getSubDefinitions() {
            return subDefinitions;
        }

        /**
         * @see org.java.plugin.ExtensionPoint.ParameterDefinition#getSuperDefinition()
         */
        public ParameterDefinition getSuperDefinition() {
            return superDefinition;
        }
        
        /**
         * @see org.java.plugin.ExtensionPoint.ParameterDefinition#getSubDefinition(java.lang.String)
         */
        public ParameterDefinition getSubDefinition(String id) {
            for (Iterator it = subDefinitions.iterator(); it.hasNext();) {
                ParameterDefinitionImpl def = (ParameterDefinitionImpl)it.next();
                if (def.getId().equals(id)) {
                    return def;
                }
            }
            throw new IllegalArgumentException("parameter definition with ID " + id
                + " not found in extension point " + getUniqueId());
        }

        /**
         * @see org.java.plugin.ExtensionPoint.ParameterDefinition#getType()
         */
        public String getType() {
            return type;
        }

        /**
         * @see org.java.plugin.Documentable#getDocumentation()
         */
        public Documentation getDocumentation() {
            return defDoc;
        }
        
        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "{PluginExtensionPoint.ParameterDefinition: extPointUid=" + getDeclaringExtensionPoint().getUniqueId() +"; id=" + getId() + "}";
        }
    }
}
