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
public class Slide {
    private final String name;
    private final boolean dependency;
    private final int formID;
    
    public Slide(String name, boolean dependency, int formID) {
        this.name = name;
        this.dependency = dependency;
        this.formID = formID;
    }

    public String getName() {
        return name;
    }

    public boolean hasDependency() {
        return dependency;
    }    

    public int getFormID() {
        return formID;
    }
}
