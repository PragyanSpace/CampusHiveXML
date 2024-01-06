package com.example.campushive.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.campushive.data.ChatMessage
import com.example.campushive.repository.ChatRepository
import com.google.firebase.database.FirebaseDatabase

class ChatViewModel:ViewModel() {
    var repository: ChatRepository = ChatRepository()
    var messages: LiveData<List<ChatMessage>> = repository.messages


    fun getAllMessages(db: FirebaseDatabase,room:String) {
        repository.getMessages(db,room)
    }

    fun sendMessage(db: FirebaseDatabase,room:String, message: ChatMessage) {

        repository.sendMessage(db, room=room, message = message)
    }

}