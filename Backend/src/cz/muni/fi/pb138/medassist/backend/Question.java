/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.medassist.backend;

/**
 *
 * @author Lenka
 */
public class Question {
    
    private final String text;
    private final QuestionType type;
    private final int slideID;
    
    public Question(String text, QuestionType type, int slideID) {
        this.text = text;
        this.type = type;
        this.slideID = slideID;
    }

    public String getText() {
        return text;
    }

    public QuestionType getType() {
        return type;
    }

    public int getSlideID() {
        return slideID;
    }
    
}
