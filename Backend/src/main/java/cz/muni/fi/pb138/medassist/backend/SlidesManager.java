/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.medassist.backend;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Lenka
 */
public class SlidesManager {
    
    private Document document;
    
    public SlidesManager(Document doc) {
        this.document = doc;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
    
    public int createSlide(Slide slide, int formID) {
        Element slides = getFormSlidesElement(slide, formID);
        int sid = document.getElementsByTagName("slide").getLength() + 1;
        
        Element newSlide = document.createElement("slide");
        newSlide.setAttribute("sid", String.valueOf(sid));
        newSlide.setAttribute("dependency", String.valueOf(slide.hasDependency()));
        
        Element slideName = document.createElement("name");
        slideName.appendChild(document.createTextNode(slide.getName()));
        newSlide.appendChild(slideName);
        
        Element questions = document.createElement("questions");
        newSlide.appendChild(questions);
        
        slides.appendChild(newSlide);
        return sid;
    }
    
    /**
     * Finds slides element in correct form to create new slide of the form.
     * @param slide new slide we want to add to the form
     * @return Element slides from the correct form
     */
    private Element getFormSlidesElement(Slide slide, int formID) {
        Element slides = null;
        NodeList forms = document.getElementsByTagName("form");
        int i = 0;
        while (forms.item(i).getAttributes().getNamedItem("fid") != null) {
            Attr fid = (Attr) forms.item(i).getAttributes().getNamedItem("fid");
            if (Long.valueOf(fid.getValue()) == formID) {
                slides = (Element) ((Element) forms.item(i)).getElementsByTagName("slides").item(0);
                break;
            }
            i ++;
        }
        if (slides == null) {
            System.err.println("wrong");
            System.exit(-1);
        }
        return slides;
    }
}
