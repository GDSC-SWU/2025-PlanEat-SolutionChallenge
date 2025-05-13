package com.example.planeat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planeat.R
import com.example.planeat.adapter.ChatAdapter
import com.example.planeat.network.api.PlaneatApi
import com.example.planeat.data.model.ChatMessage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize views
        recyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.message_input)
        sendButton = findViewById(R.id.send_button)

        // Set up RecyclerView
        chatAdapter = ChatAdapter("me")
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        val letter = intent.getStringExtra("letter")

        if (!letter.isNullOrBlank()) {
            chatAdapter.addMessage(ChatMessage("recommend", "Here is the reason for the recommendation:"))

            val paragraphs = letter.split("\n\n", "\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            // Show each paragraph with delay
            MainScope().launch {
                for (paragraph in paragraphs) {
                    delay(2000)
                    chatAdapter.addMessage(ChatMessage("recommend", paragraph))
                    recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                }

                delay(2000)
                chatAdapter.addMessage(ChatMessage("recommend", getString(R.string.ask_gemini)))
                recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            }
        } else {
            chatAdapter.addMessage(ChatMessage("recommend", getString(R.string.ask_gemini)))
        }

        sendButton.setOnClickListener {
            val userMessage = messageInput.text.toString()
            val userEmail = intent.getStringExtra("user_email") ?: "Unknown"
            if (userMessage.isNotBlank()) {
                chatAdapter.addMessage(ChatMessage("me", userMessage))
                messageInput.text.clear()
                sendMessageToPlaneat(userEmail, userMessage)
            }
        }

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        // Toggle send button icon based on input
        messageInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                sendButton.setImageResource(
                    if (s.isNullOrEmpty()) R.drawable.ic_send_disabled else R.drawable.ic_send
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun sendMessageToPlaneat(userEmail: String, userMessage: String) {
        MainScope().launch {
            try {
                val aiResponse = PlaneatApi.generateResponse(userEmail, userMessage)

                // Check if the response contains a YouTube link
                val youtubeRegex = Regex("""https?://(?:www\.)?(?:youtube\.com/watch\?v=|youtu\.be/)([a-zA-Z0-9_-]{11})""")
                val match = youtubeRegex.find(aiResponse)
                val videoId = match?.groupValues?.get(1)
                val youtubeLink = match?.value // Full YouTube link
                val thumbnailUrl = videoId?.let { "https://img.youtube.com/vi/$it/0.jpg" }

                // Add message with optional thumbnail and YouTube link
                chatAdapter.addMessage(ChatMessage("bot", aiResponse, thumbnailUrl, youtubeLink))
                recyclerView.scrollToPosition(chatAdapter.itemCount - 1)

            } catch (e: Exception) {
                e.printStackTrace()
                chatAdapter.addMessage(ChatMessage("bot", "Sorry, something went wrong."))
                recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            }
        }
    }
}