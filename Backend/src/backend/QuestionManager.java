/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Lenka
 */
public class QuestionManager {
    private int nextQID;
    private final Element questionsElement;
    private final Document document;
    private final int optionsCount;
    
    public QuestionManager(int nextQID, Element questionsElement, 
            Document document, int optionsCount) {
        this.nextQID = nextQID;
        this.questionsElement = questionsElement;
        this.document = document;
        this.optionsCount = optionsCount;
    }
    
    public Element createQuestion(Question question) {
        Element newQuestion = document.createElement("question");
        newQuestion.setAttribute("qid", String.valueOf(nextQID));
        newQuestion.setAttribute("type", String.valueOf(question.getType()).toLowerCase());
        
        Element questionText = document.createElement("text");
        questionText.appendChild(document.createTextNode(question.getText()));
        newQuestion.appendChild(questionText);
        
        Element options = document.createElement("options");
        for (int i = 0; i < optionsCount; i++) {
            Element option = document.createElement("option");
            option.appendChild(document.createTextNode((i+1) + ". option"));
            options.appendChild(option);
        }
        
        newQuestion.appendChild(options);
        questionsElement.appendChild(newQuestion);
        nextQID += 1;
        return questionsElement;
    }
}
