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

import java.util.Collection;

/**
 * This interface abstracts the extension point - a place where the
 * functionality of plug-in can be extended.
 * @version $Id: ExtensionPoint.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
 */
public interface ExtensionPoint extends PluginElement {
    /**
     * Extension point multiplicity constant.
     */
    String EXT_MULT_ANY = "any";
    
    /**
     * Extension point multiplicity constant.
     */
    String EXT_MULT_ONE = "one";
    
    /**
     * Extension point multiplicity constant.
     */
    String MULT_ONE_PER_PLUGIN = "one-per-plugin";

    /**
     * Returns combination of plug-in ID and extension point ID that is unique
     * within whole set of plug-ins, available for activation.
     * @return unique ID for this extension point
     */
    String getUniqueId();

    /**
     * @return multiplicity of this extension point
     */
    String getMultiplicity();

    /**
     * Returns collection of all top level parameter definitions declared
     * in this extension point.
     * @return collection of {@link ExtensionPoint.ParameterDefinition} objects
     */
    Collection getParameterDefinitions();
    
    /**
     * @param id ID of parameter definition to look for
     * @return parameter definition with given ID
     */
    ParameterDefinition getParameterDefinition(String id);

    /**
     * Returns a collection of all extensions that was successfully "connected"
     * to this point.
     * @return collection of {@link Extension} objects
     */
	Collection getConnectedExtensions();
    
    /**
     * @param uniqueId unique ID of extension
     * @return extension that was successfully "connected" to this point
     */
    Extension getConnectedExtension(String uniqueId);
    
    /**
     * @return <code>true</code> if extension point is considered to be valid
     */
    boolean isValid();

    /**
     * This interface abstracts parameter definition - a parameter
     * "type declaration".
     * @version $Id: ExtensionPoint.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
     */
    interface ParameterDefinition extends PluginElement {
        /**
         * Parameter definition type constant.
         */
        String TYPE_STRING = "string";

        /**
         * Parameter definition type constant.
         */
        String TYPE_BOOLEAN = "boolean";

        /**
         * Parameter definition type constant.
         */
        String TYPE_NUMBER = "number";

        /**
         * Parameter definition type constant.
         */
        String TYPE_DATE = "date";

        /**
         * Parameter definition type constant.
         */
        String TYPE_TIME = "time";

        /**
         * Parameter definition type constant.
         */
        String TYPE_DATETIME = "date-time";

        /**
         * Parameter definition type constant.
         */
        String TYPE_NULL = "null";

        /**
         * Parameter definition type constant.
         */
        String TYPE_ANY = "any";

        /**
         * Parameter definition type constant.
         */
        String TYPE_PLUGIN_ID = "plugin-id";

        /**
         * Parameter definition type constant.
         */
        String TYPE_EXTENSION_POINT_ID = "extension-point-id";

        /**
         * Parameter definition type constant.
         */
        String TYPE_EXTENSION_ID = "extension-id";

        /**
         * Parameter definition multiplicity constant.
         */
        String MULT_ONE = "one";

        /**
         * Parameter definition multiplicity constant.
         */
        String MULT_ANY = "any";

        /**
         * Parameter definition multiplicity constant.
         */
        String MULT_NONE_OR_ONE = "none-or-one";

        /**
         * Parameter definition multiplicity constant.
         */
        String MULT_ONE_OR_MORE = "one-or-more";

        /**
         * @return multiplicity of parameter, that can be defined according
         *         to this definition
         */
        String getMultiplicity();

        /**
         * @return value type of parameter, that can be defined according
         *         to this definition
         */
        String getType();

        /**
         * Returns collection of all parameter sub-definitions declared
         * in this parameter definition.
         * @return collection of {@link ExtensionPoint.ParameterDefinition} objects
         */
        Collection getSubDefinitions();

        /**
         * @param id ID of parameter sub-definition to look for
         * @return parameter sub-definition with given ID
         */
        ParameterDefinition getSubDefinition(String id);

        /**
         * @return extension point, this definition belongs to
         */
        ExtensionPoint getDeclaringExtensionPoint();
        
        /**
         * @return parameter definition, of which this one is child or
         *         <code>null</code> if this is top level parameter definition
         */
        ParameterDefinition getSuperDefinition();
    }
}