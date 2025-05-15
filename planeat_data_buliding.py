import pandas as pd
import matplotlib.pyplot as plt

# Step 1: Load the CSV file
df = pd.read_csv("newfood.csv")

# Step 2: Select relevant columns for nutrition-based recommendation
selected_cols = ["title", "calories", "Protein/g", "Fat/g", "Carbohydrates/g"]
df_selected = df[selected_cols]

# Step 3: Rename columns for clarity
df_selected.columns = ["name", "calories", "protein", "fat", "carbs"]

# Step 4: Visualize missing values before removal
missing = df_selected.isnull().sum()

plt.figure(figsize=(8, 5))
missing.plot(kind='barh', color='tomato')
plt.title("Missing Values (Before Removal)")
plt.xlabel("Number of Missing Values")
plt.grid(True, linestyle='--', alpha=0.5)
plt.tight_layout()
plt.savefig("missing_values_visual.png")  # Save the visualization result
plt.show()

# Step 5: Remove rows with missing values
df_clean = df_selected.dropna()

# Step 6: Save the cleaned data to a new CSV file
df_clean.to_csv("cleaned_food_nutrition.csv", index=False)
print("Saved successfully: cleaned_food_nutrition.csv")
print("Number of rows:", len(df_clean))

import pandas as pd
import matplotlib.pyplot as plt

# Step 1: Load the CSV file
df = pd.read_csv("cleaned_food_nutrition.csv")  # Uploaded file

# Step 2: Select only the relevant nutrient columns
nutrients = ["calories", "protein", "fat", "carbs"]
df_selected = df[["name"] + nutrients]

# Step 3: Define IQR-based outlier removal function
def remove_outliers_iqr(df, columns):
    df_filtered = df.copy()
    for col in columns:
        Q1 = df_filtered[col].quantile(0.25)
        Q3 = df_filtered[col].quantile(0.75)
        IQR = Q3 - Q1
        lower = Q1 - 1.5 * IQR
        upper = Q3 + 1.5 * IQR
        df_filtered = df_filtered[(df_filtered[col] >= lower) & (df_filtered[col] <= upper)]
    return df_filtered

# Step 4: Record the number of rows before removal
before = len(df_selected)

# Step 5: Remove outliers using IQR
df_cleaned = remove_outliers_iqr(df_selected, nutrients)

# Step 6: Show the number of rows after removal and summary
after = len(df_cleaned)
removed = before - after

print(f"Before removal: {before} rows")
print(f"After removal: {after} rows")
print(f"Removed: {removed} rows")

# Step 7: Show summary statistics after cleaning
print("\nSummary statistics after cleaning:")
print(df_cleaned[nutrients].describe())

# Step 8: Save the cleaned data
df_cleaned.to_csv("planeat_food_nutrition.csv", index=False)
print("\nSaved successfully: planeat_food_nutrition.csv")

!pip install uuid

import pandas as pd
import random
import uuid

# List of allergy items (excluding 'None')
allergy_items = [
    "Egg", "Milk", "Buckwheat", "Peanut", "Soybean", "Wheat", "Pine nut", "Walnut",
    "Crab", "Shrimp", "Squid", "Mackerel", "Shellfish", "Peach", "Tomato",
    "Chicken", "Pork", "Beef", "Sulfurous acid"
]

# Function to categorize BMI based on Korean Obesity Association standards
def get_bmi_category(bmi):
    if bmi < 18.5:
        return "Underweight"
    elif bmi < 23:
        return "Normal"
    elif bmi < 25:
        return "Overweight"
    else:
        return "Obese"

# Function to generate one user record
def generate_user(has_allergy=True):
    gender = random.choice(["male", "female"])
    age = random.randint(6, 16)

    # Set height and weight based on age and gender
    if age <= 13:
        height = random.uniform(125, 150) if gender == "male" else random.uniform(123, 147)
        weight = random.uniform(25, 55) if gender == "male" else random.uniform(23, 50)
    else:
        height = random.uniform(160, 175) if gender == "male" else random.uniform(155, 170)
        weight = random.uniform(55, 75) if gender == "male" else random.uniform(50, 65)

    bmi = weight / ((height / 100) ** 2)
    bmi_category = get_bmi_category(bmi)

    # Set allergies
    if has_allergy:
        allergy_count = random.randint(1, 3)
        allergies = ", ".join(random.sample(allergy_items, allergy_count))
    else:
        allergies = "None"

    return {
        "user_id": str(uuid.uuid4()),
        "gender": gender,
        "age": age,
        "height_cm": round(height, 1),
        "weight_kg": round(weight, 1),
        "bmi": round(bmi, 2),
        "bmi_category": bmi_category,
        "location": "South Korea",
        "meals_per_day": random.randint(0, 3),
        "hunger_cycle_min": random.randint(5, 180),
        "can_cook": random.randint(0, 1),
        "allergies": allergies
    }

# Generate 5000 users with random allergy setting
users = [generate_user() for _ in range(5000)]
random.shuffle(users)

# Create DataFrame
df = pd.DataFrame(users)

# Save to CSV
df.to_csv("vertex_user_info_dummy.csv", index=False)
print("Saved successfully: vertex_user_info_dummy.csv")

# Show basic statistics
print("\nBasic statistics summary:")
print(df[["age", "height_cm", "weight_kg", "bmi", "hunger_cycle_min"]].describe())

# Show BMI category distribution
print("\nBMI category distribution:")
print(df["bmi_category"].value_counts())

# Count users with no allergy
print("\nNumber of users with 'None' allergy:")
print((df["allergies"] == "None").sum())

import pandas as pd
import random
import re

# Load the uploaded CSV files
user_df = pd.read_csv("vertex_user_info_dummy.csv")
food_df = pd.read_csv("planeat_food_nutrition.csv")

# Preprocessing: create lowercase name column
food_df["name_lower"] = food_df["name"].fillna("").str.lower()

# Filter food candidates based on user conditions
def get_food_candidates(user):
    bmi = user["bmi_category"]
    can_cook = user["can_cook"]

    # Handle allergy string
    allergies_str = str(user.get("allergies", "") or "None")
    allergies = [a.strip().lower() for a in allergies_str.split(",")] if allergies_str != "None" else []

    df = food_df.copy()

    # BMI-based filtering
    if bmi == "Underweight":
        df = df[(df["calories"] >= 500) & (df["carbs"] >= 30)]
    elif bmi == "Normal":
        df = df[(df["calories"] >= 400) & (df["calories"] <= 600)]
    elif bmi == "Overweight":
        df = df[(df["calories"] >= 300) & (df["calories"] <= 450) & (df["protein"] >= 10)]
    elif bmi == "Obese":
        df = df[(df["calories"] <= 400) & (df["protein"] >= 15) & (df["fat"] <= 10)]

    # Filter by cooking ability
    if can_cook == 0 and "difficulty" in df.columns:
        df = df[df["difficulty"].isin(["easy", "no-cook"])]

    # Filter out foods containing allergens (by name)
    if allergies:
        pattern = "|".join([re.escape(a) for a in allergies])
        df = df[~df["name_lower"].str.contains(pattern)]

    return df

# Generate training data
training_rows = []
for _, user in user_df.iterrows():
    candidates = get_food_candidates(user)
    if not candidates.empty:
        food = candidates.sample(n=1).iloc[0]
        training_rows.append({
            # User information
            "age": user["age"],
            "gender": user["gender"],
            "bmi_category": user["bmi_category"],
            "can_cook": user["can_cook"],
            "meals_per_day": user["meals_per_day"],
            "hunger_cycle_min": user["hunger_cycle_min"],
            "allergies": user["allergies"],
            # Food information
            "food_calories": food["calories"],
            "food_protein": food["protein"],
            "food_fat": food["fat"],
            "food_carbs": food["carbs"],
            "food_difficulty": food["difficulty"] if "difficulty" in food else "unknown",
            # Label
            "label": food["name"]
        })

# Save the result
training_df = pd.DataFrame(training_rows)
training_df.to_csv("training_data.csv", index=False)
print("Saved successfully: training_data.csv")
training_df.head()

import pandas as pd
import matplotlib.pyplot as plt

# Load training data
df = pd.read_csv("training_data.csv")

# Use a sample of 500 for visualization
sample_df = df.sample(n=500, random_state=42)

# Visualize recommended food label distribution by BMI category
sample_df.groupby(["bmi_category", "label"]).size().unstack().fillna(0).T.plot(
    kind="bar", stacked=True, figsize=(14, 7), width=0.9
)
plt.title("Recommended Food Distribution by BMI Category (Sample of 500)", fontsize=14)
plt.xlabel("Food Label")
plt.ylabel("Recommendation Count")
plt.legend(title="BMI Category")
plt.xticks(rotation=90)
plt.tight_layout()
plt.show()

# Show top 20 most frequent food labels
top_labels = sample_df["label"].value_counts().nlargest(20).index
top_df = sample_df[sample_df["label"].isin(top_labels)]

# Plot BMI distribution for top 20 food labels
top_df.groupby(["bmi_category", "label"]).size().unstack().fillna(0).T.plot(
    kind="bar", stacked=True, figsize=(14, 7)
)
plt.title("BMI Distribution by Top 20 Recommended Foods")
plt.xlabel("Food Label")
plt.ylabel("Recommendation Count")
plt.legend(title="BMI Category")
plt.xticks(rotation=75)
plt.tight_layout()
plt.show()

import pandas as pd

# Load training data
df = pd.read_csv('training_data.csv', encoding='ISO-8859-1')

# Get sorted list of unique food labels
unique_labels = sorted(df['label'].dropna().unique())

# Print total number of unique labels
print("Total number of food labels:", len(unique_labels))

# Print each label
for label in unique_labels:
    print(label)

import pandas as pd

# Load the CSV file
df = pd.read_csv("traintrain.csv", encoding='ISO-8859-1')

# Define a list of known ingredients
ingredients_list = [
    "Potato", "Chili pepper", "Lemon", "Rice", "Sesame",
    "Seaweed", "Anchovy", "Carrot", "Napa cabbage", "Flour",
    "Tofu", "Garlic", "Chives", "Apple", "Egg", "Milk", "Buckwheat",
    "Peanut", "Soybean", "Wheat", "Pine nut", "Walnut", "Crab",
    "Shrimp", "Squid", "Mackerel", "Shellfish", "Peach", "Tomato",
    "Chicken", "Pork", "Beef", "Fish", "Sulfurous acid"
]

# Function to extract ingredients from food label
def extract_ingredients(title):
    matched = [ingredient for ingredient in ingredients_list if ingredient.lower() in title.lower()]
    matched += [None] * (3 - len(matched))  # Pad with None if less than 3 ingredients found
    return pd.Series(matched[:3])

# Apply extraction and create new columns
df[['Ingredients1', 'Ingredients2', 'Ingredients3']] = df['label'].apply(extract_ingredients)

# Display sample results
print(df[['label', 'Ingredients1', 'Ingredients2', 'Ingredients3']].head())

# Save the updated dataframe
df.to_csv("training_data_with_ingredients.csv", index=False)
print("Saved successfully: training_data_with_ingredients.csv")

import pandas as pd
import random

# Load the CSV file
df = pd.read_csv("training_data_with_ingredients.csv", encoding='ISO-8859-1')

# Add a new column 'hunger_cycle_min' with random values between 5 and 180
df["hunger_cycle_min"] = [random.randint(5, 180) for _ in range(len(df))]

# Save the updated DataFrame to a new file
df.to_csv("final_training_data.csv", index=False)
print("Saved successfully: final_training_data.csv")

import pandas as pd

# Load the CSV file
df = pd.read_csv("final_training_data.csv")

# Display dataset summary
print("Dataset Summary:")
print(df.info())

# Preview the first few rows
print("\nSample Data:")
print(df.head())

# Check for missing values in each column
print("\nMissing Values:")
print(df.isnull().sum())

import pandas as pd

# Load the dataset
df = pd.read_csv("final_training_data.csv", encoding='ISO-8859-1')

# Count occurrences for each food label
label_counts = df['label'].value_counts()

# Filter labels that appear 2 times or fewer
rare_labels = label_counts[label_counts <= 2]

# Print results
print(f"Total number of unique labels: {len(label_counts)}")
print(f"Number of labels with 2 or fewer instances: {len(rare_labels)}")
print("\nList of rare food labels (<= 2 occurrences):")
print(rare_labels)

# 11. Test user input
test_user = pd.DataFrame([{
    'age': 12, 'gender': 1, 'bmi_category': 2, 'can_cook': 0,
    'meals_per_day': 5, 'hunger_cycle_min': 60,
    'food_calories': 0, 'food_protein': 0, 'food_fat': 0, 'food_carbs': 0,
    'food_difficulty': 3, 'allergies': 'Milk, Egg, Wheat',
    'available_ingredients': ['Potato', 'Carrot', 'Rice']
}])

# 12. Get recommendation
result = recommend(test_user, model, scaler, label_encoder)
print("Final Recommendation:\n", json.dumps(result, indent=2, ensure_ascii=False, default=float))

"""# Recommendation System Experiment - Successed"""

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder, StandardScaler
from xgboost import XGBClassifier
from sklearn.metrics import classification_report, accuracy_score, top_k_accuracy_score
import time
import json

# 1. Load dataset
df = pd.read_csv("final_training_data.csv", encoding='ISO-8859-1')

# 2. Basic preprocessing
for col in ["Ingredients1", "Ingredients2", "Ingredients3"]:
    df[col] = df[col].fillna("None")
df['allergies'] = df['allergies'].fillna('')

# 3. Encode categorical variables
cat_cols = ['gender', 'bmi_category', 'food_difficulty']
for col in cat_cols:
    df[col] = LabelEncoder().fit_transform(df[col])

# 4. Define features and label
feature_cols = [
    'age', 'gender', 'bmi_category', 'can_cook', 'meals_per_day',
    'hunger_cycle_min', 'food_calories', 'food_protein', 'food_fat', 'food_carbs',
    'food_difficulty'
]
X = df[feature_cols]
y = df['label']

# 5. Split dataset
X_train, X_test, y_train_raw, y_test_raw = train_test_split(
    X, y, test_size=0.2, random_state=42
)

# 6. Encode labels
label_encoder = LabelEncoder()
y_train = label_encoder.fit_transform(y_train_raw)
test_mask = y_test_raw.isin(label_encoder.classes_)
X_test = X_test[test_mask]
y_test_raw = y_test_raw[test_mask]
y_test = label_encoder.transform(y_test_raw)

# 7. Scale features
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

# 8. Train model
start = time.time()
model = XGBClassifier(n_estimators=100, max_depth=8, learning_rate=0.1, eval_metric='mlogloss')
model.fit(X_train_scaled, y_train)
print("Training time:", round(time.time() - start, 2), "s")

# 9. Evaluate model
y_pred = model.predict(X_test_scaled)
print("Accuracy:", accuracy_score(y_test, y_pred))
print("Classification Report:\n", classification_report(y_test, y_pred))
print("Top-3 Accuracy:", top_k_accuracy_score(
    y_test, model.predict_proba(X_test_scaled), k=3, labels=np.arange(len(label_encoder.classes_))
))

# 10. Recommendation function
def recommend(user_input_df, model, scaler, label_encoder):
    input_scaled = scaler.transform(user_input_df[feature_cols])
    probs = model.predict_proba(input_scaled)[0]
    label_indices = np.argsort(probs)[::-1]

    for idx in label_indices:
        try:
            label_name = label_encoder.inverse_transform([idx])[0]
        except ValueError:
            continue

        food_row = df[df['label'] == label_name].iloc[0]
        user_allergies = user_input_df.iloc[0]['allergies'].lower().split(',')
        if any(ing.lower().strip() in label_name.lower() for ing in user_allergies if ing):
            continue

        if user_input_df.iloc[0]['can_cook'] == 0:
            if any(
                ing in user_input_df.iloc[0].get("available_ingredients", [])
                for ing in [food_row['Ingredients1'], food_row['Ingredients2'], food_row['Ingredients3']]
                if ing != "None"
            ):
                probs[idx] *= 1.2

        hunger = user_input_df.iloc[0]['hunger_cycle_min']
        calories = food_row['food_calories']
        probs[idx] *= (1 + (hunger / 60) * (calories / 500) * 0.1)

        return {
            "label": label_name,
            "score": round(probs[idx], 4),
            "calories": calories,
            "carbs": food_row['food_carbs'],
            "protein": food_row['food_protein'],
            "fat": food_row['food_fat']
        }

    return None

"""# Training Code Organization"""

import pandas as pd
import numpy as np
import joblib
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder, StandardScaler
from xgboost import XGBClassifier
from sklearn.metrics import classification_report, accuracy_score, top_k_accuracy_score
import time

# 1. Load dataset
df = pd.read_csv("final_training_data.csv", encoding='ISO-8859-1')

# 2. Basic preprocessing
for col in ["Ingredients1", "Ingredients2", "Ingredients3"]:
    df[col] = df[col].fillna("None")
df['allergies'] = df['allergies'].fillna('')

# 3. Encode categorical variables
cat_cols = ['gender', 'bmi_category', 'food_difficulty']
for col in cat_cols:
    df[col] = LabelEncoder().fit_transform(df[col])

# 4. Define features and label
feature_cols = [
    'age', 'gender', 'bmi_category', 'can_cook', 'meals_per_day',
    'hunger_cycle_min', 'food_calories', 'food_protein', 'food_fat', 'food_carbs',
    'food_difficulty'
]
X = df[feature_cols]
y = df['label']

# 5. Split dataset
X_train, X_test, y_train_raw, y_test_raw = train_test_split(
    X, y, test_size=0.2, random_state=42
)

# 6. Encode labels
label_encoder = LabelEncoder()
y_train = label_encoder.fit_transform(y_train_raw)
test_mask = y_test_raw.isin(label_encoder.classes_)
X_test = X_test[test_mask]
y_test_raw = y_test_raw[test_mask]
y_test = label_encoder.transform(y_test_raw)

# 7. Scale features
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

# 8. Train model
start = time.time()
model = XGBClassifier(n_estimators=100, max_depth=8, learning_rate=0.1, eval_metric='mlogloss')
model.fit(X_train_scaled, y_train)
print("✅ Training time:", round(time.time() - start, 2), "s")

# 9. Evaluate model
y_pred = model.predict(X_test_scaled)
print("✅ Accuracy:", accuracy_score(y_test, y_pred))
print("✅ Top-3 Accuracy:", top_k_accuracy_score(
    y_test, model.predict_proba(X_test_scaled), k=3, labels=np.arange(len(label_encoder.classes_))
))
print("✅ Classification Report:")
print(classification_report(y_test, y_pred))

# 🔥 10. Save model, scaler, encoder, processed data
joblib.dump(model, "xgb_model.pkl")
joblib.dump(scaler, "scaler.pkl")
joblib.dump(label_encoder, "label_encoder.pkl")
df.to_csv("processed_data.csv", index=False)

print("🎉 All files saved: xgb_model.pkl, scaler.pkl, label_encoder.pkl, processed_data.csv")