/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.medassist.backend;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;

/**
 * Class for defining helping methods.
 * 
 * @author Lenka
 */
public class Utils {
    
    private static final String xsl = "src/main/resources/form_to_html.xsl";

    /**
     * Method creating DOM Document instance from given input stream.
     * In case given input stream is null, it creates instance of blank document
     * otherwise it parses input stream correctly into DOM Document object.
     * 
     * @param is inputStream we want to parse
     * @return correct new instance of DOM Document
     * @throws ParserConfigurationException if a DocumentBuilder 
     *         cannot be created which satisfies the configuration requested.
     * @throws SAXException if any parse errors occur.
     * @throws IOException if any IO errors occur.
     */
    public static Document newDocumentInstance(InputStream is)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder();
        if (is == null) {
            return builder.newDocument();
        } else {
            return builder.parse(is);
        }
    }

    /**
     * Helper for converting curentDocument into string.
     * It is used in case we want to change currentResource content.
     * 
     * @param document DOM document we want to convert to string
     * @return XML document as string
     * @throws TransformerException if an unrecoverable error occurs during 
     *         the course of the transformation.
     */
    public static String convertDocumentToString(Document document)
            throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        StreamResult result = new StreamResult(ps);

        transformer.transform(source, result);

        ps.flush();

        return baos.toString();
    }
    
    /**
     * Method that transforms given DOM document form into HTML page.
     * The transformation is defined by XSL file.
     * @param form document, we want to transform
     * @return string containing HTML page as plain text or null in case a problem occurs
     * @throws org.xmldb.api.base.XMLDBException
     */
    public static String XSLTransform(Document form) throws XMLDBException {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer xsltProc = transformerFactory.newTransformer(
                    new StreamSource(new File(xsl)));
            
            DOMSource source = new DOMSource(form);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            StreamResult result = new StreamResult(ps);
            
            xsltProc.transform(source, result);
            
            xsltProc.transform(
                    source, 
                    new StreamResult(new File("src/main/resources/form_html.html")));
            
            ps.flush();
            
            return baos.toString();
        } catch (TransformerException ex) {
            throw new XMLDBException();
        }
    }
    
}
