package com.example.meshsosapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meshsosapp.models.ChatMessage

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_SOS = 2
    }

    // ViewHolder for normal messages
    class NormalMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderText: TextView = itemView.findViewById(R.id.sender_text)
        val messageText: TextView = itemView.findViewById(R.id.message_text)
        val timestampText: TextView = itemView.findViewById(R.id.timestamp_text)
    }

    // ViewHolder for SOS messages
    class SosMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sosMessageText: TextView = itemView.findViewById(R.id.sos_message_text)
        val sosTimestampText: TextView = itemView.findViewById(R.id.sos_timestamp_text)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSos) {
            VIEW_TYPE_SOS
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SOS) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sos, parent, false)
            SosMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message, parent, false)
            NormalMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder.itemViewType == VIEW_TYPE_SOS) {
            val sosHolder = holder as SosMessageViewHolder
            sosHolder.sosMessageText.text = message.text
            sosHolder.sosTimestampText.text = "[${message.timestamp}]"
        } else {
            val normalHolder = holder as NormalMessageViewHolder
            // Apply the new formatting here
            normalHolder.senderText.text = "<@${message.sender}>"
            normalHolder.messageText.text = message.text
            normalHolder.timestampText.text = "[${message.timestamp}]"
        }
    }

    override fun getItemCount() = messages.size
}

