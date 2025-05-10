package org.example.models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Recipe {
    private String name;
    private List<Ingredient> ingredients;
    private int prepTimeMinutes;
    private String difficulty; // "easy", "medium", "hard"
    private int calories;
    private String type; // "sweet", "salty", "spicy", "vegan"
    private int servings;

    public Recipe(String name, List<Ingredient> ingredients, int prepTimeMinutes,
                  String difficulty, int calories, String type, int servings) {
        this.name = name;
        this.ingredients = ingredients;
        this.prepTimeMinutes = prepTimeMinutes;
        this.difficulty = difficulty;
        this.calories = calories;
        this.type = type;
        this.servings = servings;
    }
}
