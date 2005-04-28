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

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.ExtensionPoint;
import org.java.plugin.IntegrityCheckReport;
import org.java.plugin.PathResolver;
import org.java.plugin.PluginDescriptor;
import org.java.plugin.PluginRegistry;
import org.java.plugin.Version;
import org.java.plugin.IntegrityCheckReport.ReportItem;
import org.java.plugin.impl.IntegrityChecker.ReportItemImpl;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Standard implementation of plug-in registry.
 * For internal use only!
 * @see org.java.plugin.PluginManager#createStandardRegistry()
 * @version $Id: PluginRegistryImpl.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 */
public class PluginRegistryImpl implements PluginRegistry {
    private static final boolean DUMP_ENABLED = true;
    private static final char UNIQUE_SEPARATOR = '@';
    private static Log log = LogFactory.getLog(PluginRegistryImpl.class);

    static String pluginDtd;
    
    static {
        try {
            Reader in = new InputStreamReader(
                    PluginRegistry.class.getResourceAsStream("plugin_0_2.dtd"),
                    "UTF-8");
            try {
                StringBuffer sBuf = new StringBuffer();
                char[] cBuf = new char[64];
                int read;
                while ((read = in.read(cBuf)) != -1) {
                    sBuf.append(cBuf, 0, read);
                }
                pluginDtd = sBuf.toString();
            } finally {
                in.close();
            }
        } catch (Exception e) {
            log.error("can't read plug-in DTD file", e);
        }
    }

    private static EntityResolver getDtdEntityResolver() {
        return new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) {
                //log.debug("publicId=" + publicId + ", systemId=" + systemId);
                if (publicId == null) {
                    return null;
                }
                if (publicId.equals("-//JPF//Java Plug-in Manifest 0.2")) {
                    return new InputSource(new StringReader(pluginDtd));
                }
                return null;
            }
        };
    }

    private Map actualDesctiptors; // <pluginId, PluginDescriptorImpl>
    private Map oldDesctiptors; // <pluginId, List <PluginDescriptorImpl>>
    private List registrationReport = new LinkedList();
    private Set registeredPlugins = new HashSet(); // <plug-in or plug-in fragment unique ID>
    private List allFragments = new LinkedList();
    
    /**
     * Creates standard registry object.
     */
    public PluginRegistryImpl() {
        registrationReport.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, null,
                ReportItem.ERROR_NO_ERROR, "starting up registry"));
    }

    /**
     * @see org.java.plugin.PluginRegistry#register(java.net.URL[])
     */
    public Map register(URL[] manifests) {
        actualDesctiptors = new HashMap();
        oldDesctiptors = new HashMap();
        Map result = new HashMap(manifests.length);
        Map fragments = new HashMap();
        registrationReport.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, null,
                ReportItem.ERROR_NO_ERROR, "processing plug-in manifest files"));
        for (int i = 0; i < manifests.length; i++) {
            URL file = manifests[i];
            Element doc;
            try {
                doc = XmlUtil.loadDom(file.toExternalForm(),
                        getDtdEntityResolver()).getDocumentElement();
            } catch (Exception e) {
                log.error("can't parse manifest file " + file, e);
                registrationReport.add(new ReportItemImpl(ReportItem.SEVERITY_ERROR,
                        null, ReportItem.ERROR_MANIFEST_PROCESSING_FAILED,
                        "can't parse manifest file " + file + ", error - " + e));
                continue;
            }
            if ("plugin-fragment".equals(doc.getNodeName())) {
                fragments.put(file, doc);
                continue;
            }
            if (!"plugin".equals(doc.getNodeName())) {
                log.warn("URL " + file + " points to XML document of unknown type");
                continue;
            }
            log.debug("processing plug-in manifest file - " + file);
            try {
                PluginDescriptorImpl descr = new PluginDescriptorImpl(this, doc, file);
                registrationReport.add(new ReportItemImpl(ReportItem.SEVERITY_INFO,
                        null, ReportItem.ERROR_NO_ERROR,
                        "registering plug-in descriptor " + descr.getUniqueId()));
                if (registeredPlugins.contains(descr.getUniqueId())) {
                    throw new Exception("plug-in " + descr.getUniqueId()
                        + " already registered");
                }
                registeredPlugins.add(descr.getUniqueId());
                result.put(file, descr);
                if (actualDesctiptors.containsKey(descr.getId())) {
                    PluginDescriptorImpl existingDescr =
                        (PluginDescriptorImpl)actualDesctiptors.get(
                                descr.getId());
                    if (existingDescr.getVersion().isGreaterThan(
                            descr.getVersion())) {
                        addOldVersion(descr);
                    } else {
                        actualDesctiptors.put(descr.getId(), descr);
                        addOldVersion(existingDescr);
                    }
                } else {
                    actualDesctiptors.put(descr.getId(), descr);
                }
                registrationReport.add(new ReportItemImpl(ReportItem.SEVERITY_INFO,
                        null, ReportItem.ERROR_NO_ERROR,
                        "plug-in descriptor " + descr.getUniqueId() + " registered"));
            } catch (Exception e) {
                log.error("can't process plug-in file " + file, e);
                registrationReport.add(new ReportItemImpl(ReportItem.SEVERITY_ERROR,
                        null, ReportItem.ERROR_MANIFEST_PROCESSING_FAILED,
                        e.toString()));
            }
        }
        // processing fragments
        registrationReport.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, null,
                ReportItem.ERROR_NO_ERROR,
                "processing plug-in fragment manifest files"));
        for (Iterator it = fragments.keySet().iterator(); it.hasNext();) {
            URL file = (URL)it.next();
            Element doc = (Element)fragments.get(file);
            log.debug("processing plug-in fragment manifest file - " + file);
            try {
                PluginFragmentImpl fragment = new PluginFragmentImpl(this, doc, file);
                registrationReport.add(new ReportItemImpl(ReportItem.SEVERITY_INFO,
                        null, ReportItem.ERROR_NO_ERROR,
                        "registering plug-in fragment " + fragment.getUniqueId()));
                if (registeredPlugins.contains(fragment.getUniqueId())) {
                    throw new Exception("plug-in fragment "
                            + fragment.getUniqueId()
                            + " already registered");
                }
                registeredPlugins.add(fragment.getUniqueId());
                allFragments.add(fragment);
                PluginDescriptorImpl descr =
                    (PluginDescriptorImpl)getPluginDescriptor(
                            fragment.getPluginId());
                boolean isRegistered = false;
                if (fragment.matches(descr)) {
                    descr.registerFragment(fragment, doc);
                    isRegistered = true;
                }
                for (Iterator it2 = getOldPluginDescriptors(
                        descr.getId()).iterator(); it2.hasNext();) {
                    PluginDescriptorImpl oldDescr =
                        (PluginDescriptorImpl)it2.next();
                    if (fragment.matches(oldDescr)) {
                        oldDescr.registerFragment(fragment, doc);
                        isRegistered = true;
                    }
                }
                if (!isRegistered) {
                    log.warn("no matching plug-in found for fragment " + fragment.getUniqueId());
                    registrationReport.add(new ReportItemImpl(
                            ReportItem.SEVERITY_WARNING, null,
                            ReportItem.ERROR_NO_ERROR,
                            "no matching plug-in found for fragment "
                            + fragment.getUniqueId() + ", fragment ignored"));
                } else {
                    result.put(file, fragment);
                    registrationReport.add(new ReportItemImpl(ReportItem.SEVERITY_INFO,
                            null, ReportItem.ERROR_NO_ERROR,
                            "plug-in fragment " + fragment.getUniqueId() + " registered"));
                }
            } catch (Exception e) {
                log.error("can't process plug-in fragment manifest file " + file, e);
                registrationReport.add(new ReportItemImpl(ReportItem.SEVERITY_ERROR,
                        null, ReportItem.ERROR_MANIFEST_PROCESSING_FAILED,
                        e.toString()));
            }
        }
        fragments.clear();
        if (DUMP_ENABLED && log.isDebugEnabled()) {
            StringBuffer buf = new StringBuffer();
            buf.append("PLUGIN REGISTRY DUMP:\r\n")
                .append("-------------- DUMP BEGIN -----------------\r\n")
                .append("\tActual plug-ins: " + actualDesctiptors.size() + "\r\n");
            for (Iterator it = actualDesctiptors.values().iterator();
                    it.hasNext();) {
                buf.append("\t\t")
                    .append(it.next())
                    .append("\r\n");
            }
            buf.append("\tOld version plug-ins: " + oldDesctiptors.size()
                    + "\r\n");
            for (Iterator it = oldDesctiptors.entrySet().iterator();
                    it.hasNext();) {
                Map.Entry entry = (Map.Entry)it.next();
                buf.append("\t\t")
                    .append(entry.getKey())
                    .append(" - ")
                    .append(entry.getValue())
                    .append("\r\n");
            }
            buf.append("Memory TOTAL/FREE/MAX: ")
                .append(Runtime.getRuntime().totalMemory())
                .append("/")
                .append(Runtime.getRuntime().freeMemory())
                .append("/")
                .append(Runtime.getRuntime().maxMemory())
                .append("\r\n");
            buf.append("-------------- DUMP END -----------------\r\n");
            log.debug(buf.toString());
        }
        registrationReport.add(new ReportItemImpl(ReportItem.SEVERITY_INFO, null,
                ReportItem.ERROR_NO_ERROR, "total number of registered plug-ins now is "
                + actualDesctiptors.size()));
        log.info("plug-in descriptors registered - " + result.size());
        return result;
    }

    private void addOldVersion(PluginDescriptor descr) {
        List list = (List)oldDesctiptors.get(descr.getId());
        if (list == null) {
            list = new LinkedList();
            oldDesctiptors.put(descr.getId(), list);
        }
        list.add(descr);
    }
    
    /**
     * @see org.java.plugin.PluginRegistry#getExtensionPoint(java.lang.String, java.lang.String)
     */
    public ExtensionPoint getExtensionPoint(String pluginId, String pointId) {
        PluginDescriptor descriptor = getPluginDescriptor(pluginId);
        if (descriptor == null) {
            throw new IllegalArgumentException("unknown extension point ID - "
                + makeUniqueId(pluginId, pointId));
        }
        for (Iterator it = descriptor.getExtensionPoints().iterator();
                it.hasNext();) {
            ExtensionPoint point = (ExtensionPoint)it.next();
            if (point.getId().equals(pointId)) {
                if (point.isValid()) {
                    return point;
                }
                log.warn("extension point " + point.getUniqueId() + " is invalid and ignored by registry");
                break;
            }
        }
        throw new IllegalArgumentException("unknown extension point ID - "
                + makeUniqueId(pluginId, pointId));
    }

    /**
     * @see org.java.plugin.PluginRegistry#getExtensionPoint(java.lang.String)
     */
    public ExtensionPoint getExtensionPoint(String uniqueId) {
        return getExtensionPoint(extractPluginId(uniqueId),
                extractId(uniqueId));
    }

    /**
     * @see org.java.plugin.PluginRegistry#getPluginDescriptor(java.lang.String)
     */
    public PluginDescriptor getPluginDescriptor(String pluginId) {
        PluginDescriptor result = (PluginDescriptor)actualDesctiptors.get(
                pluginId);
        if (result == null) {
            throw new IllegalArgumentException("unknown plug-in ID - "
                    + pluginId);
        }
        return result;
    }

    /**
     * @see org.java.plugin.PluginRegistry#getPluginDescriptors()
     */
    public Collection getPluginDescriptors() {
        return actualDesctiptors.isEmpty() ? Collections.EMPTY_LIST
                : Collections.unmodifiableCollection(actualDesctiptors.values());
    }

    /**
     * @see org.java.plugin.PluginRegistry#getOldPluginDescriptors(java.lang.String)
     */
    public Collection getOldPluginDescriptors(String pluginId) {
        List result = (List)oldDesctiptors.get(pluginId);
        return (result != null)
            ? Collections.unmodifiableCollection(result)
            : Collections.EMPTY_LIST;
    }

    /**
     * @see org.java.plugin.PluginRegistry#getPluginFragments()
     */
    public Collection getPluginFragments() {
        return allFragments.isEmpty() ? Collections.EMPTY_LIST
                : Collections.unmodifiableCollection(allFragments);
    }

    /**
     * @see org.java.plugin.PluginRegistry#checkIntegrity(PathResolver)
     */
    public IntegrityCheckReport checkIntegrity(PathResolver pathResolver) {
        IntegrityChecker intergityCheckReport = new IntegrityChecker(this,
                registrationReport);
        intergityCheckReport.doCheck(pathResolver);
        return intergityCheckReport;
    }
    
    /**
     * Constructs unique identifier for some plug-in element from it's ID.
     * @param pluginId plug-in ID
     * @param id element ID
     * @return unique ID
     */
    static String makeUniqueId(String pluginId, String id) {
        return pluginId + UNIQUE_SEPARATOR + id;
    }

    /**
     * Constructs unique identifier for plug-in with given ID.
     * @param pluginId plug-in ID
     * @param version plug-in version identifier
     * @return unique plug-in ID
     */
    static String makeUniqueId(String pluginId, Version version) {
        return pluginId + UNIQUE_SEPARATOR + version;
    }
    
    /**
     * @see org.java.plugin.PluginRegistry#extractPluginId(java.lang.String)
     */
    public String extractPluginId(String uniqueId) {
        int p = uniqueId.indexOf(UNIQUE_SEPARATOR);
        if ((p <= 0) || (p >= (uniqueId.length() - 1))) {
            throw new IllegalArgumentException("invalid unique ID - "
                    + uniqueId);
        }
        return uniqueId.substring(0, p);
    }

    /**
     * @see org.java.plugin.PluginRegistry#extractId(java.lang.String)
     */
    public String extractId(String uniqueId) {
        int p = uniqueId.indexOf(UNIQUE_SEPARATOR);
        if ((p <= 0) || (p >= (uniqueId.length() - 1))) {
            throw new IllegalArgumentException("invalid unique ID - "
                    + uniqueId);
        }
        return uniqueId.substring(p + 1);
    }

    /**
     * @see org.java.plugin.PluginRegistry#extractVersion(java.lang.String)
     */
    public Version extractVersion(String uniqueId) {
        int p = uniqueId.indexOf(UNIQUE_SEPARATOR);
        if ((p <= 0) || (p >= (uniqueId.length() - 1))) {
            throw new IllegalArgumentException("invalid unique ID - "
                    + uniqueId);
        }
        return Version.parse(uniqueId.substring(p + 1));
    }
}
