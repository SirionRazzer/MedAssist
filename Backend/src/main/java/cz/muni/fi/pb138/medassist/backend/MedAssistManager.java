/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.medassist.backend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;

/**
 * Interface for MedAssist application with needed operations.
 * @author Lenka
 */
public interface MedAssistManager {
    
    final List<Integer> doctors = new ArrayList<>();
    
    /**
     * Creates new doctorID_form.xml file and initialize root tag of this file.
     * DoctorID is next unused integer from formFiles list. In generated file 
     * name, doctor id has exactly 9 digits. If original id has less digits,
     * zeros are added in front of it (e.g. 000000001_form.xml).
     *
     * @return returns new generated doctorID
     * @throws java.lang.Exception
     */
    public int createNewFormXML() throws Exception;
    
    /**
     * Creates new form file for the doctor.
     * @param form correct xml file represented by DOM Document
     * @throws TransformerException 
     */
    public void createNewForm(Document form) throws TransformerException;
    
    /**
     * Finds all forms for current doctor.
     * @return two dimensional array containing id(first row) and name(second 
     *         row) for each form.
     */
    public List<String[]> findAllForms();
    
    /**
     * Gets form with given fid and transform it to HTML page using XSL stylesheet
     * @param fid ID of form we want to show
     * @param xsl stream source of the file that contains XSLT definition
     * @return string containing HTML page of given form
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     * @throws org.xmldb.api.base.XMLDBException
     */
    public String getFormAsHTML(int fid, StreamSource xsl) 
            throws ParserConfigurationException, SAXException, IOException, XMLDBException;
}
