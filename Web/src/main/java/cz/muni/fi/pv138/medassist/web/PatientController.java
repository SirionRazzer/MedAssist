/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv138.medassist.web;

import cz.muni.fi.pb138.medassist.backend.MedAssistManagerImpl;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;

/**
 *
 * @author Lenka
 */
@WebServlet(name = "Patient", urlPatterns = {"/Patient/*"})
public class PatientController extends HttpServlet {
    
    //private static final String xsl = "src/main/resources/form_to_html.xsl";
    private static final String xsl = "/WEB-INF/form_to_html.xsl";

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
            case "/listForms":
                listForms(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Unknown action: " + action);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        switch (action) {
            case "/printForm/*":
                printForm(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Unknown action: " + action);
        }
    }

    /**
     * Returns MedAssistManagerImpl variable that was set as attribute in InitListener class.
     * @return MedAssistManagerImpl object for this context
     */
    private MedAssistManagerImpl getMedAssistManager() {
        return (MedAssistManagerImpl) getServletContext().getAttribute("medAssistManager");
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
    private void printForm(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        try {
            MedAssistManagerImpl manager = getMedAssistManager();
            manager.setCurrentDoctor(1);
            StreamSource xslFileSource = new StreamSource(new File(xsl));
            String form = manager.getFormAsHTML(1, xslFileSource);  //first attribute is fid of the form we want
            request.setAttribute("form", form);
            request.getRequestDispatcher("/WEB-INF/1_form.jsp").forward(request, response);
        } catch (XMLDBException | SAXException | ParserConfigurationException ex) {
            System.err.println("Couldn't connect to the database.");
        }

    }

    private void listForms(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        try {
            MedAssistManagerImpl manager = getMedAssistManager();
            manager.setCurrentDoctor(1);
            List<String[]> forms = manager.findAllForms();
            request.setAttribute("forms", forms);
            request.getRequestDispatcher("/WEB-INF/formList.jsp").forward(request, response);
        } catch (SAXException | XMLDBException | ParserConfigurationException ex) {
            Logger.getLogger(DoctorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
