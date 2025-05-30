import csv
import json
import sys
import re
import io

import pandas as pd

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

def _extract_calories(nutrition_str):
    try:
        fat_match = re.search(r'Total Fat (\d+)g', nutrition_str)
        carb_match = re.search(r'Total Carbohydrate (\d+)g', nutrition_str)
        protein_match = re.search(r'Protein (\d+)g', nutrition_str)

        fat = int(fat_match.group(1)) if fat_match else 0
        carbs = int(carb_match.group(1)) if carb_match else 0
        protein = int(protein_match.group(1)) if protein_match else 0

        calories = (fat * 9) + (carbs * 4) + (protein * 4)
        return calories
    except Exception as e:
        print(e)
        return 9999

def _convert_user_json_to_dict(json_recipes):
    """
    Converts the received-json file to dict.
    """
    if type(json_recipes) is str:
        user_parameters = json.loads(json_recipes)

    return user_parameters


def _load_recipe_db_as_df():
    """
    Loads the recipe_db as a df
    """
    recipe_df = pd.read_csv("recipe_db.csv")
    return recipe_df

def _exclude_on_boolean_keyword(recipe_row, keyword, req):
    """
    Excludes based on if the boolean keyword matches the boolean user requirement.
    """
    if recipe_row[keyword] is req:
        return True
    return False

def _extract_simple_string_list(recipe_row, keyword):
    unstructured_string = str(recipe_row[keyword])
    splitted_string = unstructured_string.split(",")
    return [str(x).lower().replace(" ", "") for x in splitted_string]

def _extract_ingredient_string_list(recipe_row, keyword):
    unstructured_string = str(recipe_row[keyword])
    splitted_string = unstructured_string.split(",")
    splitted_string = [x.split(" ") for x in splitted_string]
    return [str(x).lower().replace(" ", "") for x in splitted_string]

def _exclude_on_simple_stirng_list_based_requirements(recipe_row, keyword, req_list):
    """
    Here we expect a perfect match inbetween strings e.g. allergic information
    """
    recipe_row_string_list = _extract_simple_string_list(recipe_row, keyword)

    for req in req_list:
        if req.lower() in recipe_row_string_list:
            return False # Indicating e.g. allergen exists in recipe

    return True # Indicating e.g. no match in allergens -> recipe is fine for user

def _exclude_on_simple_string_list_percentage_based_requirements(recipe_row, keyword, req_list, miss_percentage = 0.2):
    """
    Here we expect a perfect match inbetween strings e.g. allergic information
    """
    misses = int(miss_percentage * len(req_list)) # Introduce miss-parameter to have 1 parameter
    recipe_row_string_list = _extract_ingredient_string_list(recipe_row, keyword)

    for req in req_list:
        if req.lower() in recipe_row_string_list:
            if misses <= 0:
                return False
            else:
                misses -= 1

    return True # Indicating e.g. no match in allergens -> recipe is fine for user

def _exclude_on_float_int_requirement(recipe_row, keyword, req):
    recipe_value = recipe_row[keyword]
    if str(recipe_value) == "nan":
        return True
    if keyword == "prep_time":
        recipe_value = str(recipe_value).split(" ")
        if len(recipe_value) == 2:
            recipe_value = recipe_value[0]
        if len(recipe_value) == 4:
            hours, minutes = recipe_value[0], recipe_value[2]
            recipe_value = hours * 60 + minutes
    if float(recipe_value) <= float(req):
        return True # Indicating the numerical requirement is given
    return False

def _handle_hard_requirements(recipe_df, user_parameters, recipes_in_question):
    """
    Returns an index-list of recipes that fulfill the hard requirements
    """
    allergic_recipes = []

    # Handling allergic information
    allergens_info = user_parameters["selectedAllergens"]
    if len(allergens_info) == 0:
        allergic_recipes = recipes_in_question
    else:
        for index in recipes_in_question:
            if _exclude_on_simple_stirng_list_based_requirements(recipe_df.iloc[index], "allergens", allergens_info):
                allergic_recipes.append(index)

    # Handling vegan, vegetarian and (carnivor)
    diet_pref_recipes = []
    vegan_req = user_parameters["vegan"]
    vegetarian_req = user_parameters["vegetarian"]

    # Handling Carnivor-diet
    if vegan_req is not True and vegetarian_req is not True:
        return allergic_recipes
    # Handling vegetarian-diet
    elif vegan_req is not True and vegetarian_req is True:
        for index in allergic_recipes:
            if _exclude_on_boolean_keyword(recipe_df.iloc[index], "is_vegetarian", True):
                diet_pref_recipes.append(index)
    # Handling vegan-diet
    elif (vegan_req is True and vegetarian_req is True) or (vegan_req is True and vegetarian_req is not True):
        for index in allergic_recipes:
            if _exclude_on_boolean_keyword(recipe_df.iloc[index], "is_vegan", True):
                diet_pref_recipes.append(index)

    return diet_pref_recipes


def _handle_soft_requirements_primary(recipe_df, user_parameters, recipes_in_question):
    """
    Returns an index-list of recipes that fulfill the soft but primary requirements
    """
    # Handle time-req
    time_recipes = []
    time_req = user_parameters["max_total_time"]
    for index in recipes_in_question:
        if _exclude_on_float_int_requirement(recipe_df.iloc[index], "prep_time", time_req):
            time_recipes.append(index)

    return time_recipes


def _handle_soft_requirements_secondary(recipe_df, user_parameters, recipes_in_question):
    """
    Returns an index-list of recipes that fulfill the soft and secondary requirements
    """
    # Handle ingredients
    ingredient_recipes = []
    ingredient_list = user_parameters["ingredients"]

    for index in recipes_in_question:
        if _exclude_on_simple_string_list_percentage_based_requirements(recipe_df.iloc[index], "ingredients", ingredient_list):
            ingredient_recipes.append(index)

    # Handle calories
    calory_recipes = []
    calory_req = str(user_parameters["max_calories"])

    for index in ingredient_recipes:
        recipe_calories = _extract_calories(str(recipe_df["nutrition"]))
        if int(calory_req) >= recipe_calories:
            calory_recipes.append(index)

    return calory_recipes

def _sort_diminish_by_rating(recipe_df, recipes_in_question, max_recipes):
    filtered_recipes = recipe_df.loc[recipes_in_question]
    sorted_recipes = filtered_recipes.sort_values(by='rating', ascending=False)
    top_recipe_indexes = sorted_recipes.head(max_recipes).index.tolist()
    return top_recipe_indexes

def _handle_bottleneck(recipe_df, user_parameters, recipes_in_question):
    """
    If necessary reduces the amount of recipes by rating
    """
    if len(recipes_in_question) <= user_parameters["number_of_recipes"]:
        return recipes_in_question
    else:
        return _sort_diminish_by_rating(recipe_df, recipes_in_question, user_parameters["number_of_recipes"])


def _convert_recipes_to_output_json(recipe_df, user_parameters, recipes_in_question):
    """
    Converst the selected recommended recipes to readable json-format
    """
    valid_indexes = [idx for idx in recipes_in_question if idx in recipe_df.index]

    if not valid_indexes:
        print("Warning: No valid recipe indexes found in 'recipes_in_question'. Returning empty JSON.")
        return json.dumps({})

    selected_recipes_df = recipe_df.loc[valid_indexes]
    selected_recipes_list_of_dicts = selected_recipes_df.to_dict(orient='records')
    output_json_dict = {}
    for i, recipe_dict in enumerate(selected_recipes_list_of_dicts):
        output_json_dict[i + 1] = recipe_dict
    new_output_dict = {}
    for r in output_json_dict:
        calories = _extract_calories(str(output_json_dict[r]["nutrition"]))
        new_output_dict[r] = output_json_dict[r]
        new_output_dict[r]["calories"] = calories
    return new_output_dict

def main(json_recipes):
    # Skeleton/Workflow of agent
    user_parameters = _convert_user_json_to_dict(json_recipes)
    recipe_df = _load_recipe_db_as_df()

    # We assume every recipe fulfills requirements in the start reducing the amount during the process
    recipes_in_question = [x for x in range(len(recipe_df))]

    # The Info-Hierarchy is to reduce the search space + make it more efficient + Have the actual recommendation work with least amount of parameters
    """
    Proposed Hierarchy of information:
        1. Hard requirements ->
            1. allergic_information (user_parameters), allergens (recipe_df)
            2. Carnivor, vegetarian, vegan ()
        2. Soft requirements but primary ->
            1. max_total_time (json), prep_time (db.csv)
            2. min_rating (json), rating (db.csv)
        3. Soft requiments but secondary ->
            1. ingredients (json + db.csv)
            2. max_calories (json + db.csv)
        4. Bottleneck ->
            1. number_of_recipes (json)
    """
    recipes_in_question = _handle_hard_requirements(recipe_df, user_parameters, recipes_in_question)
    recipes_in_question = _handle_soft_requirements_primary(recipe_df, user_parameters, recipes_in_question)
    recipes_in_question = _handle_soft_requirements_secondary(recipe_df, user_parameters, recipes_in_question)
    final_recipe_selection = _handle_bottleneck(recipe_df, user_parameters, recipes_in_question)

    return _convert_recipes_to_output_json(recipe_df, user_parameters, final_recipe_selection)


if __name__ == '__main__':
    parametros_json = sys.stdin.read()
    if not parametros_json:
        print("Falta el parámetro JSON")
        sys.exit(1)
    else:
        json_recipes = main(parametros_json)
        for r in json_recipes:
            print(f"\nRECIPE n°{r}: {json_recipes[r]['recipe_name']}")
            print(f"- Ingredients: {json_recipes[r]['ingredients']}")
            print(f"- Rating: {json_recipes[r]['rating']}")
            print(f"- Prep Time: {json_recipes[r]['prep_time']}")
            print(f"- Calories: {json_recipes[r]['calories']}")
            print(f"- Allergens: {json_recipes[r]['allergens']}")
            print(f"- Directions:\n{json_recipes[r]['directions']}")
