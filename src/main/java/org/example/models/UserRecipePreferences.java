package org.example.models;
import java.io.Serializable;
import java.util.List;

public class UserRecipePreferences implements Serializable {
    private List<String> ingredients;
    private List<String> selectedAllergens;
    private int number_of_recipes;
    private int max_calories;
    private double min_rating;
    private int max_total_time;
    private boolean vegan;
    private boolean vegetarian;

    public UserRecipePreferences(){
        
    }
    
    public UserRecipePreferences(int number_of_recipes, int max_calories, 
            double min_rating, int max_total_time, boolean vegan, boolean vegetarian) {
        this.number_of_recipes = number_of_recipes;
        this.max_calories = max_calories;
        this.min_rating = min_rating;
        this.max_total_time = max_total_time;
        this.vegan = vegan;
        this.vegetarian = vegetarian;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSelectedAllergens() {
        return selectedAllergens;
    }

    public void setSelectedAllergens(List<String> selectedAllergens) {
        this.selectedAllergens = selectedAllergens;
    }

    public int getNumber_of_recipes() {
        return number_of_recipes;
    }

    public void setNumber_of_recipes(int number_of_recipes) {
        this.number_of_recipes = number_of_recipes;
    }

    public int getMax_calories() {
        return max_calories;
    }

    public void setMax_calories(int max_calories) {
        this.max_calories = max_calories;
    }

    public double getMin_rating() {
        return min_rating;
    }

    public void setMin_rating(double min_rating) {
        this.min_rating = min_rating;
    }

    public int getMax_total_time() {
        return max_total_time;
    }

    public void setMax_total_time(int max_total_time) {
        this.max_total_time = max_total_time;
    }

    public boolean isVegan() {
        return vegan;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }
    
}

