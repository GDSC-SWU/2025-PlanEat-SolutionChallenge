from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import pandas as pd
import numpy as np
import joblib
import os

# Step 1: Set model path
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_DIR = os.path.join(BASE_DIR, "model")

# Step 2: Load models and data
model = joblib.load(os.path.join(MODEL_DIR, "xgb_model.pkl"))
scaler = joblib.load(os.path.join(MODEL_DIR, "scaler.pkl"))
label_encoder = joblib.load(os.path.join(MODEL_DIR, "label_encoder.pkl"))
df = pd.read_csv(os.path.join(MODEL_DIR, "processed_data.csv"))
desc_df = pd.read_csv(os.path.join(MODEL_DIR, "reason_withgemma.csv"), encoding='utf-8-sig')

# Step 3: Define feature columns
feature_cols = [
    'age', 'gender', 'bmi_category', 'can_cook', 'meals_per_day',
    'hunger_cycle_min', 'food_calories', 'food_protein', 'food_fat', 'food_carbs',
    'food_difficulty'
]

app = FastAPI()

# Step 4: Temporary in-memory storage for user recommendation history
user_history = {}  # {user_email: [recommended label list]}

# Step 5: Define user input model
class UserInput(BaseModel):
    user_email: str
    age: int
    gender: int
    height: float
    weight: float
    can_cook: int
    meals_per_day: int
    hunger_cycle_min: int
    allergies: str
    available_ingredients: list[str]

# Step 6: Calculate BMI category from height and weight
def calculate_bmi_category(height_cm: float, weight_kg: float) -> int:
    height_m = height_cm / 100
    bmi = weight_kg / (height_m ** 2)
    if bmi < 18.5:
        return 0  # Underweight
    elif bmi < 23:
        return 1  # Normal
    elif bmi < 25:
        return 2  # Overweight
    else:
        return 3  # Obese

# Step 7: Retrieve precomputed explanation from CSV
def get_precomputed_explanation(label_name: str) -> str:
    row = desc_df[desc_df["label"] == label_name]
    if not row.empty:
        return row.iloc[0]["recommendation_reason"]
    else:
        return "We're currently updating this content."

# Step 8: Define food recommendation endpoint
@app.post("/recommend")
def recommend_food(user: UserInput):
    bmi_category = calculate_bmi_category(user.height, user.weight)
    user_allergies = [a.strip().lower() for a in user.allergies.split(',') if a.strip()]

    # Merge user features with each food item
    food_df = df[['label', 'food_calories', 'food_protein', 'food_fat', 'food_carbs',
                  'food_difficulty', 'Ingredients1', 'Ingredients2', 'Ingredients3']].copy()
    food_df['age'] = user.age
    food_df['gender'] = user.gender
    food_df['bmi_category'] = bmi_category
    food_df['can_cook'] = user.can_cook
    food_df['meals_per_day'] = user.meals_per_day
    food_df['hunger_cycle_min'] = user.hunger_cycle_min

    # Keep only known labels
    known_labels = set(label_encoder.classes_)
    food_df = food_df[food_df['label'].isin(known_labels)].reset_index(drop=True)

    # Filter out foods that contain allergens
    def contains_allergen(row):
        ingredients = [str(row["Ingredients1"]).lower(), str(row["Ingredients2"]).lower(), str(row["Ingredients3"]).lower()]
        return any(allergy in ingredients for allergy in user_allergies)

    if user_allergies:
        food_df["has_allergy"] = food_df.apply(contains_allergen, axis=1)
        allergy_mask = ~food_df["has_allergy"]
    else:
        allergy_mask = pd.Series([True] * len(food_df))

    # Predict scores
    input_df = food_df[feature_cols]
    input_scaled = scaler.transform(input_df)
    probs = model.predict_proba(input_scaled)
    label_indices = label_encoder.transform(food_df['label'])
    scores = probs[np.arange(len(probs)), label_indices]

    # Apply allergy mask
    scores = scores * allergy_mask.values

    # Apply ingredient match weight if user cannot cook
    if user.can_cook == 0:
        def matches_ingredients(row):
            ingredients = [str(row["Ingredients1"]).lower(), str(row["Ingredients2"]).lower(), str(row["Ingredients3"]).lower()]
            return any(ing in [i.lower() for i in user.available_ingredients] for ing in ingredients)
        ingredient_match = food_df.apply(matches_ingredients, axis=1)
        scores *= (1 + 0.2 * ingredient_match.values)

    # Apply hunger-based weight
    hunger_factor = (1 + (user.hunger_cycle_min / 60) * (food_df['food_calories'] / 500) * 0.1)
    scores *= hunger_factor

    # Filter out previously recommended items for this user
    if user.user_email not in user_history:
        user_history[user.user_email] = []

    seen_mask = ~food_df['label'].isin(user_history[user.user_email])
    scores = scores * seen_mask.values

    # Select top recommendation
    top_idx = np.argmax(scores)
    top_score = scores[top_idx]

    if top_score == 0:
        raise HTTPException(status_code=404, detail="No suitable recommendation found.")

    best_row = food_df.iloc[top_idx]
    best_label = best_row['label']

    # Save to user history
    user_history[user.user_email].append(best_label)

    # Log result
    print("Received Request:", user.dict())
    print("Final Recommendation:", best_label)

    return {
        "label": best_label,
        "calories": float(best_row['food_calories']),
        "carbs": float(best_row['food_carbs']),
        "protein": float(best_row['food_protein']),
        "fat": float(best_row['food_fat']),
        "letter": get_precomputed_explanation(best_label)
    }
