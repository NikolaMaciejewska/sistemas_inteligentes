package org.example.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.models.Recipe;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class RecipeLoader {

    public static List<Recipe> loadRecipes() {
        try {
            InputStream is = RecipeLoader.class.getClassLoader().getResourceAsStream("data/recipes.json");

            if (is == null) {
                System.err.println("Could not find recipes.json");
                return Collections.emptyList();
            }

            InputStreamReader reader = new InputStreamReader(is);
            Type recipeListType = new TypeToken<List<Recipe>>() {}.getType();
            return new Gson().fromJson(reader, recipeListType);

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}