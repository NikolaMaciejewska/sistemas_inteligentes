package org.example.models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserPreferences {
    private List<String> allergies;
    private List<String> dislikedIngredients;
    private String typePreference; // "vegan", "spicy", etc.
    private int maxCalories;
    private String difficulty;


    public UserPreferences(List<String> allergies, List<String> dislikedIngredients, String typePreference,
                           int maxCalories, String difficulty) {
        this.allergies = allergies;
        this.dislikedIngredients = dislikedIngredients;
        this.typePreference = typePreference;
        this.maxCalories = maxCalories;
        this.difficulty = difficulty;
    }
}