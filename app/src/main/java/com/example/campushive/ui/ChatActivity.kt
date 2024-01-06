package com.example.campushive.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campushive.R
import com.example.campushive.data.ChatMessage
import com.example.campushive.databinding.ActivityChatBinding
import com.example.campushive.ui.adapter.ChatAdapter
import com.example.campushive.util.AppConst
import com.example.campushive.util.FirebaseUtil
import com.example.campushive.viewmodel.ChatViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class  ChatActivity : AppCompatActivity() {
    lateinit var binding:ActivityChatBinding
    private lateinit var adapter:ChatAdapter
    private lateinit var viewModel:ChatViewModel
    private lateinit var database:FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_chat)
        database = Firebase.database(AppConst.dbUrl)
        viewModel= ViewModelProvider(this)[ChatViewModel::class.java]
        adapter=ChatAdapter(this@ChatActivity, arrayListOf())
        binding.chatRv.let{
            it.layoutManager=LinearLayoutManager(this@ChatActivity,LinearLayoutManager.VERTICAL,false)
            it.adapter= adapter
        }
        observeMessages()
        viewModel.getAllMessages(database,intent.getStringExtra("room")?:"")
        binding.initListeners()

    }

    private fun observeMessages() {
        viewModel.messages.observe(this) {
            adapter.setMessages(it)
        }
    }

    private fun ActivityChatBinding.initListeners() {
        infoBtn.setOnClickListener {
            val dialog = Dialog(this@ChatActivity)

            // Set the custom dialog layout
            dialog.setContentView(R.layout.coding_chat_info_dialog)
            dialog.setCancelable(true)
            dialog.show()
        }
        sendBtn.setOnClickListener {
            val time=getCurrentTimeWithLocalTime()
            val messageData=ChatMessage(message = this.message.getText().toString(), username = FirebaseUtil.user, time = time)
            message.setText("")
            adapter.addNewMessage(message = messageData)
            viewModel.sendMessage(database,intent.getStringExtra("roomName").toString(),messageData)

        }
    }

    fun getCurrentTimeWithLocalTime(): String {
        val localTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return localTime.format(formatter)
    }


}