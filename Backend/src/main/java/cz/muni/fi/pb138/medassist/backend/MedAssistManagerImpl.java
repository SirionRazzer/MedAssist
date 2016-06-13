/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.medassist.backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.exist.xmldb.EXistResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

/**
 * Class for managing operations in MedAssist application.
 *
 * @author Lenka
 */
public class MedAssistManagerImpl implements MedAssistManager {

    private final Collection collection;

    private int currentDoctor;
    private Document currentFormDocument;
    private Document currentAnswerDocument;
    private XMLResource currentFormResource;
    private XMLResource currentAnswerResource;

    /**
     * Constructor with parameters. It sets class parameter collection as given
     * collection. In case given collection already contains some resources
     * (there are already some doctors registered) it appends the owners
     * (doctors) to the list of doctors.
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
     *
     * @return collection of this object
     */
    public Collection getCollection() {
        return collection;
    }

    /**
     * Simple getter for doctor.
     *
     * @return doctorID of currently set doctor
     */
    public int getCurrentDoctor() {
        return currentDoctor;
    }

    /**
     * Simple getter for document.
     *
     * @return current document
     */
    public Document getCurrentFormDocument() {
        return currentFormDocument;
    }

    /**
     * Changes current doctor. Should be used only in case of more doctors
     * within the office. It also changes current document and resource.
     *
     * @param currentDoctorID id of the doctor we want to switch to
     *
     * @throws IOException if any IO errors occur.
     * @throws SAXException if any parse errors occur.
     * @throws XMLDBException if any error occurs while accessing the database
     * @throws ParserConfigurationException if a DocumentBuilder cannot be
     * created which satisfies the configuration requested.
     */
    public void setCurrentDoctor(int currentDoctorID)
            throws IOException, SAXException, XMLDBException,
            ParserConfigurationException {
        if (!doctors.contains(currentDoctorID)) {
            throw new IllegalArgumentException("Doctor with given ID does not exist.");
        }
        this.currentDoctor = currentDoctorID;

        String formFile = String.format("%09d", currentDoctor) + "_form.xml";
        String answerFile = String.format("%09d", currentDoctor) + "_answer.xml";
        this.currentFormResource = (XMLResource) collection.getResource(formFile);
        this.currentAnswerResource = (XMLResource) collection.getResource(answerFile);
        try (
                InputStream isForm = new StringBufferInputStream((String) currentFormResource.getContent());
                InputStream isAnswer = new StringBufferInputStream((String) currentAnswerResource.getContent());
                ) {
            currentFormDocument = Utils.newDocumentInstance(isForm);
            currentAnswerDocument = Utils.newDocumentInstance(isAnswer);
        }
    }

    /**
     * Creates new doctorID_form.xml and doctorID_answer.xml files 
     * and initialize root tag of these files.
     * DoctorID is next unused integer from formFiles list. In generated file
     * name, doctor id has exactly 9 digits. If original id has less digits,
     * zeros are added in front of it (e.g. 000000001_form.xml).
     *
     * Changes class attributes: 
     * -> sets currentDoctor as generated doctorID 
     * -> creates new resource within the collection, sets it as currentResource 
     * -> sets currentFormDocument as DOM Document instance of currentResource 
     * -> adds new doctorID to the list of
     *
     * @return new generated doctorID
     * @throws Exception in case any error occurs while performing this action
     */
    @Override
    public int createNewFormXML()
            throws Exception {
        int doctorID = doctors.size() + 1;
        String formFile = String.format("%09d", doctorID) + "_form.xml";
        String answerFile = String.format("%09d", doctorID) + "_answer.xml";
        doctors.add(doctorID);

        Document document = Utils.newDocumentInstance(null);
        Element rootElement = document.createElement("forms");
        document.appendChild(rootElement);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream psForm = new PrintStream(baos);
        StreamResult result = new StreamResult(psForm);

        transformer.transform(source, result);

        psForm.flush();

        try {
            currentFormResource = (XMLResource) collection.createResource(formFile, "XMLResource");
            currentAnswerResource = (XMLResource) collection.createResource(answerFile, "XMLResource");
            
            currentFormResource.setContent(baos.toString());
            currentAnswerResource.setContent(baos.toString());
            
            collection.storeResource(currentFormResource);
            collection.storeResource(currentAnswerResource);
            
            setCurrentDoctor(doctorID);

        } catch (XMLDBException e) {
            System.err.println("XML:DB Exception occured " + e.getMessage());
        }
        return currentDoctor;
    }

    /**
     * Creates new form file for the doctor, that is currently set. Form must be
 in DOM Document format and must return correct xml (please check
 singleFormSchema.xsd in resources). Root node of the correct form is
 appended at the end of currentFormDocument. Content of the currentResource is
 than changed to updated document and after that uploaded to the
 collection.
     *
     * @param form correct xml file represented by DOM Document
     * @throws TransformerException if an unrecoverable error occurs during the
     * course of the transformation.
     */
    @Override
    public void createNewForm(Document form) throws TransformerException {

        if (currentFormDocument == null) {
            throw new NullPointerException("No current document is set within the MedAssistManager.");
        }

        int fid = currentFormDocument.getElementsByTagName("form").getLength() + 1;

        Node documentRoot = currentFormDocument.getDocumentElement();
        Element formElement = (Element) currentFormDocument.adoptNode(form.getFirstChild());
        formElement.setAttribute("fid", String.valueOf(fid));

        documentRoot.appendChild(formElement);

        try {
            currentFormResource.setContent(Utils.convertDocumentToString(currentFormDocument));
            collection.storeResource(currentFormResource);
        } catch (XMLDBException ex) {
            Logger.getLogger(MedAssistManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Helper that frees the resources.
     *
     * @throws XMLDBException if any error occurs while accessing the database
     */
    public void freeResource() throws XMLDBException {
        if (currentFormResource != null) {
            ((EXistResource) currentFormResource).freeResources();
        }
    }

    @Override
    public List<String[]> findAllForms() {
        NodeList formsList = currentFormDocument.getElementsByTagName("form");
        int formCount = formsList.getLength();
        List<String[]> forms = new ArrayList();
        for (int i = 0; i < formCount; i++) {
            Element form = (Element) formsList.item(i);
            String[] newForm = {form.getAttribute("fid"), 
                    form.getElementsByTagName("name").item(0).getTextContent()};
            forms.add(newForm);
        }
        return forms;
    }

    @Override
    public String getFormAsHTML(int fid, StreamSource xsl)
            throws ParserConfigurationException, SAXException, IOException, XMLDBException {
        Document document = Utils.newDocumentInstance(null);
        String result = null;

        XPathQueryService xpqs = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
        xpqs.setProperty("indent", "yes");

        String resourceID = String.format("%09d", currentDoctor) + "_form.xml";
        String xPath = "//*[@fid = " + fid + "]";
        ResourceSet resourceSet = xpqs.queryResource(resourceID, xPath);
        ResourceIterator iterator = resourceSet.getIterator();
        Resource res = iterator.nextResource();
        if (iterator.hasMoreResources()) {
            throw new IllegalArgumentException("More forms with the same id.");
        } else {
            String formFromResource = (String) res.getContent();
            Document form = Utils.newDocumentInstance(
                    new ByteArrayInputStream(formFromResource.getBytes(StandardCharsets.UTF_8)));
            
            result = Utils.xslTransform(form, xsl);
            
            try {
                ((EXistResource) res).freeResources();
            } catch (XMLDBException xe) {
                xe.printStackTrace();
            }
        }

        return result;
    }
}
