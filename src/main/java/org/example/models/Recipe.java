package org.example.models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Recipe implements Serializable {
    private String title;
    private List<Ingredient> ingredients;
    private int preparationTime; // in minutes
    private String difficulty; // "Easy", "Medium", "Hard"
    private int calories;
    private List<String> tags; // e.g., "vegan", "gluten-free"
    private int servings;

    public Recipe(String title, List<Ingredient> ingredients, int preparationTime,
                  String difficulty, int calories, List<String> tags, int servings) {
        this.title = title;
        this.ingredients = ingredients;
        this.preparationTime = preparationTime;
        this.difficulty = difficulty;
        this.calories = calories;
        this.tags = tags;
        this.servings = servings;
    }
}
