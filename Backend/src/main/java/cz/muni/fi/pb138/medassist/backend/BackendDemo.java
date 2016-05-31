package cz.muni.fi.pb138.medassist.backend;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
    private static final String URI = "xmldb:exist://localhost:8899/exist/xmlrpc/db";
    private static final String COLLECTION = "/MedAssist";

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        Collection col = null;
        XMLResource res = null;
        MedAssistManagerImpl medAssistManager = null;
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

            medAssistManager = new MedAssistManagerImpl(col);
            
            //uncomment in case you want to create form for a new doctor
            /*int doctorID = medAssistManager.createNewFormXML();
            
            
            Document form = null;
            File file = new File("src/main/resources/form.xml");
            try (InputStream is = new FileInputStream(file)) {
                DocumentBuilder builder = DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder();
                if (is == null) {
                    throw new NullPointerException("Empty document.");
                } else {
                    form = builder.parse(is);
               }

            }
            
            medAssistManager.createNewForm(form);
            */

        } catch (XMLDBException | NullPointerException e) {
            System.err.println("XML:DB Exception occurred " + e.getMessage());
            e.printStackTrace(System.out);
        } finally {
            if (col != null) {
                col.close();
            }
            if (medAssistManager != null) {
                medAssistManager.freeResource();
            }
        }
    }
    
    private static String getDocumentAsString(Document doc)
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
}
