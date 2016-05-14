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
public class OptionsManager {
    private final Document document;
    
    public OptionsManager(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }
    
    public void createOption(Option option) {
        Element options = getQuestionOptionsElement(option);
        
        Element newOption = document.createElement("option");
        newOption.appendChild(document.createTextNode(option.getText()));
        
        options.appendChild(newOption);
    }
    
    /**
     * Finds options element in correct question to create new option in it.
     * @param option new slide we want to add to the form
     * @return Element options from the correct question
     */
    private Element getQuestionOptionsElement(Option option) {
        Element options = null;
        NodeList forms = document.getElementsByTagName("question");
        int i = 0;
        while (forms.item(i).getAttributes().getNamedItem("qid") != null) {
            Attr fid = (Attr) forms.item(i).getAttributes().getNamedItem("qid");
            if (Long.valueOf(fid.getValue()) == option.getQuestionID()) {
                options = (Element) ((Element) forms.item(i)).getElementsByTagName("options").item(0);
                break;
            }
            i ++;
        }
        if (options == null) {
            System.err.println("wrong");
            System.exit(-1);
        }
        return options;
    }
}
