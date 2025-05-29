import pandas as pd

df = pd.read_csv('downloads/allergens.csv')

all_allergens = set()
for allergens_string in df['Allergens']:
    if str(allergens_string) == "nan":
        continue
    allergens_list = [allergen.strip() for allergen in allergens_string.split(',')]
    all_allergens.update(allergens_list)

unique_allergens = sorted(list(all_allergens))
print(unique_allergens)