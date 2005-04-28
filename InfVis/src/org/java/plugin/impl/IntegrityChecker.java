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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.PathResolver;
import org.java.plugin.Identity;
import org.java.plugin.IntegrityCheckReport;


/**
 * @version $Id: IntegrityChecker.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 */
class IntegrityChecker implements IntegrityCheckReport {
    private static Log log = LogFactory.getLog(IntegrityChecker.class);

    private final PluginRegistryImpl registry;
    private List items = new LinkedList();
    private int errorsCount;
    private int warningsCount;

    IntegrityChecker(PluginRegistryImpl registry, Collection items) {
        this.items = new LinkedList();
        this.registry = registry;
        for (Iterator it = items.iterator(); it.hasNext();) {
            ReportItem item = (ReportItem)it.next();
            if (item.getSeverity() == ReportItem.SEVERITY_ERROR) {
                errorsCount++;
            } else if (item.getSeverity() == ReportItem.SEVERITY_WARNING) {
                warningsCount++;
            }
            this.items.add(item);
        }
    }
    
    void doCheck(PathResolver pathResolver) {
        int count = 0;
        items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, null,
                ReportItem.ERROR_NO_ERROR, "checking plug-ins"));
        try {
            for (Iterator it = registry.getPluginDescriptors().iterator();
                    it.hasNext();) {
                PluginDescriptorImpl descr = (PluginDescriptorImpl)it.next();
                count++;
                items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, descr,
                        ReportItem.ERROR_NO_ERROR, "checking plug-in "
                        + descr.getUniqueId()));
                checkPlugin(descr, pathResolver);
            }
        } catch (Exception e) {
            log.error("integrity check failed for registry " + registry, e);
            errorsCount++;
            items.add(new ReportItemImpl(ReportItem.SEVERITY_ERROR, null,
                    ReportItem.ERROR_CHECKER_FAULT, e.toString()));
        }
        items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, null,
                ReportItem.ERROR_NO_ERROR, count + " plug-ins checked"));
    }
    
    private void checkPlugin(PluginDescriptorImpl descr,
            PathResolver pathResolver) throws Exception {
        // checking prerequisites
        int count = 0;
        items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, descr,
                ReportItem.ERROR_NO_ERROR, "checking prerequisites for "
                + descr.getUniqueId()));
        for (Iterator it = descr.getPrerequisites().iterator(); it.hasNext();) {
            PluginPrerequisiteImpl pre = (PluginPrerequisiteImpl)it.next();
            count++;
            if (!pre.isOptional() && !pre.matches()) {
                errorsCount++;
                items.add(new ReportItemImpl(ReportItem.SEVERITY_ERROR, descr,
                        ReportItem.ERROR_UNSATISFIED_PREREQUISITE,
                        "prerequisite " + pre.getPluginId()
                        + " unsatisfied in plug-in " + descr.getUniqueId()));
            }
        }
        items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, descr,
                ReportItem.ERROR_NO_ERROR, "checked " + count
                + " prerequisites for " + descr.getUniqueId()));
        // checking libraries
        if (pathResolver != null) {
            count = 0;
            items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, descr,
                    ReportItem.ERROR_NO_ERROR, "checking libraries for "
                    + descr.getUniqueId()));
            for (Iterator it = descr.getLibraries().iterator(); it.hasNext();) {
                LibraryImpl lib = (LibraryImpl)it.next();
                count++;
                if (!pathResolver.isResourceExists(
                        pathResolver.resolvePath(lib, lib.getPath()))) {
                    errorsCount++;
                    items.add(new ReportItemImpl(ReportItem.SEVERITY_ERROR, lib,
                            ReportItem.ERROR_BAD_LIBRARY,
                            "can't access to resources from library "
                            + lib.getUniqueId() + " in plug-in "
                            + descr.getUniqueId()));
                }
            }
            items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, descr,
                    ReportItem.ERROR_NO_ERROR, "checked " + count
                    + " libraries for " + descr.getUniqueId()));
        } else {
            items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, descr,
                    ReportItem.ERROR_NO_ERROR, "checking libraries for "
                    + descr.getUniqueId() + " skipped as no path resolver provided"));
        }
        // checking extension points
        count = 0;
        items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, descr,
                ReportItem.ERROR_NO_ERROR, "checking extension points"));
        for (Iterator it = descr.getExtensionPoints().iterator(); it.hasNext();) {
            count++;
            ExtensionPointImpl extPoint = (ExtensionPointImpl)it.next();
            items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, extPoint,
                    ReportItem.ERROR_NO_ERROR, "checking extension point "
                    + extPoint.getUniqueId()));
            Collection extPointItems = extPoint.validate();
            for (Iterator it2 = extPointItems.iterator(); it2.hasNext();) {
                ReportItem item = (ReportItem)it2.next();
                if (item.getSeverity() == ReportItem.SEVERITY_ERROR) {
                    errorsCount++;
                } else if (item.getSeverity() == ReportItem.SEVERITY_WARNING) {
                    warningsCount++;
                }
                items.add(item);
            }
            items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, extPoint,
                    ReportItem.ERROR_NO_ERROR, "extension point "
                    + extPoint.getUniqueId() + " checked"));
        }
        items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, descr,
                ReportItem.ERROR_NO_ERROR, "checked " + count
                + " extension points for " + descr.getUniqueId()));
        // checking extensions
        count = 0;
        items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, descr,
                ReportItem.ERROR_NO_ERROR, "checking extensions"));
        for (Iterator it = descr.getExtensions().iterator(); it.hasNext();) {
            count++;
            ExtensionImpl ext = (ExtensionImpl)it.next();
            items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, ext,
                    ReportItem.ERROR_NO_ERROR, "checking extension "
                    + ext.getUniqueId()));
            Collection extItems = ext.validate();
            for (Iterator it2 = extItems.iterator(); it2.hasNext();) {
                ReportItem item = (ReportItem)it2.next();
                if (item.getSeverity() == ReportItem.SEVERITY_ERROR) {
                    errorsCount++;
                } else if (item.getSeverity() == ReportItem.SEVERITY_WARNING) {
                    warningsCount++;
                }
                items.add(item);
            }
            items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, ext,
                    ReportItem.ERROR_NO_ERROR, "extension "
                    + ext.getUniqueId() + " checked"));
        }
        items.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, descr,
                ReportItem.ERROR_NO_ERROR, "checked " + count
                + " extensions for " + descr.getUniqueId()));
    }
    
    /**
     * @see org.java.plugin.IntegrityCheckReport#countErrors()
     */
    public int countErrors() {
        return errorsCount;
    }

    /**
     * @see org.java.plugin.IntegrityCheckReport#countWarnings()
     */
    public int countWarnings() {
        return warningsCount;
    }

    /**
     * @see org.java.plugin.IntegrityCheckReport#getItems()
     */
    public Collection getItems() {
        return items;
    }

    static class ReportItemImpl implements ReportItem {
        private byte severity;
        private Identity source;
        private int code;
        private String msg;
        
        ReportItemImpl(byte severity, Identity source, int code, String msg) {
            this.severity = severity;
            this.source = source;
            this.code = code;
            this.msg = msg;
        }
        
        /**
         * @see org.java.plugin.IntegrityCheckReport.ReportItem#getCode()
         */
        public int getCode() {
            return code;
        }
        
        /**
         * @see org.java.plugin.IntegrityCheckReport.ReportItem#getMessage()
         */
        public String getMessage() {
            return msg;
        }
        
        /**
         * @see org.java.plugin.IntegrityCheckReport.ReportItem#getSeverity()
         */
        public byte getSeverity() {
            return severity;
        }
        
        /**
         * @see org.java.plugin.IntegrityCheckReport.ReportItem#getSource()
         */
        public Identity getSource() {
            return source;
        }
    }
}
