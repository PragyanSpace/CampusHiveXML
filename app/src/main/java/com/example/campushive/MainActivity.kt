package com.example.campushive

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.campushive.chat.ChatActivity
import com.example.campushive.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    val database = Firebase.database(AppConst.dbUrl)
    val myRef = database.getReference("College")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val sp = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = sp.edit()

        if(!sp.contains("uuid")) {
            val uuid: String = java.util.UUID.randomUUID().toString()
            editor.putString("uuid", uuid)
            editor.apply()
            myRef.child("jec").child("users").child(uuid).setValue(uuid)
        }


        binding.searchRoomBtn.setOnClickListener {


            startActivity(Intent(this@MainActivity, SearchForRoomActivity::class.java))
//            startActivity(Intent(this@MainActivity, VoiceCallActivity::class.java))
        }

        binding.editData.setOnClickListener {
            startActivity(Intent(this@MainActivity, ChatActivity::class.java))
        }

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                binding.remoteData = dataSnapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

    }
}