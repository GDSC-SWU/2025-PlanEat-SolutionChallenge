package com.example.planeat.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.planeat.R
import com.example.planeat.data.model.ChatMessage
import com.squareup.picasso.Picasso

class ChatAdapter(private val myUserId: String) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val myMessage: TextView = view.findViewById(R.id.my_message)
        val otherMessage: TextView = view.findViewById(R.id.other_message)
        val recommendMessage: TextView = view.findViewById(R.id.recommend_message)
        val youtubeThumbnail: ImageView = view.findViewById(R.id.youtube_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]

        // Hide thumbnail by default
        holder.youtubeThumbnail.visibility = View.GONE

        if (message.senderId == "recommend") {
            holder.recommendMessage.text = message.text
            holder.recommendMessage.visibility = View.VISIBLE
            holder.myMessage.visibility = View.GONE
            holder.otherMessage.visibility = View.GONE

            // Show thumbnail if available
            if (!message.youtubeThumbnailUrl.isNullOrEmpty()) {
                holder.youtubeThumbnail.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                    .load(message.youtubeThumbnailUrl)
                    .into(holder.youtubeThumbnail)
            }

        } else {
            if (message.senderId == myUserId) {
                holder.myMessage.text = message.text
                holder.myMessage.visibility = View.VISIBLE
                holder.otherMessage.visibility = View.GONE
                holder.recommendMessage.visibility = View.GONE
            } else {
                holder.otherMessage.text = message.text
                holder.otherMessage.visibility = View.VISIBLE
                holder.myMessage.visibility = View.GONE
                holder.recommendMessage.visibility = View.GONE

                // Show and handle YouTube thumbnail if available
                if (message.youtubeThumbnailUrl != null) {
                    holder.youtubeThumbnail.visibility = View.VISIBLE
                    Picasso.get().load(message.youtubeThumbnailUrl).into(holder.youtubeThumbnail)

                    // Open YouTube video on click using the youtubeLink passed in the message
                    holder.youtubeThumbnail.setOnClickListener {
                        val context = it.context

                        // Get the YouTube link from the message
                        val youtubeLink = message.youtubeLink

                        // Log the YouTube link for debugging
                        Log.d("YouTubeThumbnail", "YouTube Link: $youtubeLink")

                        // Ensure the youtubeLink is valid before opening
                        if (!youtubeLink.isNullOrEmpty()) {
                            // Log the YouTube link
                            Log.d("YouTubeThumbnail", "Opening YouTube URL: $youtubeLink")

                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
                            context.startActivity(intent)
                        } else {
                            // Log error if youtubeLink is null or empty
                            Log.e("YouTubeThumbnail", "Failed to open YouTube link. Link is null or empty.")
                        }
                    }
                }
            }
        }
    }

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}