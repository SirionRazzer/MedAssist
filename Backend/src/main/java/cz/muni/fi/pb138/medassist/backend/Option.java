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
public class Option {
    
    private final int questionID;
    private final String text;
    
    public Option(String text, int questionID) {
        this.questionID = questionID;
        this.text = text;
    }

    public int getQuestionID() {
        return questionID;
    }

    public String getText() {
        return text;
    }
    
}
