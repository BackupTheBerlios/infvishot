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

/**
 * Empty plug-in implementation. This class is used internally for plug-ins,
 * which doesn't defined plug-in class in manifest.
 * <br>
 * This class is for internal use only!
 * @version $Id: EmptyPlugin.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
 */
class EmptyPlugin extends Plugin {
	/**
     * Required constructor.
	 * @param manager
	 * @param descr
	 */
    public EmptyPlugin(PluginManager manager, PluginDescriptor descr) {
		super(manager, descr);
	}

    /**
     * "No-op implementation".
     * @see org.java.plugin.Plugin#doStart()
     */
	protected void doStart() throws Exception {
		// no-op
	}

    /**
     * "No-op implementation".
     * @see org.java.plugin.Plugin#doStop()
     */
	protected void doStop() throws Exception {
		// no-op
	}
}
