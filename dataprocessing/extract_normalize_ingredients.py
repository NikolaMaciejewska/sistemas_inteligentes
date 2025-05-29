# Script to extract the ingredients from the recipes_with_allergens.csv and return them in a list as normalized
import pandas as pd
import re

def normalize(s: str) -> str:
    s = s.lower()
    s = re.sub(r"[^a-z ]", "", s)
    s = re.sub(r"\s+", " ", s)
    s = s.strip()
    return s

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
        part = normalize(part)
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

def create_unique_ingredient_list():
    df = pd.read_csv("recipes_with_allergens.csv")
    
    all_ingredients = []
    for index, row in df.iterrows():
        recipes_ingredients = extract_ingredient_keywords(row['ingredients'])
        for ingredient in recipes_ingredients:
            all_ingredients.append(ingredient)


    unique_ingredients = list(set(all_ingredients))
    ingredient_df = pd.DataFrame({'ingredients': unique_ingredients})
    ingredient_df.to_csv("all_ingredients.csv")

if __name__ == '__main__':
    create_unique_ingredient_list()