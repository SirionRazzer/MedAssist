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
public class SlideManager {
    
    private int nextSID;
    private final Element slidesElement;
    private Document document;
    private int questionsCount;
    
    public SlideManager(int nextSID, Element slidesElement, Document doc, int questionsCount) {
        this.nextSID = nextSID;
        this.slidesElement = slidesElement;
        this.document = doc;
        this.questionsCount = questionsCount;
    }
    
    public Element createSlide(Slide slide) {
        Element newSlide = document.createElement("slide");
        newSlide.setAttribute("sid", String.valueOf(nextSID));
        newSlide.setAttribute("dependency", String.valueOf(slide.hasDependency()));
        
        Element slideName = document.createElement("name");
        slideName.appendChild(document.createTextNode(slide.getName()));
        newSlide.appendChild(slideName);
        
        Element questions = document.createElement("questions");
        int qid = document.getElementsByTagName("question").getLength() + 1;
        QuestionManager manager = new QuestionManager(qid, questions, document, 1);
        for (int i = 0; i < questionsCount; i++) {
            Question question = new Question((i + 1) + ". question", QuestionType.CHECKBOX);
            questions = manager.createQuestion(question);
        }
        newSlide.appendChild(questions);
        
        slidesElement.appendChild(newSlide);
        nextSID += 1;
        return slidesElement;
    }
}
