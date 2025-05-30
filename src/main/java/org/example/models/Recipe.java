package org.example.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {
    private int idRecipe;
    private String recipe_name;
    private int prep_time; // in minutes
    private int cook_time; // in minutes
    private int total_time; // in minutes
    private int servings;
    private ArrayList<Ingredient> ingredients;
    private String directions;
    private double rating; // 1.0 --> 5.0
    private ArrayList<String> tags; // e.g., "vegan", "gluten-free"
    private int calories;
    
    public Recipe(int idRecipe, String recipe_name, int prep_time, int cook_time, 
            int total_time, int servings, String directions, double rating, int calories) {
        this.idRecipe = idRecipe;
        this.recipe_name = recipe_name;
        this.prep_time = prep_time;
        this.cook_time = cook_time;
        this.total_time = total_time;
        this.servings = servings;
        this.directions = directions;
        this.rating = rating;
        this.calories = calories;
        this.ingredients = new ArrayList<>();
        this.tags = new ArrayList<>();
    }
    
    public Recipe() {
        
    }

    public int getIdRecipe() {
        return idRecipe;
    }

    public void setIdRecipe(int idRecipe) {
        this.idRecipe = idRecipe;
    }
    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    public int getPrep_time() {
        return prep_time;
    }

    public void setPrep_time(int prep_time) {
        this.prep_time = prep_time;
    }

    public int getCook_time() {
        return cook_time;
    }

    public void setCook_time(int cook_time) {
        this.cook_time = cook_time;
    }

    public int getTotal_time() {
        return total_time;
    }

    public void setTotal_time(int total_time) {
        this.total_time = total_time;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }
    
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
    
    public ArrayList<String> getTags() {
        return tags;
    }
    
    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

}
