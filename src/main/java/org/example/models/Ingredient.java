package org.example.models;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private int idIngredient;
    private String name;
    private String category; // lácteos, carnes, vegetales

    public Ingredient(int idIngredient, String name, String category) {
        this.idIngredient = idIngredient;
        this.name = name;
        this.category = category;
    }
    
    public Ingredient(){
        
    }
    
    public int getIdIngredient() {
        return idIngredient;
    }

    public void setIdIngredient(int idIngredient) {
        this.idIngredient = idIngredient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
}