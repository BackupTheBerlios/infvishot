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
import java.util.Date;

import org.java.plugin.ExtensionPoint.ParameterDefinition;

/**
 * This interface abstracts an extension - particular functionality,
 * the plug-in contribute to the system
 * @version $Id: Extension.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
 */
public interface Extension extends PluginElement {
    /**
     * Returns combination of plug-in ID and extension ID that is unique
     * within whole set of plug-ins, available for activation.
     * @return unique ID for this extension
     */
    String getUniqueId();

    /**
     * Returns collection of all top level parameters defined in this extension.
     * @return collection of {@link Extension.Parameter} objects
     */
    Collection getParameters();
    
    /**
     * @param id ID of parameter to look for
     * @return parameter with given ID
     */
    Parameter getParameter(String id);

    /**
     * @param id ID of parameter to look for
     * @return collection of all parameters with given ID
     */
    Parameter[] getParameters(String id);

    /**
     * @return ID of plug-in, extended point belongs to
     */
	String getExtendedPluginId();
    
    /**
     * @return ID of extended point
     */
    String getExtendedPointId();
    
    /**
     * @return <code>true</code> if extension is considered to be valid
     */
    boolean isValid();
    
    /**
     * This interface abstracts extension parameter according to extension
     * declaration in manifest.
     * @version $Id: Extension.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
     */
    interface Parameter extends PluginElement {
        /**
         * @return parameter value as it is specified in manifest, if no value
         *         provided there, this method should return empty string
         */
        String rawValue();

        /**
         * Returns collection of all sub-parameters defined in this parameter.
         * @return collection of {@link Extension.Parameter} objects
         */
        Collection getSubParameters();

        /**
         * @param id ID of sub-parameter to look for
         * @return sub-parameter with given ID
         */
        Parameter getSubParameter(String id);

        /**
         * @param id ID of sub-parameter to look for
         * @return collection of all sub-parameters with given ID
         */
        Parameter[] getSubParameters(String id);

        /**
         * @return extension this parameter belongs to
         */
        Extension getDeclaringExtension();
        
        /**
         * Returns definition for this extension parameter.
         * May return <code>null</code> for "invalid" parameters.
         * @return parameter definition or <code>null</code>, if this parameter
         *         is "invalid"
         */
        ParameterDefinition getDefinition();
        
        /**
         * @return parameter, of which this one is child or <code>null</code> if
         *         this is top level parameter
         */
        Parameter getSuperParameter();
        
        /**
         * Returns "typed" value of parameter. If this parameter is invalid or
         * is not of type {@link ExtensionPoint.ParameterDefinition#TYPE_STRING}, this method
         * should throw an {@link UnsupportedOperationException}.
         * @return value as String object
         */
        String valueAsString();
        
        /**
         * Returns "typed" value of parameter. If this parameter is invalid or
         * is not of type {@link ExtensionPoint.ParameterDefinition#TYPE_BOOLEAN}, this method
         * should throw an {@link UnsupportedOperationException}.
         * @return value as Boolean object
         */
        Boolean valueAsBoolean();
        
        /**
         * Returns "typed" value of parameter. If this parameter is invalid or
         * is not of type {@link ExtensionPoint.ParameterDefinition#TYPE_NUMBER}, this method
         * should throw an {@link UnsupportedOperationException}.
         * @return value as Number object
         */
        Number valueAsNumber();
        
        /**
         * Returns "typed" value of parameter. If this parameter is invalid or
         * is not of type {@link ExtensionPoint.ParameterDefinition#TYPE_DATE},
         * {@link ExtensionPoint.ParameterDefinition#TYPE_TIME}
         * or {@link ExtensionPoint.ParameterDefinition#TYPE_DATETIME}, this method
         * should throw an {@link UnsupportedOperationException}.
         * @return value as Date object
         */
        Date valueAsDate();
        
        /**
         * Returns "typed" value of parameter. If this parameter is invalid or
         * is not of type {@link ExtensionPoint.ParameterDefinition#TYPE_PLUGIN_ID}, this method
         * should throw an {@link UnsupportedOperationException}.
         * @return value as PluginDescriptor object
         */
        PluginDescriptor valueAsPluginDescriptor();
        
        /**
         * Returns "typed" value of parameter. If this parameter is invalid or
         * is not of type {@link ExtensionPoint.ParameterDefinition#TYPE_EXTENSION_POINT_ID}, this method
         * should throw an {@link UnsupportedOperationException}.
         * @return value as ExtensionPoint object
         */
        ExtensionPoint valueAsExtensionPoint();
        
        /**
         * Returns "typed" value of parameter. If this parameter is invalid or
         * is not of type {@link ExtensionPoint.ParameterDefinition#TYPE_EXTENSION_ID}, this method
         * should throw an {@link UnsupportedOperationException}.
         * @return value as Extension object
         */
        Extension valueAsExtension();
    }
}