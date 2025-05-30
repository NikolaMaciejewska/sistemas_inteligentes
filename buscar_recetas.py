import csv
import json
import sys
import re
import io

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

def extraer_calorias(nutrition_str):
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


def buscar_recetas(parametros_json, ruta_csv='dataprocessing/recipes_with_allergens.csv'):
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

            calories = extraer_calorias(nutrition)

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

if __name__ == '__main__':
    parametros_json = sys.stdin.read()
    if not parametros_json:
        print("Falta el parámetro JSON")
        sys.exit(1)

    recetas = buscar_recetas(parametros_json)

    for r in recetas:
        print(f"\nReceta: {r['recipe_name']}")
        print(f"Ingredientes: {r['ingredients']}")
        print(f"Rating: {r['rating']}")
        print(f"Prep Time: {r['prep_time']}")
        print(f"Calories: {r['calories']}")
        print(f"Directions:\n{r['directions']}")
