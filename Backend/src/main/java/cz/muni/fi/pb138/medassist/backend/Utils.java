/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.medassist.backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
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
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;

/**
 * Class for defining helping methods.
 * 
 * @author Lenka
 */
public class Utils {
    
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
     * @param xsl file defining the transformation
     * @return string containing HTML page as plain text or null in case a problem occurs
     * @throws org.xmldb.api.base.XMLDBException
     */
    public static String xslTransform(Document form, StreamSource xsl) 
            throws XMLDBException {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos)){
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer xsltProc = transformerFactory.newTransformer(xsl);
            
            DOMSource source = new DOMSource(form);
            StreamResult result = new StreamResult(ps);
            
            xsltProc.transform(source, result);
            
            ps.flush();
            
            return baos.toString();
        } catch (TransformerException | IOException ex) {
            throw new XMLDBException();
        }
    }
    
    /**
     * Helper method that replaces char sequences from frontEnd JS files
     * to correct characters.
     * @param source source string we want to repair
     * @return correct string representation of given string
     */
    public static String formatOutput(String source) {
        String result = source
                .replace("%3C", "<")
                .replace("%3E", ">")
                .replace("%2F", "/")
                .replace("%3D", "=")
                .replace("+", " ")
                .replace("%22", "\"");
        return result;
    }
}
