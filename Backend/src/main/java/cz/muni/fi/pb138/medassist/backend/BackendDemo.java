package cz.muni.fi.pb138.medassist.backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.exist.xmldb.EXistResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

/**
 *
 * @author Lenka
 */
public class BackendDemo {

    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db";
    private static final String COLLECTION = "/MedAssist";
    private static final int doctorID = 1;

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        Collection col = null;
        XMLResource res = null;
        try {
            Class cl = Class.forName(DRIVER);
            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);

            col = DatabaseManager.getCollection(URI + COLLECTION, "admin", "admin");
            if (col == null) {
                System.err.println("Collection: " + COLLECTION
                        + " does not exist. Please create it in your database ("
                        + URI + ").");
                System.exit(-1);
            }

            String formFile = "form" + doctorID + ".xml";
            col.removeResource(col.getResource(formFile));

            res = (XMLResource) col.getResource(formFile);
            if (res == null) {
                res = (XMLResource) col.createResource(formFile, "XMLResource");
                res.setContent(createStringFormFile());
                col.storeResource(res);
            }
            try (InputStream is = new StringBufferInputStream((String) res.getContent())) {
                Document doc = newDocumentInstance(is);

                FormsManager formsManager = new FormsManager(doc);
                Random randomGenerator = new Random();
                int formNumber = randomGenerator.nextInt(5) + 1;
                for (int formIndex = 0; formIndex < formNumber; formIndex++) {
                    Form form = new Form((formIndex + 1) + ". form maven");
                    int fid = formsManager.createForm(form);
                    doc = formsManager.getDocument();

                    int slidesNumber = randomGenerator.nextInt(4) + 1;
                    SlidesManager slidesManager = new SlidesManager(doc);
                    for (int slideIndex = 0; slideIndex < 3; slideIndex++) {
                        Slide slide = new Slide((slideIndex + 1) + ". slide", false, fid);
                        int sid = slidesManager.createSlide(slide);
                        doc = slidesManager.getDocument();

                        int questionsNumber = randomGenerator.nextInt(10) + 1;
                        QuestionsManager questionsManager = new QuestionsManager(doc);
                        for (int questionIndex = 0; questionIndex < 3; questionIndex++) {
                            Question question = new Question((questionIndex + 1) + ". question", QuestionType.RADIOBUTTON, sid);
                            int qid = questionsManager.createQuestion(question);
                            doc = questionsManager.getDocument();

                            int optionsNumber = randomGenerator.nextInt(5) + 1;
                            OptionsManager optionsManager = new OptionsManager(doc);
                            for (int optionIndex = 0; optionIndex < optionsNumber; optionIndex++) {
                                Option option = new Option((optionIndex + 1) + ". option", qid);
                                optionsManager.createOption(option);
                            }
                            doc = optionsManager.getDocument();
                        }
                    }
                }

                res.setContent(createStringFormFile(doc));
                col.storeResource(res);
            }

        } catch (XMLDBException | NullPointerException e) {
            System.err.println("XML:DB Exception occurred " + e.getCause() + " " + e.getMessage());
        } finally {
            if (col != null) {
                col.close();
            }
            if (res != null) {
                ((EXistResource) res).freeResources();
            }
        }
    }

    private static String createStringFormFile()
            throws Exception {
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

        return baos.toString();
    }

    private static String createStringFormFile(Document doc)
            throws Exception {
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
}
