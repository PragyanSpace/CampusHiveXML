package com.example.campushive.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.campushive.data.ChatMessage
import com.example.campushive.databinding.MessageItemBinding
import com.example.campushive.databinding.SelfMessageItemBinding
import com.example.campushive.util.FirebaseUtil

class ChatAdapter(private val context: Context, private var messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var binding:ViewDataBinding

    private val VIEW_TYPE_USER_MESSAGE = 1
    private val VIEW_TYPE_OTHER_MESSAGE = 2

    inner class UserMessageViewHolder(val binding: SelfMessageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(messages:ChatMessage)
        {
            binding.apply{
                message.text=messages.message
                time.text=messages.time
            }
        }
    }

    inner class OtherMessageViewHolder(val binding: MessageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(messages:ChatMessage)
        {
            binding.apply{
                message.text=messages.message
                time.text=messages.time
                username.text=messages.username
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER_MESSAGE -> {
                binding = SelfMessageItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                UserMessageViewHolder(binding as SelfMessageItemBinding)
            }
            VIEW_TYPE_OTHER_MESSAGE -> {
                binding = MessageItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OtherMessageViewHolder(binding as MessageItemBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE -> {
                val userHolder=holder as UserMessageViewHolder
                userHolder.bind(message)
            }
            VIEW_TYPE_OTHER_MESSAGE -> {
                val otherHolder = holder as OtherMessageViewHolder
                otherHolder.bind(message)
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.username == FirebaseUtil.user) {
            VIEW_TYPE_USER_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    fun addNewMessage(message: ChatMessage)
    {
        messages.add(message)
        notifyItemInserted(itemCount-1)
    }

    fun setMessages(messages:List<ChatMessage>)
    {
        this.messages.addAll(messages)
        notifyDataSetChanged()
    }
}
