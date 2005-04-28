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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @version $Id: PluginClassLoader.java,v 1.1 2005/04/28 16:29:19 harrym_nu Exp $
 */
class PluginClassLoader extends URLClassLoader {
    static final Log log = LogFactory.getLog(PluginClassLoader.class);
    
    private static File libCacheFolder;
    private static boolean libCacheFolderInitialized = false;
    
    private static URL getClassBaseUrl(Class cls) {
        ProtectionDomain pd = cls.getProtectionDomain();
        if (pd != null) {
            CodeSource cs = pd.getCodeSource();
            if (cs != null) {
                return cs.getLocation();
            }
        }
        return null;
    }

    private static URL[] getUrls(PluginManager manager, PluginDescriptor descr) {
        List result = new LinkedList();
        for (Iterator it = descr.getLibraries().iterator(); it.hasNext();) {
            Library lib = (Library)it.next();
            if (!lib.isCodeLibrary()) {
                continue;
            }
            result.add(manager.getPathResolver().resolvePath(lib, lib.getPath()));
        }
        if (PluginManager.DUMP_ENABLED && log.isDebugEnabled()) {
            StringBuffer buf = new StringBuffer();
            buf.append("Code URL's populated for plug-in " + descr + ":\r\n");
            for (Iterator it = result.iterator(); it.hasNext();) {
                buf.append("\t");
                buf.append(it.next());
                buf.append("\r\n");
            }
            log.debug(buf.toString());
        }
        return (URL[])result.toArray(new URL[result.size()]);
    }
    
    private static File getLibCacheFolder() {
        if (libCacheFolder != null) {
            return libCacheFolderInitialized ? libCacheFolder : null;
        }
        synchronized (PluginClassLoader.class) {
            libCacheFolder = new File(System.getProperty("java.io.tmpdir"), System.currentTimeMillis() + ".jpf-lib-cache");
            log.debug("libraries cache folder is " + libCacheFolder);
            File lockFile = new File(libCacheFolder, "lock");
            if (lockFile.exists()) {
                log.error("can't initialize libraries cache folder " + libCacheFolder + " as lock file indicates that it is owned by another JPF instance");
                return null;
            }
            if (libCacheFolder.exists()) {
                // clean up folder
                emptyFolder(libCacheFolder);
            } else {
                libCacheFolder.mkdirs();
            }
            try {
                if (!lockFile.createNewFile()) {
                    log.error("can\'t create lock file in JPF libraries cache folder" + libCacheFolder);
                    return null;
                }
            } catch (IOException ioe) {
                log.error("can\'t create lock file in JPF libraries cache folder" + libCacheFolder, ioe);
                return null;
            }
            lockFile.deleteOnExit();
            libCacheFolder.deleteOnExit();
            libCacheFolderInitialized = true;
        }
        return libCacheFolder;
    }
    
    private static void emptyFolder(File folder) {
        if (!folder.isDirectory()) {
            return;
        }
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                emptyFolder(file);
            } else {
                file.delete();
            }
        }
    }
    
    private PluginManager manager;
    private PluginDescriptor descr;
    private PluginDescriptor[] publicImports;
    private PluginDescriptor[] privateImports;
    private PluginResourceLoader resourceLoader;
    private Map resourceFilters; // <lib URL, ResourceFilter>
    private Map libraryCache; // <libname, File>

    /**
     * Creates class instance configured to load classes and resources for
     * given plug-in.
     * @param manager plug-in manager instance
     * @param descr plug-in descriptor
     * @param parent parent class loader, usually this is JPF "host"
     *        application class loader
     */
    public PluginClassLoader(PluginManager manager, PluginDescriptor descr,
            ClassLoader parent) {
        super(getUrls(manager, descr), parent);
        this.manager = manager;
        this.descr = descr;
        // collect imported plug-ins
        Map publicImportsMap = new HashMap();
        Map privateImportsMap = new HashMap();
        for (Iterator it = descr.getPrerequisites().iterator(); it.hasNext();) {
            PluginPrerequisite pre = (PluginPrerequisite)it.next();
            PluginDescriptor preDescr = descr.getRegistry().getPluginDescriptor(
                pre.getPluginId());
            if (pre.isExported()) {
                publicImportsMap.put(preDescr.getId(), preDescr);
            } else {
                privateImportsMap.put(preDescr.getId(), preDescr);
            }
        }
        publicImports = (PluginDescriptor[])publicImportsMap.values().toArray(
                new PluginDescriptor[publicImportsMap.size()]);
        privateImports = (PluginDescriptor[])privateImportsMap.values().toArray(
                new PluginDescriptor[privateImportsMap.size()]);
        resourceLoader = PluginResourceLoader.get(manager, descr);
        resourceFilters = new HashMap();
        for (Iterator it = descr.getLibraries().iterator(); it.hasNext();) {
            Library lib = (Library)it.next();
            resourceFilters.put(manager.getPathResolver().resolvePath(lib, lib.getPath()), new ResourceFilter(lib));
        }
        libraryCache = new HashMap();
    }

    PluginDescriptor getPluginDescriptor() {
        return descr;
    }
    
    /**
     * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
     */
    protected Class loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        //log.debug("loadClass(String, boolean): name=" + name + ", this=" + this);
        try {
            return getParent().loadClass(name);
        } catch (ClassNotFoundException cnfe) {
            Class result = loadClass(name, resolve, this, null);
            if (result != null) {
                return result;
            }
        }
        throw new ClassNotFoundException(name);
    }
    
    private Class loadClass(String name, boolean resolve,
            PluginClassLoader requestor, Set seen)
            throws ClassNotFoundException {
        //log.debug("loadClass(String, boolean, ...): name=" + name + ", this=" + this);
        if ((seen != null) && seen.contains(descr.getId())) {
            return null;
        }
        if ((this != requestor) && !manager.isPluginActivated(descr)
            && !manager.isPluginActivating(descr)) {
            throw new ClassNotFoundException("can't load class " + name
                    + " - plug-in " + descr + " is not activated yet");
        }
        Class result = null;
        synchronized (manager) {
            result = findLoadedClass(name);
            if (result != null) {
                checkClassVisibility(result, requestor);
                if (resolve) {
                    resolveClass(result);
                }
                return result; // found already loaded class in this plug-in
            }
            try {
                result = findClass(name);
            } catch (ClassNotFoundException cnfe) {
                // ignore
            }
            if (result != null) {
                checkClassVisibility(result, requestor);
                if (resolve) {
                    resolveClass(result);
                }
                return result; // found class in this plug-in
            }
            if (seen == null) {
                seen = new HashSet();
            }
            seen.add(descr.getId());
            for (int i = 0; i < publicImports.length; i++) {
                if (seen.contains(publicImports[i].getId())) {
                    continue;
                }
                result = ((PluginClassLoader)manager.getPluginClassLoader(
                        publicImports[i])).loadClass(name, resolve,
                                requestor, seen);
                if (result != null) {
                    if (resolve) {
                        resolveClass(result);
                    }
                    break; // found class in publicly imported plug-in
                }
            }
            if ((this == requestor) && (result == null)) {
                for (int i = 0; i < privateImports.length; i++) {
                    if (seen.contains(privateImports[i].getId())) {
                        continue;
                    }
                    result = ((PluginClassLoader)manager.getPluginClassLoader(
                            privateImports[i])).loadClass(name, resolve,
                                    requestor, seen);
                    if (result != null) {
                        if (resolve) {
                            resolveClass(result);
                        }
                        break; // found class in privately imported plug-in
                    }
                }
            }
        }
        return result;
    }
    
    private void checkClassVisibility(Class cls, PluginClassLoader requestor)
            throws ClassNotFoundException {
        log.debug("checkClassVisibility(Class, PluginClassLoader): class=" + cls.getName() + ", requestor=" + requestor + ", this=" + this);
        if (this == requestor) {
            return;
        }
        URL lib = getClassBaseUrl(cls);
        if (lib == null) {
            return; // cls is a system class
        }
        ClassLoader loader = cls.getClassLoader();
        if (!(loader instanceof PluginClassLoader)) {
            return;
        }
        if (loader != this) {
            ((PluginClassLoader)loader).checkClassVisibility(cls, requestor);
        } else {
            ResourceFilter filter = (ResourceFilter)resourceFilters.get(lib);
            if ((filter == null) || !filter.isClassVisible(cls.getName())) {
                throw new ClassNotFoundException("class " + cls.getName()
                        + " is not visible for plug-in " + requestor.descr.getId());
            }
        }
    }

    /**
     * @see java.lang.ClassLoader#findLibrary(java.lang.String)
     */
    protected String findLibrary(String libname) {
        if ((libname == null) || "".equals(libname.trim())) {
            return null;
        }
        log.debug("findLibrary(String): libname=" + libname);
        libname = System.mapLibraryName(libname);
        /*
        libname = libname.replace('\\', '/');
        if (libname.startsWith("/")) {
            libname = libname.substring(1);
        }
        */
        String result = null;
        for (Iterator it = descr.getLibraries().iterator(); it.hasNext();) {
            Library lib = (Library)it.next();
            if (lib.isCodeLibrary()) {
                continue;
            }
            //log.debug("findLibrary(String): trying file " + file + ", exists=" + file.exists());
            URL libUrl = manager.getPathResolver().resolvePath(lib, lib.getPath());
            if ("file".equals(libUrl.getProtocol())) {
                // we have local file system URL
                // so we can treat it as java.io.File
                File file = new File(URI.create(libUrl.toExternalForm() + libname));
                if (file.isFile()) {
                    result = file.getAbsolutePath();
                    break;
                }
                continue;
            }
            // we have some kind of non-local URL
            // try to copy it to local temporary file
            File libFile = (File)libraryCache.get(libname);
            if (libFile != null) {
                if (libFile.isFile()) {
                    result = libFile.getAbsolutePath();
                    break;
                }
                libraryCache.remove(libname);
            }
            if (libraryCache.containsKey(libname)) {
                // already tried to cache this library
                break;
            }
            libFile = cacheLibrary(libUrl, libname);
            if (libFile != null) {
                result = libFile.getAbsolutePath();
                break;
            }
        }
        log.debug("findLibrary(String): result=" + result);
        return result;
    }

    private synchronized File cacheLibrary(URL libUrl, String libname) {
        File cacheFolder = getLibCacheFolder();
        if (libraryCache.containsKey(libname)) {
            return (File)libraryCache.get(libname);
        }
        File result = null;
        try {
            if (cacheFolder == null) {
                throw new IOException("can't initialize libraries cache folder");
            }
            File libCachePluginFolder = new File(cacheFolder, descr.getUniqueId());
            if (!libCachePluginFolder.exists() && !libCachePluginFolder.mkdirs()) {
                throw new IOException("can't create cache folder "
                        + libCachePluginFolder);
            }
            result = new File(libCachePluginFolder, libname);
            InputStream in = libUrl.openStream();
            try {
                OutputStream out = new BufferedOutputStream(
                        new FileOutputStream(result));
                try {
                    byte[] buf = new byte[512];
                    int read;
                    while ((read = in.read(buf)) != -1) {
                        out.write(buf, 0, read);
                    }
                } finally {
                    out.close();
                }
            } finally {
                in.close();
            }
            libraryCache.put(libname, result);
            log.debug("library " + libname + " successfully cached from URL " + libUrl + " and saved to local file " + result);
        } catch (IOException ioe) {
            log.debug("can't cache library " + libname + " from URL " + libUrl, ioe);
            libraryCache.put(libname, null);
            result = null;
        }
        return result;
    }

    /**
     * @see java.lang.ClassLoader#findResource(java.lang.String)
     */
    public URL findResource(String name) {
        log.debug("findResource(String): name=" + name);
        URL result = findResource(name, this, null);
        log.debug("findResource(String): result=" + result);
        return result;
    }

    /**
     * @see java.lang.ClassLoader#findResources(java.lang.String)
     */
    public Enumeration findResources(String name) throws IOException {
        List result = new LinkedList();
        findResources(result, name, this, null);
        return new IteratedEnumeration(result.iterator());
    }

    private URL findResource(String name, PluginClassLoader requestor,
            Set seen) {
        log.debug("findResource(String,...): name=" + name + ", this=" + this);
        if ((seen != null) && seen.contains(descr.getId())) {
            return null;
        }
        URL result = super.findResource(name);
        if (result != null) { // found resource in this plug-in class path
            if (isResourceVisible(name, result, requestor)) {
                return result;
            }
            return null;
        }
        if (resourceLoader != null) {
            result = resourceLoader.findResource(name);
            if (result != null) { // found resource in this plug-in resource libraries
                if (isResourceVisible(name, result, requestor)) {
                    return result;
                }
                return null;
            }
        }
        if (seen == null) {
            seen = new HashSet();
        }
        seen.add(descr.getId());
        for (int i = 0; i < publicImports.length; i++) {
            if (seen.contains(publicImports[i].getId())) {
                continue;
            }
            result = ((PluginClassLoader)manager.getPluginClassLoader(
                    publicImports[i])).findResource(name, requestor, seen);
            if (result != null) {
                break; // found resource in publicly imported plug-in
            }
        }
        if ((this == requestor) && (result == null)) {
            for (int i = 0; i < privateImports.length; i++) {
                if (seen.contains(privateImports[i].getId())) {
                    continue;
                }
                result = ((PluginClassLoader)manager.getPluginClassLoader(
                        privateImports[i])).findResource(name, requestor, seen);
                if (result != null) {
                    break; // found resource in privately imported plug-in
                }
            }
        }
        return result;
    }

    private void findResources(List result, String name,
            PluginClassLoader requestor, Set seen) throws IOException {
        if ((seen != null) && seen.contains(descr.getId())) {
            return;
        }
        for (Enumeration enum = super.findResources(name);
                enum.hasMoreElements();) {
            URL url = (URL)enum.nextElement();
            if (isResourceVisible(name, url, requestor)) {
                result.add(url);
            }
        }
        if (resourceLoader != null) {
            for (Enumeration enum = resourceLoader.findResources(name);
                    enum.hasMoreElements();) {
                URL url = (URL)enum.nextElement();
                if (isResourceVisible(name, url, requestor)) {
                    result.add(url);
                }
            }
        }
        if (seen == null) {
            seen = new HashSet();
        }
        seen.add(descr.getId());
        for (int i = 0; i < publicImports.length; i++) {
            if (seen.contains(publicImports[i].getId())) {
                continue;
            }
            ((PluginClassLoader)manager.getPluginClassLoader(
                    publicImports[i])).findResources(result, name,
                            requestor, seen);
        }
        if (this == requestor) {
            for (int i = 0; i < privateImports.length; i++) {
                if (seen.contains(privateImports[i].getId())) {
                    continue;
                }
                ((PluginClassLoader)manager.getPluginClassLoader(
                        privateImports[i])).findResources(result, name,
                                requestor, seen);
            }
        }
    }
    
    private boolean isResourceVisible(String name, URL url,
            PluginClassLoader requestor) {
        log.debug("isResourceVisible(URL, PluginClassLoader): url=" + url + ", requestor=" + requestor);
        URL lib = null;
        String file = url.getFile();
        try {
            String libFile = file.substring(0, file.length() - name.length());
            String prot = url.getProtocol();
            if ("jar".equals(prot)) {
                if (libFile.endsWith("!/")) {
                    lib = new URL(libFile.substring(0, libFile.length() - 2));
                } else if (libFile.endsWith("!")) {
                    lib = new URL(libFile.substring(0, libFile.length() - 1));
                } else {
                    lib = new URL(libFile);
                }
            } else {
                lib = new URL(prot, url.getHost(), libFile);
            }
        } catch (MalformedURLException mue) {
            log.error("can't get resource library URL", mue);
            return false;
        }
        ResourceFilter filter = (ResourceFilter)resourceFilters.get(lib);
        if (filter == null) {
            return false;
        }
        if (this == requestor) {
            return true;
        }
        return filter.isResourceVisible(name);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "{PluginClassLoader: uid=" + System.identityHashCode(this) + "; " + descr + "}";
    }
    
    private static class ResourceFilter {
        private boolean isPublic;
        private Set entries;

        ResourceFilter(Library lib) {
            entries = new HashSet();
            for (Iterator it = lib.getExports().iterator(); it.hasNext();) {
                String exportPrefix = (String)it.next();
                if ("*".equals(exportPrefix)) {
                    isPublic = true;
                    continue;
                }
                if (!lib.isCodeLibrary()) {
                    exportPrefix = exportPrefix.replace('\\', '.').replace('/', '.');
                    if (exportPrefix.startsWith(".")) {
                        exportPrefix = exportPrefix.substring(1);
                    }
                }
                entries.add(exportPrefix);
            }
        }
        
        boolean isClassVisible(String className) {
            if (isPublic) {
                return true;
            }
            if (entries.isEmpty()) {
                return false;
            }
            if (entries.contains(className)) {
                return true;
            }
            int p = className.lastIndexOf('.');
            if (p == -1) {
                return false;
            }
            return entries.contains(className.substring(0, p) + ".*");
        }

        boolean isResourceVisible(String resPath) {
            resPath = resPath.replace('\\', '.').replace('/', '.');
            if (resPath.startsWith(".")) {
                resPath = resPath.substring(1);
            }
            if (resPath.endsWith(".")) {
                resPath = resPath.substring(0, resPath.length() - 1);
            }
            return isClassVisible(resPath);
        }
    }

    private static class IteratedEnumeration implements Enumeration {
        private final Iterator it;

        IteratedEnumeration(Iterator it) {
            this.it = it;
        }
        
        /**
         * @see java.util.Enumeration#hasMoreElements()
         */
        public boolean hasMoreElements() {
            return it.hasNext();
        }

        /**
         * @see java.util.Enumeration#nextElement()
         */
        public Object nextElement() {
            return it.next();
        }
    }
}
