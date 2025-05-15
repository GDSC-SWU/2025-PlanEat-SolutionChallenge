import functions_framework
import requests
from flask import jsonify

# In-memory storage for user conversation history
user_histories = {}

# YouTube API key - Posted after modifying the app key according to the GitHub security policy
YOUTUBE_API_KEY = "issued api key"

# Food bank data with Google Maps links
raw_data = [
    {"district": "Mapo-gu", "address": "10 Sinchon-ro 26-gil, Mapo-gu, Seoul", "center": "Mapo Happy Sharing Food Market #2", "phone": "02-703-1377"},
    {"district": "Gangnam-gu", "address": "28 Jagok-ro 11-gil, Gangnam-gu, Seoul", "center": "Gangnam Food Support Center", "phone": "02-562-5377"},
]
foodbanks = [
    {**fb, "map": f"https://maps.google.com/?q={fb['address'].replace(' ', '+')}"}
    for fb in raw_data
]

# Function to search for a YouTube video using the YouTube API
def search_youtube_video(query: str) -> str:
    url = "https://www.googleapis.com/youtube/v3/search"
    params = {
        "part": "snippet",
        "q": query,
        "type": "video",
        "maxResults": 1,
        "key": YOUTUBE_API_KEY
    }
    try:
        response = requests.get(url, params=params)
        data = response.json()
        if "items" in data and data["items"]:
            video_id = data["items"][0]["id"].get("videoId", "")
            if video_id:
                title = data["items"][0]["snippet"]["title"]
                return f"Title: {title}\nLink: https://www.youtube.com/watch?v={video_id}"
        return "Sorry, couldn't find a video link."
    except Exception as e:
        print("YouTube API error:", e)
        return "Sorry, couldn't find a video link."

# Function to call Gemini chat API while maintaining history
def call_gemini_chat(user_email, user_message):
    API_KEY = "My API Key" # Posted after modifying the app key according to the GitHub security policy
    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={API_KEY}"
    headers = {"Content-Type": "application/json"}

    history = user_histories.get(user_email, [])
    history.append({"role": "user", "parts": [{"text": user_message}]})

    response = requests.post(url, headers=headers, json={"contents": history})
    try:
        result = response.json()
        reply = result["candidates"][0]["content"]["parts"][0]["text"]
        history.append({"role": "model", "parts": [{"text": reply}]})
        user_histories[user_email] = history[-10:]  # Keep last 10 exchanges
        return reply
    except Exception as e:
        print("Gemini error:", e)
        return "There was a problem getting a response from Gemini."

# Cloud Function endpoint for chatbot
@functions_framework.http
def chat(request):
    data = request.get_json(silent=True) or {}
    user_message = data.get("user_message", "").strip()
    user_email = data.get("user_email", "anonymous")

    # Case 1: Handle food bank queries
    if "food bank" in user_message.lower():
        for fb in foodbanks:
            if fb["district"].lower() in user_message.lower():
                return jsonify({
                    "bot_reply": (
                        f"Center: {fb['center']}\n"
                        f"Address: {fb['address']}\n"
                        f"Phone: {fb['phone']}\n"
                        f"Map: {fb['map']}"
                    )
                })

    # Case 2: Handle YouTube/video queries (search directly)
    if "youtube" in user_message.lower() or "video" in user_message.lower():
        yt_result = search_youtube_video(user_message)
        return jsonify({"bot_reply": yt_result})

    # Case 3: Default to Gemini chat for general queries
    reply = call_gemini_chat(user_email, user_message)
    return jsonify({"bot_reply": reply})
