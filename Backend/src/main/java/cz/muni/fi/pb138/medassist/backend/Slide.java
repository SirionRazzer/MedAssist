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
public class Slide {
    private final String name;
    private final boolean dependency;
    private final List<Question> questions = new ArrayList<>();
    
    public Slide(String name, boolean dependency) {
        this.name = name;
        this.dependency = dependency;
    }

    public String getName() {
        return name;
    }

    public boolean hasDependency() {
        return dependency;
    }    

    public List<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        if ( question == null) {
            throw new IllegalArgumentException("Argument question is null.");
        }
        questions.add(question);
    }
}
