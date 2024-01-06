package com.example.campushive.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.campushive.data.ChatMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class ChatRepository {
    var messages= MutableLiveData<List<ChatMessage>>()

    fun getMessages(db: FirebaseDatabase,room:String)
    {
        val listOfMessages= mutableListOf<ChatMessage>()
        val messageRef = db.getReference("College/jec/chat/$room")
        messageRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    for (j in i.children){
                        Log.d("asdasd",j.value.toString())
                        val message=j.getValue(ChatMessage::class.java)
                        if (message != null) {
                            listOfMessages.add(message)
                        }
                    }

                }
                messages.postValue(listOfMessages)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun sendMessage(db: FirebaseDatabase,room:String,message: ChatMessage)
    {
        val messageRef = db.getReference("College/jec/chat/$room")
        val messageId=messageRef.push().key
        if(messageId!=null)
            messageRef.child(messageId).setValue(message)
//        messageRef.child("username").setValue(message.username)
//        messageRef.child("message").setValue(message.message)
//        messageRef.child("time").setValue(message.time)
    }
}