import pandas as pd
import ast

def categorize_recipes_by_diet(input_csv_path="recipes_with_allergens.csv", output_csv_path="recipe_db.csv"):
    """
    Loads a CSV of recipes, categorizes them as vegetarian or vegan based on
    their allergens, and saves the updated DataFrame to a new CSV.

    Args:
        input_csv_path (str): The path to the input CSV file containing recipe
                              information, including an 'allergens' column.
        output_csv_path (str): The path where the new CSV file with dietary
                               categories will be saved.
    """
    try:
        df = pd.read_csv(input_csv_path)
        print(f"Successfully loaded '{input_csv_path}'.")

        carnivore_allergens = [
            'Anchovies', 'Chicken', 'Fish', 'Pork', 'Shellfish'
        ]
        carnivore_allergens_lower = [a.lower() for a in carnivore_allergens]

        non_vegan_vegetarian_allergens = [
            'Dairy', 'Eggs', 'Ghee', 'Milk'
        ]
        non_vegan_vegetarian_allergens_lower = [a.lower() for a in non_vegan_vegetarian_allergens]

        if 'allergens' not in df.columns:
            print("Error: The input CSV must contain an 'allergens' column.")
            return

        df['is_vegetarian'] = False
        df['is_vegan'] = False

        def check_dietary_compliance(row):
            recipe_allergens = []
            allergens_data = row['allergens']

            if pd.isna(allergens_data) or str(allergens_data).strip() == '':
                recipe_allergens = []
            elif isinstance(allergens_data, str):
                recipe_allergens = [allergen.strip().lower() for allergen in allergens_data.split(',')]
            else:
                try:
                    parsed_list = ast.literal_eval(str(allergens_data))
                    recipe_allergens = [str(a).strip().lower() for a in parsed_list]
                except (ValueError, SyntaxError):
                    print(f"Warning: Unexpected allergen format for row. Data: '{allergens_data}'. Treating as empty list.")
                    recipe_allergens = []

            is_vegetarian = True
            for allergen in recipe_allergens:
                if allergen in carnivore_allergens_lower:
                    is_vegetarian = False
                    break
            is_vegan = is_vegetarian
            if is_vegan:
                for allergen in recipe_allergens:
                    if allergen in non_vegan_vegetarian_allergens_lower:
                        is_vegan = False
                        break

            return pd.Series({'is_vegetarian': is_vegetarian, 'is_vegan': is_vegan})

        df[['is_vegetarian', 'is_vegan']] = df.apply(check_dietary_compliance, axis=1)

        df.to_csv(output_csv_path, index=False)
        print(f"Successfully saved the updated DataFrame to '{output_csv_path}'.")

    except FileNotFoundError:
        print(f"Error: The file '{input_csv_path}' was not found. Please ensure it exists in the same directory.")
    except Exception as e:
        print(f"An unexpected error occurred: {e}")


if __name__ == '__main__':
    categorize_recipes_by_diet()