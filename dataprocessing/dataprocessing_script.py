import kagglehub
import os
import shutil
import pandas as pd
import re

# Global variables
RECIPE_PATH = "./downloads/recipe.csv"
ALLERGENS_PATH = "./downloads/allergens.csv"

def execute_download():
    # Create the output folder
    os.makedirs("./downloads", exist_ok=True)

    # Download datasets
    recipe_dir = kagglehub.dataset_download("thedevastator/better-recipes-for-a-better-life")
    allergens_dir = kagglehub.dataset_download("uom190346a/food-ingredients-and-allergens")

    # Copy and rename files
    shutil.copy(os.path.join(recipe_dir, "recipes.csv"), RECIPE_PATH)
    shutil.copy(os.path.join(allergens_dir, "food_ingredients_and_allergens.csv"), ALLERGENS_PATH)

    print("Download and copy completed to ./downloads/")

def normalize_string(s):
    """Lowercases and strips a string."""
    return str(s).lower().strip()

def clean_last_word(word):
    """Remove non-alphabetic characters from the end of a word."""
    # Remove any non-alphabetic characters (keeping only a-z)
    return re.sub(r'[^a-z]+$', '', word)

def extract_ingredient_keywords(ingredients_str):
    """
    Takes a full ingredients string from the recipe and returns a list of ingredient keywords.
    Assumes ingredients are comma-separated.
    Grabs the last word before the comma (or a short phrase), ensuring it's alphabetic.
    """
    raw_parts = ingredients_str.split(",")
    keywords = []

    for part in raw_parts:
        part = normalize_string(part)
        words = part.split()
        if not words:
            continue
        # Heuristic: keep last one or two words, skip quantities
        if len(words) >= 2:
            last_word = clean_last_word(words[-1])
            if last_word:
                keywords.append(last_word)
        else:
            last_word = clean_last_word(words[-1])
            if last_word:
                keywords.append(last_word)

    return keywords

def build_allergen_lookup(allergens_df):
    """Creates a dictionary mapping normalized ingredient names to allergens."""
    allergens_df["Main Ingredient"] = allergens_df["Main Ingredient"].apply(normalize_string)
    return {
        row["Main Ingredient"]: row["Allergens"]
        for _, row in allergens_df.iterrows()
    }

def find_allergens_for_ingredients(ingredient_keywords, allergen_lookup):
    """Given a list of keywords, returns a set of matched allergens."""
    allergens_found = set()
    for keyword in ingredient_keywords:
        if keyword in allergen_lookup:
            allergens = allergen_lookup[keyword]
            allergens_found.update(a.strip() for a in str(allergens).split(","))
    return allergens_found

def glue_datasets():
    recipe_df = pd.read_csv(RECIPE_PATH)
    allergens_df = pd.read_csv(ALLERGENS_PATH)

    # Build allergen lookup dictionary
    allergen_lookup = build_allergen_lookup(allergens_df)

    # Add allergen column
    recipe_with_allergens_df = recipe_df.copy()
    recipe_with_allergens_df["allergens"] = ""

    for idx, row in recipe_with_allergens_df.iterrows():
        ingredient_keywords = extract_ingredient_keywords(str(row["ingredients"]))
        matched_allergens = find_allergens_for_ingredients(ingredient_keywords, allergen_lookup)
        recipe_with_allergens_df.at[idx, "allergens"] = ", ".join(sorted(matched_allergens))

    # Output sample
    print(recipe_with_allergens_df[["ingredients", "allergens"]].head())

    # Count how many recipes contain allergens
    count_with_allergens = recipe_with_allergens_df["allergens"].apply(lambda x: x.strip() != "").sum()
    print(f"\nNumber of recipes that contain at least one allergen: {count_with_allergens}")

    # Save the resulting DataFrame to a CSV
    recipe_with_allergens_df.to_csv("recipes_with_allergens.csv", index=False)

def main():
    # Check if we need to download csv-files
    if not os.path.isdir("downloads"):
        execute_download()
    
    glue_datasets()

if __name__ == '__main__':
    main()