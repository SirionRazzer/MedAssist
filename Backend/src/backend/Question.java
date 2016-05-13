/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

/**
 *
 * @author Lenka
 */
public class Question {
    
    private final String text;
    private final QuestionType type;
    
    public Question(String text, QuestionType type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public QuestionType getType() {
        return type;
    }
    
}
