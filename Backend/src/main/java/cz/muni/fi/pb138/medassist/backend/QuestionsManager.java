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
public class QuestionsManager {
    private final Document document;
    
    public QuestionsManager(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }
    
    public int createQuestion(Question question, int slideID) {
        Element questions = getSlideQuestionsElement(question, slideID);
        int qid = document.getElementsByTagName("question").getLength() + 1;
        
        Element newQuestion = document.createElement("question");
        newQuestion.setAttribute("qid", String.valueOf(qid));
        newQuestion.setAttribute("type", String.valueOf(question.getType()).toLowerCase());
        
        Element questionText = document.createElement("text");
        questionText.appendChild(document.createTextNode(question.getText()));
        newQuestion.appendChild(questionText);
        
        Element options = document.createElement("options");
        newQuestion.appendChild(options);
        
        questions.appendChild(newQuestion);
        return qid;
    }
    
    /**
     * Finds questions element in correct slide to create new question in it.
     * @param question new slide we want to add to the form
     * @return Element questions from the correct slide
     */
    private Element getSlideQuestionsElement(Question question, int slideID) {
        Element questions = null;
        NodeList forms = document.getElementsByTagName("slide");
        int i = 0;
        while (forms.item(i).getAttributes().getNamedItem("sid") != null) {
            Attr sid = (Attr) forms.item(i).getAttributes().getNamedItem("sid");
            if (Long.valueOf(sid.getValue()) == slideID) {
                questions = (Element) ((Element) forms.item(i)).getElementsByTagName("questions").item(0);
                break;
            }
            i ++;
        }
        if (questions == null) {
            System.err.println("wrong");
            System.exit(-1);
        }
        return questions;
    }
}
