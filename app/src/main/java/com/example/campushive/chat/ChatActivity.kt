package com.example.campushive.chat

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campushive.R
import com.example.campushive.databinding.ActivityChatBinding
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException


class  ChatActivity : AppCompatActivity() {
    private var mSocket: Socket? = null
    lateinit var binding:ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_chat)
        try {
            mSocket = IO.socket("http://chat.socket.io")
            mSocket?.connect()
        } catch (e: URISyntaxException) {
        }
        initListeners()

    }

    private fun initListeners() {
        binding.infoBtn.setOnClickListener {
            var dialog = Dialog(this);

            // Set the custom dialog layout
            dialog.setContentView(R.layout.coding_chat_info_dialog)
            dialog.setCancelable(true)
            dialog.show()
        }
        binding.chatRv.let{
            it.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            it.adapter= ChatAdapter(this, arrayListOf())
        }
    }


}