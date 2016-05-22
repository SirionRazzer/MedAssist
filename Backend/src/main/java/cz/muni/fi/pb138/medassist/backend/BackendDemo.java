package cz.muni.fi.pb138.medassist.backend;

import java.util.Random;
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
        MedAssistManager medAssistManager = null;
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

            medAssistManager = new MedAssistManager(col);
            int doctorID = medAssistManager.createNewFormXML();
            
            Random randomGenerator = new Random();
            int formNumber = randomGenerator.nextInt(5) + 1;
            for (int formIndex = 0; formIndex < formNumber; formIndex++) {
                Form form = new Form((formIndex + 1) + ". form maven");

                int slidesNumber = randomGenerator.nextInt(4) + 1;
                for (int slideIndex = 0; slideIndex < slidesNumber; slideIndex++) {
                    Slide slide = new Slide((slideIndex + 1) + ". slide", false);

                    int questionsNumber = randomGenerator.nextInt(10) + 1;
                    for (int questionIndex = 0; questionIndex < questionsNumber; questionIndex++) {
                        Question question = new Question((questionIndex + 1) + ". question", QuestionType.RADIOBUTTON);

                        int optionsNumber = randomGenerator.nextInt(5) + 1;
                        for (int optionIndex = 0; optionIndex < optionsNumber; optionIndex++) {
                            question.addOption(new Option((optionIndex + 1) + ". option"));
                        }
                        slide.addQuestion(question);
                    }
                    form.addSlide(slide);
                }
                medAssistManager.createNewForm(form);
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
}
