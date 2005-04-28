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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.java.plugin.Documentation;
import org.java.plugin.Extension;
import org.java.plugin.ExtensionPoint;
import org.java.plugin.PluginDescriptor;
import org.java.plugin.PluginFragment;
import org.java.plugin.ExtensionPoint.ParameterDefinition;
import org.java.plugin.IntegrityCheckReport.ReportItem;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version $Id: ExtensionImpl.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 */
final class ExtensionImpl extends IdentityImpl implements Extension {
    private final PluginDescriptorImpl descriptor;
    private final PluginFragmentImpl fragment;
    private String extendedPluginId;
    private String extendedPointId;
    private List parameters;
    private Boolean isValid;
    private DocumentationImpl doc;
    private String docsPath;
    
    ExtensionImpl(PluginDescriptorImpl descr, PluginFragmentImpl fragment,
            Node node) throws Exception {
        super(node);
        this.descriptor = descr;
        this.fragment = fragment;
        extendedPluginId = XmlUtil.getAttribute(node, "plugin-id");
        if ((extendedPluginId == null) || "".equals(extendedPluginId.trim())) {
            throw new Exception("extension ID is blank in plug-in " + descr.getId());
        }
        extendedPointId = XmlUtil.getAttribute(node, "point-id");
        if ((extendedPointId == null) || "".equals(extendedPointId.trim())) {
            throw new Exception("extended point ID is blank in plug-in " + descr.getId());
        }
        NodeList nodes = XmlUtil.getNodeList(node, "parameter");
        parameters = new ArrayList(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            parameters.add(new ParameterImpl(null, nodes.item(i)));
        }
        parameters = Collections.unmodifiableList(parameters);
        if (XmlUtil.isNodeExists(node, "doc")) {
            doc = new DocumentationImpl(this, XmlUtil.getNode(node, "doc"));
        }
    }

    /**
     * @see org.java.plugin.Extension#getUniqueId()
     */
    public String getUniqueId() {
        return PluginRegistryImpl.makeUniqueId(descriptor.getId(), getId());
    }

    /**
     * @see org.java.plugin.Extension#getParameters()
     */
    public Collection getParameters() {
        return parameters;
    }

    /**
     * @see org.java.plugin.Extension#getParameter(java.lang.String)
     */
    public Parameter getParameter(String id) {
        ParameterImpl result = null;
        for (Iterator it = parameters.iterator(); it.hasNext();) {
            ParameterImpl param = (ParameterImpl)it.next();
            if (param.getId().equals(id)) {
                if (result == null) {
                    result = param;
                } else {
                    throw new IllegalArgumentException(
                        "more than one parameter with ID " + id
                        + " defined in extension " + getUniqueId());
                }
            }
        }
        /*
        if (result == null) {
            throw new IllegalArgumentException("parameter with ID " + id
                + " not found in extension " + getUniqueId());
        }
        */
        return result;
    }

    /**
     * @see org.java.plugin.Extension#getParameters(java.lang.String)
     */
    public Parameter[] getParameters(String id) {
        List result = new LinkedList();
        for (Iterator it = parameters.iterator(); it.hasNext();) {
            ParameterImpl param = (ParameterImpl)it.next();
            if (param.getId().equals(id)) {
                result.add(param);
            }
        }
        return (ParameterImpl[])result.toArray(new ParameterImpl[result.size()]);
    }

    /**
     * @see org.java.plugin.Extension#getExtendedPluginId()
     */
    public String getExtendedPluginId() {
        return extendedPluginId;
    }

    /**
     * @see org.java.plugin.Extension#getExtendedPointId()
     */
    public String getExtendedPointId() {
        return extendedPointId;
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
     * @see org.java.plugin.Extension#isValid()
     */
    public boolean isValid() {
        if (isValid == null) {
            validate();
        }
        return isValid.booleanValue();
    }
    
    Collection validate() {
        Collection result = validateParameters(getDeclaringPluginDescriptor().getRegistry().
                getExtensionPoint(getExtendedPluginId(), getExtendedPointId()).
                getParameterDefinitions(), parameters);
        isValid = result.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
        return result;
    }
    
    private Collection validateParameters(Collection allDefinitions,
            Collection allParams) {
        List result = new LinkedList();
        Map groups = new HashMap();
        for (Iterator it = allParams.iterator(); it.hasNext();) {
            Parameter param = (Parameter)it.next();
            ParameterDefinition def = param.getDefinition();
            if (def == null) {
                result.add(new IntegrityChecker.ReportItemImpl(
                        ReportItem.SEVERITY_ERROR, this,
                        ReportItem.ERROR_INVALID_EXTENSION,
                        "can't detect definition for parameter "
                        + param.getId() + " in extension " + getUniqueId()));
                continue;
            }
            if (groups.containsKey(param.getId())) {
                ((Collection)groups.get(param.getId())).add(param);
            } else {
                Collection paramGroup = new LinkedList();
                paramGroup.add(param);
                groups.put(param.getId(), paramGroup);
            }
        }
        if (!result.isEmpty()) {
            return result;
        }
        for (Iterator it = allDefinitions.iterator(); it.hasNext();) {
            ParameterDefinition def = (ParameterDefinition)it.next();
            Collection paramGroup = (Collection)groups.get(def.getId());
            result.addAll(validateParameters(def,
                    (paramGroup != null) ? paramGroup : Collections.EMPTY_LIST));
        }
        return result;
    }
    
    private Collection validateParameters(ParameterDefinition def, Collection params) {
        log.debug("validating parameters for definition " + def);
        if (ParameterDefinition.MULT_ONE.equals(def.getMultiplicity())
                && (params.size() != 1)) {
            return Collections.singletonList(new IntegrityChecker.ReportItemImpl(
                    ReportItem.SEVERITY_ERROR, this,
                    ReportItem.ERROR_INVALID_EXTENSION,
                    "too many or too few parameters " + def.getId() + " defined in extension " + getUniqueId()));
        } else if (ParameterDefinition.MULT_NONE_OR_ONE.equals(def.getMultiplicity())
                && (params.size() > 1)) {
            return Collections.singletonList(new IntegrityChecker.ReportItemImpl(
                    ReportItem.SEVERITY_ERROR, this,
                    ReportItem.ERROR_INVALID_EXTENSION,
                    "too many parameters " + def.getId() + " defined in extension " + getUniqueId()));
        } else if (ParameterDefinition.MULT_ONE_OR_MORE.equals(def.getMultiplicity())
                && params.isEmpty()) {
            return Collections.singletonList(new IntegrityChecker.ReportItemImpl(
                    ReportItem.SEVERITY_ERROR, this,
                    ReportItem.ERROR_INVALID_EXTENSION,
                    "too few parameters " + def.getId() + " defined in extension " + getUniqueId()));
        }
        if (params.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List result = new LinkedList();
        int count = 1;
        for (Iterator it = params.iterator(); it.hasNext(); count++) {
            ParameterImpl param = (ParameterImpl)it.next();
            if (!param.isValid()) {
                result.add(new IntegrityChecker.ReportItemImpl(
                        ReportItem.SEVERITY_ERROR, this,
                        ReportItem.ERROR_INVALID_EXTENSION,
                        "parameter " + def.getId() + " (" + count + "), defined in extension " + getUniqueId() + ", has invalid value"));
            }
            if (!ParameterDefinition.TYPE_ANY.equals(def.getType())
                    && result.isEmpty()) {
                result.addAll(validateParameters(
                        param.getDefinition().getSubDefinitions(),
                        param.getSubParameters()));
            }
        }
        return result;
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
        return "{PluginExtension: uid=" + getUniqueId() + "}";
    }

    private class ParameterImpl extends IdentityImpl implements Parameter {
        private String rawValue;
        private boolean rawValueExists;
        private Boolean isParamValid;
        private Object valueObj;
        private List subParameters;
        private DocumentationImpl paramDoc;
        private ParameterDefinition definition;
        private boolean definitionDetected = false;
        private final ParameterImpl superParameter;

        ParameterImpl(ParameterImpl superParameter, Node node) throws Exception {
            super(node);
            this.superParameter = superParameter;
            rawValue = XmlUtil.getAttribute(node, "value");
            if (rawValue == null) {
                if (XmlUtil.isNodeExists(node, "value")) {
                    rawValue = XmlUtil.getValue(node, "value");
                    rawValueExists = true;
                } else {
                    rawValue = "";
                    rawValueExists = false;
                }
            } else {
                rawValueExists = true;
            }
            if (XmlUtil.isNodeExists(node, "doc")) {
                paramDoc = new DocumentationImpl(this, XmlUtil.getNode(node, "doc"));
            }
            NodeList nodes = XmlUtil.getNodeList(node, "parameter");
            subParameters = new ArrayList(nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                subParameters.add(new ParameterImpl(this, nodes.item(i)));
            }
            subParameters = Collections.unmodifiableList(subParameters);
        }

        /**
         * @see org.java.plugin.Extension.Parameter#getDeclaringExtension()
         */
        public Extension getDeclaringExtension() {
            return ExtensionImpl.this;
        }

        /**
         * @see org.java.plugin.PluginElement#getDeclaringPluginDescriptor()
         */
        public PluginDescriptor getDeclaringPluginDescriptor() {
            return ExtensionImpl.this.getDeclaringPluginDescriptor();
        }
        
        /**
         * @see org.java.plugin.PluginElement#getDeclaringPluginFragment()
         */
        public PluginFragment getDeclaringPluginFragment() {
            return ExtensionImpl.this.getDeclaringPluginFragment();
        }

        /**
         * @see org.java.plugin.Extension.Parameter#getDefinition()
         */
        public ParameterDefinition getDefinition() {
            if (definitionDetected) {
                return definition;
            }
            log.debug("detecting definition for parameter " + this);
            Collection definitions;
            if (superParameter != null) {
                if (superParameter.getDefinition() == null) {
                    definitionDetected = true;
                    return null;
                }
                if (ParameterDefinition.TYPE_ANY.equals(
                            superParameter.getDefinition().getType())) {
                    definitionDetected = true;
                    definition = superParameter.getDefinition();
                    return definition;
                }
                definitions = superParameter.getDefinition().getSubDefinitions();
            } else {
                definitions = getDeclaringPluginDescriptor().getRegistry().
                    getExtensionPoint(getDeclaringExtension().getExtendedPluginId(),
                            getDeclaringExtension().getExtendedPointId()).
                                getParameterDefinitions();
            }
            for (Iterator it = definitions.iterator(); it.hasNext();) {
                ParameterDefinition def = (ParameterDefinition)it.next();
                if (def.getId().equals(getId())) {
                    definition = def;
                    break;
                }
            }
            definitionDetected = true;
            return definition;
        }

        /**
         * @see org.java.plugin.Extension.Parameter#getSuperParameter()
         */
        public Parameter getSuperParameter() {
            return superParameter;
        }
        
        /**
         * @see org.java.plugin.Extension.Parameter#getSubParameters()
         */
        public Collection getSubParameters() {
            return subParameters;
        }
        
        /**
         * @see org.java.plugin.Documentable#getDocsPath()
         */
        public String getDocsPath() {
            return ExtensionImpl.this.getDocsPath();
        }

        /**
         * @see org.java.plugin.Extension.Parameter#getSubParameter(java.lang.String)
         */
        public Parameter getSubParameter(String id) {
            ParameterImpl result = null;
            for (Iterator it = subParameters.iterator(); it.hasNext();) {
                ParameterImpl param = (ParameterImpl)it.next();
                if (param.getId().equals(id)) {
                    if (result == null) {
                        result = param;
                    } else {
                        throw new IllegalArgumentException(
                            "more than one parameter with ID " + id
                            + " defined in extension " + getUniqueId());
                    }
                }
            }
            return result;
        }

        /**
         * @see org.java.plugin.Extension.Parameter#getSubParameters(java.lang.String)
         */
        public Parameter[] getSubParameters(String id) {
            List result = new LinkedList();
            for (Iterator it = subParameters.iterator(); it.hasNext();) {
                ParameterImpl param = (ParameterImpl)it.next();
                if (param.getId().equals(id)) {
                    result.add(param);
                }
            }
            return (ParameterImpl[])result.toArray(new ParameterImpl[result.size()]);
        }

        /**
         * @see org.java.plugin.Extension.Parameter#rawValue()
         */
        public String rawValue() {
            return rawValue;
        }

        /**
         * @see org.java.plugin.Documentable#getDocumentation()
         */
        public Documentation getDocumentation() {
            return paramDoc;
        }
        
        boolean isValid() {
            if (isParamValid != null) {
                return isParamValid.booleanValue();
            }
            log.debug("validating parameter " + this);
            isParamValid = Boolean.FALSE;
            if (getDefinition() == null) {
                isParamValid = Boolean.FALSE;
                log.warn("can't detect definition for parameter " + this);
            } else {
                isParamValid = rawValueExists ? parseValue() : Boolean.TRUE;
            }
            return isParamValid.booleanValue();
        }
        
        private Boolean parseValue() {
            // note that definition already detected and is not NULL
            log.debug("parsing value for parameter " + this);
            if (ParameterDefinition.TYPE_ANY.equals(definition.getType())
                    || ParameterDefinition.TYPE_NULL.equals(definition.getType())) {
                return Boolean.TRUE;
            } else if (ParameterDefinition.TYPE_STRING.equals(definition.getType())) {
                valueObj = rawValue;
                return Boolean.TRUE;
            }
            String val = rawValue.trim();
            if (val.length() == 0) {
                return Boolean.TRUE;
            }
            if (ParameterDefinition.TYPE_BOOLEAN.equals(definition.getType())) {
                if ("true".equals(val)) {
                    valueObj = Boolean.TRUE;
                } else if ("false".equals(val)) {
                    valueObj = Boolean.FALSE;
                } else {
                    log.error("can't parse value " + val + " of parameter " + this);
                    return Boolean.FALSE;
                }
            } else if (ParameterDefinition.TYPE_NUMBER.equals(definition.getType())) {
                try {
                    valueObj = NumberFormat.getInstance(Locale.ENGLISH).parse(val);
                } catch (ParseException nfe) {
                    log.error("can't parse value " + val + " of parameter " + this, nfe);
                    return Boolean.FALSE;
                }
            } else if (ParameterDefinition.TYPE_DATE.equals(definition.getType())) {
                DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    valueObj = fmt.parse(val);
                } catch (ParseException pe) {
                    log.error("can't parse value " + val + " of parameter " + this, pe);
                    return Boolean.FALSE;
                }
            } else if (ParameterDefinition.TYPE_TIME.equals(definition.getType())) {
                DateFormat fmt = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                try {
                    valueObj = fmt.parse(val);
                } catch (ParseException pe) {
                    log.error("can't parse value " + val + " of parameter " + this, pe);
                    return Boolean.FALSE;
                }
            } else if (ParameterDefinition.TYPE_DATETIME.equals(definition.getType())) {
                DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                try {
                    valueObj = fmt.parse(val);
                } catch (ParseException pe) {
                    log.error("can't parse value " + val + " of parameter " + this, pe);
                    return Boolean.FALSE;
                }
            } else if (ParameterDefinition.TYPE_PLUGIN_ID.equals(definition.getType())) {
                valueObj = getDeclaringPluginDescriptor().getRegistry().getPluginDescriptor(val);
                if (valueObj == null) {
                    log.error("unknown plug-in ID " + val + " provided in parameter " + this);
                    return Boolean.FALSE;
                }
            } else if (ParameterDefinition.TYPE_EXTENSION_POINT_ID.equals(definition.getType())) {
                valueObj = getDeclaringPluginDescriptor().getRegistry().getExtensionPoint(val);
                if (valueObj == null) {
                    log.error("unknown extension point ID " + val + " provided in parameter " + this);
                    return Boolean.FALSE;
                }
            } else if (ParameterDefinition.TYPE_EXTENSION_ID.equals(definition.getType())) {
                getDeclaringPluginDescriptor().getRegistry().getPluginDescriptor(getDeclaringPluginDescriptor().getRegistry().extractPluginId(val)).getExtensions();
                String extId = getDeclaringPluginDescriptor().getRegistry().extractId(val);
                for (Iterator it = getDeclaringPluginDescriptor().getRegistry().getPluginDescriptor(getDeclaringPluginDescriptor().getRegistry().extractPluginId(val)).getExtensions().iterator(); it.hasNext();) {
                    Extension ext = (Extension)it.next();
                    if (ext.getId().equals(extId)) {
                        valueObj = ext;
                        break;
                    }
                }
                if (valueObj == null) {
                    log.error("unknown extension ID " + val + " provided in parameter " + this);
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }
        
        /**
         * @see org.java.plugin.Extension.Parameter#valueAsBoolean()
         */
        public Boolean valueAsBoolean() {
            if (!isValid()) {
                throw new UnsupportedOperationException(
                        "parameter value is invalid");
            }
            if (!ParameterDefinition.TYPE_BOOLEAN.equals(definition.getType())) {
                throw new UnsupportedOperationException("parameter type is not "
                        + ParameterDefinition.TYPE_BOOLEAN);
            }
            return (Boolean)valueObj;
        }
        
        /**
         * @see org.java.plugin.Extension.Parameter#valueAsDate()
         */
        public Date valueAsDate() {
            if (!isValid()) {
                throw new UnsupportedOperationException(
                        "parameter value is invalid");
            }
            if (!ParameterDefinition.TYPE_DATE.equals(definition.getType())
                    && !ParameterDefinition.TYPE_DATETIME.equals(definition.getType())
                    && !ParameterDefinition.TYPE_TIME.equals(definition.getType())) {
                throw new UnsupportedOperationException("parameter type is not "
                        + ParameterDefinition.TYPE_DATE + " nor "
                        + ParameterDefinition.TYPE_DATETIME + " nor"
                        + ParameterDefinition.TYPE_TIME);
            }
            return (Date)valueObj;
        }
        
        /**
         * @see org.java.plugin.Extension.Parameter#valueAsNumber()
         */
        public Number valueAsNumber() {
            if (!isValid()) {
                throw new UnsupportedOperationException(
                        "parameter value is invalid");
            }
            if (!ParameterDefinition.TYPE_NUMBER.equals(definition.getType())) {
                throw new UnsupportedOperationException("parameter type is not "
                        + ParameterDefinition.TYPE_NUMBER);
            }
            return (Number)valueObj;
        }
        
        /**
         * @see org.java.plugin.Extension.Parameter#valueAsString()
         */
        public String valueAsString() {
            if (!isValid()) {
                throw new UnsupportedOperationException(
                        "parameter value is invalid");
            }
            if (!ParameterDefinition.TYPE_STRING.equals(definition.getType())) {
                throw new UnsupportedOperationException("parameter type is not "
                        + ParameterDefinition.TYPE_STRING);
            }
            return (String)valueObj;
        }

        /**
         * @see org.java.plugin.Extension.Parameter#valueAsExtension()
         */
        public Extension valueAsExtension() {
            if (!isValid()) {
                throw new UnsupportedOperationException(
                        "parameter value is invalid");
            }
            if (!ParameterDefinition.TYPE_EXTENSION_ID.equals(definition.getType())) {
                throw new UnsupportedOperationException("parameter type is not "
                        + ParameterDefinition.TYPE_EXTENSION_ID);
            }
            return (Extension)valueObj;
        }
        
        /**
         * @see org.java.plugin.Extension.Parameter#valueAsExtensionPoint()
         */
        public ExtensionPoint valueAsExtensionPoint() {
            if (!isValid()) {
                throw new UnsupportedOperationException(
                        "parameter value is invalid");
            }
            if (!ParameterDefinition.TYPE_EXTENSION_POINT_ID.equals(definition.getType())) {
                throw new UnsupportedOperationException("parameter type is not "
                        + ParameterDefinition.TYPE_EXTENSION_POINT_ID);
            }
            return (ExtensionPoint)valueObj;
        }
        
        /**
         * @see org.java.plugin.Extension.Parameter#valueAsPluginDescriptor()
         */
        public PluginDescriptor valueAsPluginDescriptor() {
            if (!isValid()) {
                throw new UnsupportedOperationException(
                        "parameter value is invalid");
            }
            if (!ParameterDefinition.TYPE_PLUGIN_ID.equals(definition.getType())) {
                throw new UnsupportedOperationException("parameter type is not "
                        + ParameterDefinition.TYPE_PLUGIN_ID);
            }
            return (PluginDescriptor)valueObj;
        }
        
        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "{PluginExtension.Parameter: extUid=" + getDeclaringExtension().getUniqueId() +"; id=" + getId() + "}";
        }
    }
}
