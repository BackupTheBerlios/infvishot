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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This utility class collects W3C DOM based XML processing related functions.
 * @version $Id: XmlUtil.java,v 1.1 2005/04/28 16:29:17 harrym_nu Exp $
 */
final class XmlUtil {
    static final Log log = LogFactory.getLog(XmlUtil.class);

    private static final DocumentBuilderFactory factory;
    
    static {
        factory = DocumentBuilderFactory.newInstance();
        factory.setCoalescing(false); //don't convert CDATA nodes to Text nodes and don't concatenate them
        factory.setIgnoringComments(true);
        factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        log.info("got document builder factory - " + factory);
    }
    
    private static DocumentBuilder getDocumentBuilder()
            throws ParserConfigurationException {
        DocumentBuilder result = factory.newDocumentBuilder();
        result.setErrorHandler(new ErrorHandler() {
            public void error(SAXParseException exception) throws SAXException {
                //log.error("error while parsing XML document", exception);
                throw exception;
            }
            
            public void fatalError(SAXParseException exception) throws SAXException {
                //log.fatal("fatal error while parsing XML document", exception);
                throw exception;
            }
            
            public void warning(SAXParseException exception) {
                log.warn("non-fatal error while parsing XML document", exception);
            }
        });
        return result;
    }
    
    static Document loadDom(String url)
            throws SAXException, IOException, ParserConfigurationException {
        return loadDom(url, null);
    }

    static Document loadDom(String url, EntityResolver er)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilder builder = getDocumentBuilder();
        if (er != null) {
            builder.setEntityResolver(er);
        }
        log.debug("parsing content from URL " + url);
        return builder.parse(url);
    }
    
    static Document loadDom(InputStream in)
            throws SAXException, IOException, ParserConfigurationException {
        return loadDom(in, null);
    }
    
    static Document loadDom(InputStream in, EntityResolver er)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilder builder = getDocumentBuilder();
        if (er != null) {
            builder.setEntityResolver(er);
        }
        return builder.parse(in);
    }
    
    static Document loadDom(Reader in)
            throws SAXException, IOException, ParserConfigurationException {
        return loadDom(in, null);
    }
    
    static Document loadDom(Reader in, EntityResolver er)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilder builder = getDocumentBuilder();
        if (er != null) {
            builder.setEntityResolver(er);
        }
        return builder.parse(new InputSource(in));
    }
    
    static NodeList getNodeList(Node node, String xpath)
            throws TransformerException {
        if (node == null) {
            throw new IllegalArgumentException("can't get node list - node is null");
        }
        if (xpath == null) {
            throw new IllegalArgumentException("can't get node list - XPath expression is null");
        }
        return XPathAPI.selectNodeList(node, xpath);
    }
    
    static Node getNode(Node node, String xpath)
            throws TransformerException {
        if (node == null) {
            throw new IllegalArgumentException("can't get node - node is null");
        }
        if (xpath == null) {
            throw new IllegalArgumentException("can't get node - XPath expression is null");
        }
        NodeList nodes = XPathAPI.selectNodeList(node, xpath);
        int c = nodes.getLength();
        if (c == 0) {
            return null;
        } else if (c > 1) {
            throw new IllegalArgumentException("XPath expression "
                + xpath + " selects too many nodes (" + c + ")");
        } else {
            return nodes.item(0);
        }
    }
    
    static boolean isNodeExists(Node node, String xpath)
            throws TransformerException {
        if (node == null) {
            throw new IllegalArgumentException("can't check node - node is null");
        }
        if (xpath == null) {
            throw new IllegalArgumentException("can't check node - XPath expression is null");
        }
        return XPathAPI.selectNodeList(node, xpath).getLength() == 1;
    }
    
    static String getValue(Node node, String xpath)
            throws TransformerException {
        Node resNode = getNode(node, xpath);
        if (resNode == null) {
            throw new IllegalArgumentException("XPath expression " + xpath
                + " didn't select any node");
        }
        return getValue(resNode);
    }
    
    static String getAttribute(Node node, String name) {
        if (Node.ELEMENT_NODE != node.getNodeType()) {
            throw new IllegalArgumentException("node " + node.getNodeName()
                + " is not an element node");
        }
        Attr attr = ((Element)node).getAttributeNode(name);
        return (attr != null) ? attr.getValue() : null;
    }

    static String getValue(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("can't get node value - node is null");
        }
        NodeList children = node.getChildNodes();
        int c = children.getLength();
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < c; i++) {
            Node child = children.item(i);
            switch (child.getNodeType()) {
                case Node.CDATA_SECTION_NODE: {
                    result.append(child.getNodeValue());
                    break;
                }
                case Node.TEXT_NODE: {
                    result.append(child.getNodeValue().trim());
                    break;
                }
            }
        }
        return result.toString();
    }
    
    private XmlUtil() {
        // no-op
    }
}