/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv138.medassist.web;

import cz.muni.fi.pb138.medassist.backend.MedAssistManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

/**
 * Class for initializing context of the application
 * 
 * @author Lenka
 */
@WebListener
public class InitListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(InitListener.class);

    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String URI = "xmldb:exist://localhost:8899/exist/xmlrpc/db";
    private static final String COLLECTION = "/MedAssist";
    private static final String USER = "admin";
    private static final String PASS = "admin";

    private Collection col = null;
    private MedAssistManager manager = null;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            col = prepareCollection();
            manager = new MedAssistManager(col);

            ServletContext servletContext = sce.getServletContext();
            servletContext.setAttribute("medAssistManager", manager);
        } catch (XMLDBException 
                | ClassNotFoundException 
                | InstantiationException 
                | IllegalAccessException ex) {
            logger.error("Problem occured while connecting to the database. (" + ex.getMessage() + ")");
            System.exit(-1);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (col != null) {
            try {
                col.close();
            } catch (XMLDBException ex) {
                logger.error(ex.getMessage());
            }
        }
        if (manager != null) {
            try {
                manager.freeResource();
            } catch (XMLDBException ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    private Collection prepareCollection()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        try {
            Class cl = Class.forName(DRIVER);
            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);

            col = DatabaseManager.getCollection(URI + COLLECTION, USER, PASS);
        } catch (XMLDBException ex) {
            logger.error("Could not connect to the database. (" + ex.getMessage() + ")");
            System.exit(-1);
        }

        return col;
    }

}
