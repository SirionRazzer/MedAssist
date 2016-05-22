/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv138.medassist.web;

import cz.muni.fi.pb138.medassist.backend.MedAssistManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;

/**
 *
 * @author Lenka
 */
@WebServlet(name = "Doctor", urlPatterns = {"/Doctor/*"})
public class DoctorController extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(InitListener.class);

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        switch (action) {
            case "/form1":
                printForm1(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Unknown action: " + action);
        }
    }

    /**
     * Returns MedAssistManager variable that was set as attribute in InitListener class.
     * @return MedAssistManager object for this context
     */
    private MedAssistManager getMedAssistManager() {
        return (MedAssistManager) getServletContext().getAttribute("medAssistManager");
    }

    /**
     * Test method for testing the collaboration between this application,
     * backend and database.
     * 
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    private void printForm1(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        try {
            MedAssistManager manager = getMedAssistManager();
            manager.setCurrentDoctor(1);
            String form1 = manager.getCurrentDocumentAsString();
            request.setAttribute("form1", form1);
            request.getRequestDispatcher("/WEB-INF/1_form.jsp").forward(request, response);
        } catch (XMLDBException | SAXException | ParserConfigurationException | TransformerException ex) {
            logger.error("Could not connect to the database. (" + ex.getMessage() + ")");
        }

    }
}
