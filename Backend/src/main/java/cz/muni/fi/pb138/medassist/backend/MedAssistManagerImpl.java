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
import java.io.StringBufferInputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.exist.xmldb.EXistResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

/**
 * Class for managing operations in MedAssist application.
 * 
 * @author Lenka
 */
public class MedAssistManagerImpl implements MedAssistManager {

    private final Collection collection;

    private int currentDoctor;
    private Document currentDocument;
    private XMLResource currentResource;

    /**
     * Constructor with parameters.
     * It sets class parameter collection as given collection.
     * In case given collection already contains some resources (there are 
     * already some doctors registered) it appends the owners (doctors) to the 
     * list of doctors.
     * 
     * @param collection we want to access
     * @throws XMLDBException if any error occurs while accessing the database.
     */
    public MedAssistManagerImpl(Collection collection) throws XMLDBException {
        for (String resource : Arrays.asList(collection.listResources())) {
            int doctorID = Integer.valueOf(resource.split("_")[0]);
            doctors.add(doctorID);
        }
        this.collection = collection;
    }

    /**
     * Simple getter for collection.
     * @return collection of this object
     */
    public Collection getCollection() {
        return collection;
    }

    /**
     * Simple getter for doctor.
     * @return doctorID of currently set doctor
     */
    public int getCurrentDoctor() {
        return currentDoctor;
    }

    /**
     * Simple getter for document.
     * @return current document
     */
    public Document getCurrentDocument() {
        return currentDocument;
    }

    /**
     * Changes current doctor. Should be used only in case of more doctors
     * within the office.
     * It also changes current document and resource.
     * @param currentDoctorID id of the doctor we want to switch to
     * 
     * @throws IOException if any IO errors occur.
     * @throws SAXException if any parse errors occur.
     * @throws XMLDBException if any error occurs while accessing the database
     * @throws ParserConfigurationException if a DocumentBuilder 
     *         cannot be created which satisfies the configuration requested.
     */
    public void setCurrentDoctor(int currentDoctorID) 
            throws IOException, SAXException, XMLDBException, 
                   ParserConfigurationException {
        if (!doctors.contains(currentDoctorID)) {
            throw new IllegalArgumentException("Doctor with given ID does not exist.");
        }
        this.currentDoctor = currentDoctorID;
        
        String file = String.format("%09d", currentDoctor) + "_form.xml";
        this.currentResource = (XMLResource) collection.getResource(file);
        try (InputStream is = new StringBufferInputStream((String) currentResource.getContent())) {
            currentDocument = newDocumentInstance(is);
        }
    }

    /**
     * Creates new doctorID_form.xml file and initialize root tag of this file.
     * DoctorID is next unused integer from formFiles list. In generated file 
     * name, doctor id has exactly 9 digits. If original id has less digits,
     * zeros are added in front of it (e.g. 000000001_form.xml).
     * 
     * Changes class attributes: 
     *  -> sets currentDoctor as generated doctorID 
     *  -> creates new resource within the collection, sets it as currentResource
     *  -> sets currentDocument as DOM Document instance of currentResource
     *  -> adds new doctorID to the list of 
     * 
     * @return new generated doctorID
     * @throws Exception in case any error occurs while performing this action
     */
    @Override
    public int createNewFormXML() 
            throws Exception {
        int doctorID = doctors.size() + 1;
        String formFile = String.format("%09d", doctorID) + "_form.xml";
        doctors.add(doctorID);

        Document document = newDocumentInstance(null);
        Element rootElement = document.createElement("forms");
        document.appendChild(rootElement);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        StreamResult result = new StreamResult(ps);

        transformer.transform(source, result);

        ps.flush();

        try {
            currentResource = (XMLResource) collection.createResource(formFile, "XMLResource");
            currentResource.setContent(baos.toString());
            collection.storeResource(currentResource);
            setCurrentDoctor(doctorID);

        } catch (XMLDBException e) {
            System.err.println("XML:DB Exception occured " + e.getMessage());
        }
        return currentDoctor;
    }

    /**
     * Creates new form file for the doctor, that is currently set.
     * Form must be in DOM Document format and must return correct xml (please 
     * check singleFormSchema.xsd in resources). Root node of the correct form 
     * is appended at the end of currentDocument. Content of the currentResource
     * is than changed to updated document and after that uploaded to the collection.
     * 
     * @param form correct xml file represented by DOM Document
     * @throws TransformerException if an unrecoverable error occurs during 
     *         the course of the transformation.
     */
    @Override
    public void createNewForm(Document form) throws TransformerException {
        
        if (currentDocument == null) {
            throw new NullPointerException("No current document is set within the MedAssistManager.");
        }
        
        int fid = currentDocument.getElementsByTagName("form").getLength() + 1;
        
        Node documentRoot = currentDocument.getDocumentElement();
        Element formElement = (Element) currentDocument.adoptNode(form.getFirstChild());
        formElement.setAttribute("fid", String.valueOf(fid));
        
        documentRoot.appendChild(formElement);
        
        try {
            currentResource.setContent(convertCurrentDocumentToString());
            collection.storeResource(currentResource);
        } catch (XMLDBException ex) {
            Logger.getLogger(MedAssistManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Helper for converting curentDocument into string.
     * It is used in case we want to change currentResource content.
     * 
     * @return xml document as string
     * @throws TransformerException if an unrecoverable error occurs during 
     *         the course of the transformation.
     */
    public String convertCurrentDocumentToString()
            throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(currentDocument);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        StreamResult result = new StreamResult(ps);

        transformer.transform(source, result);

        ps.flush();

        return baos.toString();
    }

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
    private static Document newDocumentInstance(InputStream is)
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
     * Helper that frees the resources.
     * @throws XMLDBException if any error occurs while accessing the database
     */
    public void freeResource() throws XMLDBException {
        if (currentResource != null) {
            ((EXistResource) currentResource).freeResources();
        }
    }
}
