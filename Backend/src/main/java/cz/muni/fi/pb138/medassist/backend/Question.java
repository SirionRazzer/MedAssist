/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.medassist.backend;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenka
 */
public class Question {
    
    private final String text;
    private final QuestionType type;
    private final List<Option> options = new ArrayList<>();
    
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

    public List<Option> getOptions() {
        return options;
    }

    public void addOption(Option option) {
        if ( option == null) {
            throw new IllegalArgumentException("Argument option is null.");
        }
        options.add(option);
    }
    
}
