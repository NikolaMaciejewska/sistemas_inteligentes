package org.example.utils;

import com.google.gson.*;
import org.example.models.Recipe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class SpoonacularFetcher {

    private static final String API_KEY = "6b6ac34a82914611ae34d6f12caa3e24";
    private static final String BASE_URL = "https://api.spoonacular.com/recipes/complexSearch";

    public static List<Recipe> fetchRecipesFromAPI(String query, int number) throws IOException {
        String urlStr = BASE_URL + "?query=" + query + "&number=" + number + "&addRecipeInformation=true&apiKey=" + API_KEY;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int status = conn.getResponseCode();
        if (status != 200) {
            throw new IOException("Failed to fetch from API. Status: " + status);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }

        conn.disconnect();
        String response = responseBuilder.toString();

        return parseRecipesFromJson(response);
    }

    private static List<Recipe> parseRecipesFromJson(String json) {
        List<Recipe> recipes = new ArrayList<>();
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray results = root.getAsJsonArray("results");

        for (JsonElement el : results) {
            JsonObject obj = el.getAsJsonObject();
            Recipe recipe = new Recipe();

            recipe.setRecipe_name(obj.get("title").getAsString());
            recipe.setCalories((int) (Math.random() * 500));
            recipe.setPrep_time(obj.get("readyInMinutes").getAsInt());

            List<String> tags = new ArrayList<>();
            if (obj.has("diets")) {
                for (JsonElement tag : obj.getAsJsonArray("diets")) {
                    tags.add(tag.getAsString());
                }
            }
            recipe.setTags((ArrayList<String>) tags);

            recipes.add(recipe);
        }

        return recipes;
    }
}
