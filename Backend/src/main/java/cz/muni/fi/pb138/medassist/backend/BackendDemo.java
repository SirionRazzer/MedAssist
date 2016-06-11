package cz.muni.fi.pb138.medassist.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
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

    private static final String file = "src/main/resources/form.xml";
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String URI = "xmldb:exist://localhost:8899/exist/xmlrpc/db";
    private static final String COLLECTION = "/MedAssist";

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        try (InputStream is = 
                new FileInputStream( 
                    new File(file))) {
            Document document = newDocumentInstance(is);
            
            String html = Utils.XSLTransform(document);
            
            System.out.println(html);
        }
        
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
            medAssistManager.setCurrentDoctor(1);
            
            String[][] forms = medAssistManager.findAllForms();
            
            for (int i = 0;i < forms.length; i++) {
                System.out.println(forms[i][0] + ": " + forms[i][1]);
            }

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
