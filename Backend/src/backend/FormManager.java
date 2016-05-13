/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Lenka
 */
public class FormManager {
    
    private Document document;
    private int slidesCount;
    
    public FormManager (Document document, int slidesCount){
        this.document = document;
        this.slidesCount = slidesCount;
    }
    
    public Document createForm(Form form) {
        Node rootNode = document.getFirstChild();
        int fid = document.getElementsByTagName("form").getLength() + 1;     
        
        Element formRoot = document.createElement("form");
        formRoot.setAttribute("fid", String.valueOf(fid));
        
        Element formName = document.createElement("name");
        formName.appendChild(document.createTextNode(form.getName()));
        formRoot.appendChild(formName);
        
        Element slides = document.createElement("slides");
        int sid = document.getElementsByTagName("slide").getLength() + 1;
        SlideManager manager = new SlideManager(sid, slides, document, 1);
        for (int i = 0; i < slidesCount; i++) {
            Slide newSlide = new Slide((i + 1) + ". slide", false);
            slides = manager.createSlide(newSlide);
        }
        
        formRoot.appendChild(slides);
        rootNode.appendChild(formRoot);
        
        return document;
    }
}
