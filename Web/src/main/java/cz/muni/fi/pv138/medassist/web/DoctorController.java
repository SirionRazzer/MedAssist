/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv138.medassist.web;

import cz.muni.fi.pb138.medassist.backend.MedAssistManagerImpl;
import cz.muni.fi.pb138.medassist.backend.Utils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;

/**
 * Controller for operations related to doctor.
 * @author Lenka
 */
@WebServlet(name = "Doctor", urlPatterns = {"/Doctor/*"})
public class DoctorController extends HttpServlet {

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
            case "/printForm":
                printForm(request, response);
                break;
            case "/listForms":
                listForms(request, response);
                break;
            case "/formCreate":
                request.getRequestDispatcher("/WEB-INF/formCreate.html").forward(request, response);
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
            String form = manager.getFormAsHTML(1);
            request.setAttribute("form", form);
            request.getRequestDispatcher("/WEB-INF/1_form.jsp").forward(request, response);
        } catch (XMLDBException | SAXException | ParserConfigurationException ex) {
            System.err.println("Couldn't connect to the database.");
        }

    }

    private void listForms(HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
