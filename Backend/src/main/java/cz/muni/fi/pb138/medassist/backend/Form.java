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
public class Form {
    
    private final String name;
    private final List<Slide> slides = new ArrayList<>();
    
    public Form (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Slide> getSlides() {
        return slides;
    }
    
    public void addSlide(Slide slide) {
        if ( slide == null) {
            throw new IllegalArgumentException("Argument slide is null.");
        }
        slides.add(slide);
    }
    
}
