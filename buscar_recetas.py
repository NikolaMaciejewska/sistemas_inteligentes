# buscar_recetas.py
import csv
import json
import sys

def buscar_recetas(parametros_json, ruta_csv='dataprocessing/recipes_with_allergens.csv'):
    if isinstance(parametros_json, str):
        parametros = json.loads(parametros_json)
    else:
        parametros = parametros_json

    ingredientes_requeridos = [i.lower() for i in parametros.get("ingredientes", [])]
    min_rating = float(parametros.get("min_rating", 0))
    max_prep_time = int(parametros.get("max_prep_time", 9999))

    recetas_resultado = []

    with open(ruta_csv, newline='', encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            ingredientes = row['ingredients'].lower() if row['ingredients'] else ""
            rating = float(row['rating']) if row['rating'] else 0.0

            try:
                prep_time = int(''.join(filter(str.isdigit, row['prep_time'])))
            except:
                prep_time = 9999

            if all(ingrediente in ingredientes for ingrediente in ingredientes_requeridos):
                if rating >= min_rating and prep_time <= max_prep_time:
                    recetas_resultado.append({
                        "recipe_name": row['recipe_name'],
                        "prep_time": row['prep_time'],
                        "ingredients": row['ingredients'],
                        "rating": row['rating']
                    })

    return recetas_resultado

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
