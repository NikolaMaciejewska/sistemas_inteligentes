import csv
import json
import sys
import re
import io

import pandas as pd

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

def _extraer_calorias(nutrition_str):
    try:
        fat_match = re.search(r'Total Fat (\d+)g', nutrition_str)
        carb_match = re.search(r'Total Carbohydrate (\d+)g', nutrition_str)
        protein_match = re.search(r'Protein (\d+)g', nutrition_str)

        fat = int(fat_match.group(1)) if fat_match else 0
        carbs = int(carb_match.group(1)) if carb_match else 0
        protein = int(protein_match.group(1)) if protein_match else 0

        calories = (fat * 9) + (carbs * 4) + (protein * 4)
        return calories
    except:
        return 9999


def _buscar_recetas(parametros_json, ruta_csv='dataprocessing/recipes_with_allergens.csv'):
    if isinstance(parametros_json, str):
        parametros = json.loads(parametros_json)
    else:
        parametros = parametros_json

    ingredientes_requeridos = [i.lower() for i in parametros.get("ingredients", [])]
    min_rating = float(parametros.get("min_rating", 0))
    max_prep_time = int(parametros.get("max_total_time", 9999))
    max_calories = int(parametros.get("max_calories", 9999))
    num_recipes = int(parametros.get("number_of_recipes", 5))

    recetas_resultado = []

    with open(ruta_csv, newline='', encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            ingredientes = row['ingredients'].lower() if row['ingredients'] else ""
            rating = float(row['rating']) if row['rating'] else 0.0
            nutrition = row.get('nutrition', '')
            directions = row.get('directions', 'No directions available.')

            try:
                prep_time = int(''.join(filter(str.isdigit, row['prep_time'])))
            except:
                prep_time = 9999

            calories = _extraer_calorias(nutrition)

            if all(ing in ingredientes for ing in ingredientes_requeridos):
                if rating >= min_rating and prep_time <= max_prep_time and calories <= max_calories:
                    recetas_resultado.append({
                        "recipe_name": row['recipe_name'],
                        "prep_time": row['prep_time'],
                        "ingredients": row['ingredients'],
                        "rating": row['rating'],
                        "calories": calories,
                        "directions": directions
                    })

    # Limit to number_of_recipes
    return recetas_resultado[:num_recipes]

def _convert_user_json_to_dict(json_recipes):
    """
    Converts the received-json file to dict.
    """
    if type(json_recipes) is str:
        json.loads(json_recipes)
    
    return json_recipes
    

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
    return [x.lower().replace(" ", "") for x in splitted_string]

def _exclude_on_simple_stirng_list_based_requirements(recipe_row, keyword, req_list):
    """
    Here we expect a perfect match inbetween strings e.g. allergic information
    """
    recipe_row_string_list = _extract_simple_string_list(recipe_row, keyword)

    for req in req_list:
        if req.lower() in recipe_row_string_list:
            return False # Indicating e.g. allergen exists in recipe
    
    return True # Indicating e.g. no match in allergens -> recipe is fine for user

def _exclude_on_float_int_requirement(recipe_row, keyword, req):
    if float(recipe_row[keyword]) <= float(req):
        return True # Indicating the numerical requirement is given
    return False

def _handle_hard_requirements(recipe_df, user_parameters, recipes_in_question):
    """
    Returns an index-list of recipes that fulfill the hard requirements
    """
    allergic_recipes = []

    # Handling allergic information
    allergens_info = user_parameters["allergic_information"]
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

    # Handle rating-req
    rating_recipes = []
    rating_req = user_parameters["min_rating"]
    for index in time_recipes:
        if _exclude_on_boolean_keyword(recipe_df.iloc[index], "rating", rating_req):
            rating_recipes.append(index)

    return rating_recipes


def _handle_soft_requirements_secondary(recipe_df, user_parameters, recipes_in_question):
    """
    Returns an index-list of recipes that fulfill the soft and secondary requirements
    """
    # TODO

def _handle_bottleneck(recipe_df, user_parameters, recipes_in_question):
    """
    If necessary reduces the amount of recipes by intelligent structuring
    """
    if recipes_in_question <= user_parameters["number_of_recipes"]:
        return recipes_in_question
    else:
        filler = "filler"
        # TODO
        # Idea would be to implement some intelligent structuring but only IF THERE IS TIME
        # Otherwise maybe dump it down to return by
        # 1. most rating
        # 2. least time
        # Hard-cut rest 

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
    return json.dumps(output_json_dict, indent=2)

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
    recipes_in_question = _handle_hard_requirements(recipe_df, user_parameters, recipes_in_question) # DONE
    recipes_in_question = _handle_soft_requirements_primary(recipe_df, user_parameters, recipes_in_question) # TODO
    recipes_in_question = _handle_soft_requirements_secondary(recipe_df, user_parameters, recipes_in_question) # TODO
    final_recipe_selection = _handle_bottleneck(recipe_df, user_parameters, recipes_in_question) # TODO
    
    return _convert_recipes_to_output_json(final_recipe_selection)


if __name__ == '__main__':
    json_file = sys.stdin.read()
    # This check might be flawed, due to behaviour of strings/json where empty ones are not perceived as None or False but rather as len(0), better change it!
    # TODO
    if not json_file:
        print("Falta el parámetro JSON")
        sys.exit(1)
    else:
        json_recipes = main(json_file)
        # TODO
        # Missing -> Turn json-recipes into string for better communication-transmission
        print(json_recipes)
