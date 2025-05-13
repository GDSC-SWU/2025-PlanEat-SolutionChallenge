package com.example.planeat.data.model;

data class ChatMessage(
        val senderId: String,
        val text: String,
        val youtubeThumbnailUrl: String? = null,
        val youtubeLink: String? = null
)