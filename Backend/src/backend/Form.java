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
public class Form {
    
    private final String name;
    private int numberOfSlides;
    
    public Form (String name) {
        this.name = name;
        this.numberOfSlides = 1;
    }

    public int getNumberOfSlides() {
        return numberOfSlides;
    }

    public void setNumberOfSlides(int numberOfSlides) {
        this.numberOfSlides = numberOfSlides;
    }

    public String getName() {
        return name;
    }
    
}
