/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.medassist.backend;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Lenka
 */
public class FormsManager {
    
    private Document document;
    
    public FormsManager (Document document){
        this.document = document;
    }
    
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
    
    public int createForm(Form form) {
        Node rootNode = document.getFirstChild();
        int fid = document.getElementsByTagName("form").getLength() + 1;     
        
        Element formRoot = document.createElement("form");
        formRoot.setAttribute("fid", String.valueOf(fid));
        
        Element formName = document.createElement("name");
        formName.appendChild(document.createTextNode(form.getName()));
        formRoot.appendChild(formName);
        
        Element slides = document.createElement("slides");        
        formRoot.appendChild(slides);
        rootNode.appendChild(formRoot);
        
        return fid;
    }
}
