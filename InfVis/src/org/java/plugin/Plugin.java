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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is base for "home" class of plug-in runtime. Using this class, plug-in code
 * can get access to plug-in framework ({@link org.java.plugin.PluginManager manager},
 * {@link org.java.plugin.PluginRegistry registry}) which was loaded it.
 * It is also used by manager during plug-in life cycle management (activation
 * and deactivation).
 * <br>
 * Plug-in vendor may provide it's own implementation of this class if some
 * actions should be performed during plug-in activation/deactivation. When no
 * class specified, framework provides default "empty" implementation that does
 * nothing when plug-in started and stopped.
 * @version $Id: Plugin.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
 */
public abstract class Plugin {
    /**
     * Makes logging service available for descending classes.
     */
    protected final Log log = LogFactory.getLog(getClass());

    private final PluginManager manager;
	private final PluginDescriptor descriptor;
	private boolean started;
	
	/**
     * Constructor to be used by plug-in manager for plug-in instantiation.
     * Your plug-ins have to provide constructor with this exact signature to
     * be successfully loaded by manager.
	 * @param manager manager, which controls this plug-in
	 * @param descr descriptor of this plug-in
	 */
    public Plugin(PluginManager manager, PluginDescriptor descr) {
        this.manager = manager;
		this.descriptor = descr;
		if (descr == null) {
			throw new IllegalArgumentException("descriptor cannot be NULL");
		}
	}

	/**
     * @return descriptor of this plug-in
	 */
    public final PluginDescriptor getDescriptor() {
		return descriptor;
	}
    
    /**
     * @return manager which controls this plug-in
     */
    public final PluginManager getManager() {
        return manager;
    }
	
	/**
     * For internal use only!
	 * @throws Exception
	 */
    final void start() throws Exception {
		if (!started) {
			doStart();
			started = true;
		}
	}

    /**
     * For internal use only!
     * @throws Exception
     */
	final void stop() throws Exception {
		if (started) {
			doStop();
			started = false;
		}
	}
	
    /**
     * For internal use only!
     * @throws Exception
     */
	final boolean isActive() {
		return started;
	}

	/**
     * This method will be called once during plug-in activation before any
     * access to any code from this plug-in.
	 * @throws Exception
	 */
    protected abstract void doStart() throws Exception;

	/**
     * This method will be called once during plug-in deactivation. After
     * this method call, no other code from this plug-in can be accessed,
     * unless {@link #doStart()} method will be called again (but for another
     * instance of this class).
	 * @throws Exception
	 */
    protected abstract void doStop() throws Exception;

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "{" + getClass().getName() + ": manager=" + manager + ", descriptor=" + descriptor + "}";
    }
}
