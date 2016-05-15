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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.xml.sax.SAXException;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

/**
 *
 * @author Lenka
 */
public class MedAssistManager {

    private FormsManager formsManager;
    private SlidesManager slidesManager;
    private QuestionsManager questionsManager;
    private OptionsManager optionsManager;

    private final Collection collection;
    private final List<Integer> doctors = new ArrayList<>();

    private int currentDoctorID;
    private String currentFormFile;
    private Document currentDocument;
    private XMLResource currentResource;

    public MedAssistManager(Collection collection)
            throws Exception {
        for (String resource : Arrays.asList(collection.listResources())) {
            int doctorID = Integer.valueOf(resource.split("_")[0]);
            doctors.add(doctorID);
        }
        this.collection = collection;
    }

    public int getCurrentDoctorID() {
        return currentDoctorID;
    }

    public void setCurrentDoctorID(int currentDoctorID) 
            throws IOException, SAXException, XMLDBException, 
                   ParserConfigurationException {
        if (!doctors.contains(currentDoctorID)) {
            throw new IllegalArgumentException("Doctor with given ID does not exist.");
        }
        this.currentDoctorID = currentDoctorID;
        setCurrentFormFile();
    }

    public String getCurrentFormFile() {
        return currentFormFile;
    }

    private void setCurrentFormFile() 
            throws IOException, SAXException, XMLDBException, 
                   ParserConfigurationException {
        this.currentFormFile = currentDoctorID + "_form.xml";
        setCurrentResource();
    }

    public Document getDocument() {
        return currentDocument;
    }

    public XMLResource getCurrentResource() {
        return currentResource;
    }

    private void setCurrentResource()
            throws IOException, SAXException, XMLDBException, 
                   ParserConfigurationException {
        this.currentResource = (XMLResource) collection.getResource(currentFormFile);
        try (InputStream is = new StringBufferInputStream((String) currentResource.getContent())) {
            currentDocument = newDocumentInstance(is);
        }
    }

    /**
     * Creates new doctorID_form.xml file and initialize root tag of this file.
     * DoctorID is next unused integer from formFiles list. It changes class
     * attributes -> sets currentDoctorID as generated doctorID -> sets
     * currentFormFile as generated form file name doctorID_form.xml -> sets
     * currentDocument as DOM Document instance of currentFormFile -> adds name
     * of the new file to formFiles
     *
     * @return returns new generated doctorID
     * @throws java.lang.Exception
     */
    public int createNewFormXML()
            throws Exception {
        int doctorID = doctors.size() + 1;
        String formFile = doctorID + "_form.xml";
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
            setCurrentDoctorID(doctorID);

        } catch (XMLDBException e) {
            System.err.println("XML:DB Exception occured " + e.getCause() + " " + e.getMessage());
        }
        return currentDoctorID;
    }

    public void createFormForCurrentDocument(Form form) throws TransformerException {
        if (currentDocument == null) {
            throw new NullPointerException("No current document is set within the MedAssistManager.");
        }
        formsManager = new FormsManager(currentDocument);
        int fid = formsManager.createForm(form);
        currentDocument = formsManager.getDocument();

        slidesManager = new SlidesManager(currentDocument);
        for (Slide slide : form.getSlides()) {
            int sid = slidesManager.createSlide(slide, fid);
            currentDocument = slidesManager.getDocument();
            questionsManager = new QuestionsManager(currentDocument);
            for (Question question : slide.getQuestions()) {
                int qid = questionsManager.createQuestion(question, sid);
                currentDocument = questionsManager.getDocument();
                optionsManager = new OptionsManager(currentDocument);
                for (Option option : question.getOptions()) {
                    optionsManager.createOption(option, qid);
                }
                currentDocument = optionsManager.getDocument();
            }
        }
        try {
            currentResource.setContent(createStringFormFile(currentDocument));
            collection.storeResource(currentResource);
        } catch (XMLDBException ex) {
            Logger.getLogger(MedAssistManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String createStringFormFile(Document doc)
            throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        StreamResult result = new StreamResult(ps);

        transformer.transform(source, result);

        ps.flush();

        return baos.toString();
    }

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

    public void freeResource() throws XMLDBException {
        if (currentResource != null) {
            ((EXistResource) currentResource).freeResources();
        }
    }
}
