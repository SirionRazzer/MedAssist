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
public class Slide {
    private final String name;
    private final boolean dependency;
    
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
}
