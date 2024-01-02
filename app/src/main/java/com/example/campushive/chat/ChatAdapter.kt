package com.example.campushive.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campushive.MessageData
import com.example.campushive.databinding.MessageItemBinding

class ChatAdapter(val context: Context, val chats: ArrayList<MessageData>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding: MessageItemBinding = MessageItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChatViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        chats[position].let {
            holder.bindView(it, context, position)
        }
    }

    inner class ChatViewHolder(private val binding: MessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(chat: MessageData?, context: Context, position: Int) {
            binding.username.text=chat?.username
            binding.message.text=chat?.message
            binding.time.text=chat?.time
        }
    }

    fun addMoreData(message:MessageData)
    {
        chats.add(message)
    }
}