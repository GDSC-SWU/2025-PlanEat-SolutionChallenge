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

# 10. Save model, scaler, encoder, processed data
joblib.dump(model, "xgb_model.pkl")
joblib.dump(scaler, "scaler.pkl")
joblib.dump(label_encoder, "label_encoder.pkl")
df.to_csv("processed_data.csv", index=False)

print("All files saved: xgb_model.pkl, scaler.pkl, label_encoder.pkl, processed_data.csv")