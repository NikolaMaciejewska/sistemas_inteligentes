package org.example.models;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RecipeBook {
    private List<Recipe> recipes;

    public RecipeBook() {
        this.recipes = new ArrayList<>();
    }

    public void addRecipe(Recipe recipe) {
        this.recipes.add(recipe);
    }
}