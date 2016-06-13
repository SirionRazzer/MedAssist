/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv138.medassist.web;

import cz.muni.fi.pb138.medassist.backend.MedAssistManagerImpl;
import cz.muni.fi.pb138.medassist.backend.Utils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;

/**
 * Controller for operations related to doctor.
 *
 * @author Lenka
 */
@WebServlet(name = "Doctor", urlPatterns = {"/Doctor/*"})
public class DoctorController extends HttpServlet {

    private static final String xsl = "src/main/resources/form_to_html.xsl";
    //private static final String xsl = "/WEB-INF/form_to_html.xsl";

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
            case "/formCreatePage":
                request.getRequestDispatcher("/WEB-INF/formCreate.html").forward(request, response);
                break;
            default:
                listForms(request, response);
                break;
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
            case "/createForm":
                createForm(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Unknown action: " + action);
        }
    }

    /**
     * Returns MedAssistManagerImpl variable that was set as attribute in
     * InitListener class.
     *
     * @return MedAssistManagerImpl object for this context
     */
    private MedAssistManagerImpl getMedAssistManager() {
        return (MedAssistManagerImpl) getServletContext().getAttribute("medAssistManager");
    }

    /**
     * Method for creating list of arrays of all the forms. 
     * It contains id of the form and name of the form for each form.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    private void listForms(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            MedAssistManagerImpl manager = getMedAssistManager();
            manager.setCurrentDoctor(1);
            List<String[]> forms = manager.findAllForms();
            request.setAttribute("forms", forms);
            request.getRequestDispatcher("").forward(request, response); //TODO: write the correct page
        } catch (SAXException | XMLDBException | ParserConfigurationException ex) {
            Logger.getLogger(DoctorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method for creating a new form and storing it in the database.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    private void createForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String form = Utils.formatOutput(request.getParameter("form"));

        try (InputStream is = new ByteArrayInputStream(form.getBytes());) {
            MedAssistManagerImpl manager = getMedAssistManager();
            manager.setCurrentDoctor(1);

            Document document = Utils.newDocumentInstance(is);

            manager.createNewForm(document);
        } catch (ParserConfigurationException | SAXException | XMLDBException | TransformerException ex) {
            Logger.getLogger(DoctorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
