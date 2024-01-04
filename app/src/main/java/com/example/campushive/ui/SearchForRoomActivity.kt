package com.example.campushive.ui

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.campushive.R
import com.example.campushive.databinding.ActivitySearchForRoomBinding
import com.example.campushive.util.AppConst
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchForRoomActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchForRoomBinding
    lateinit var sp:SharedPreferences
    lateinit var editor:Editor

    private val database = Firebase.database(AppConst.dbUrl)

    //reference for jec
    val myRef = database.getReference("College/jec/scribble")
    var roomUuid: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_for_room)
        sp = getSharedPreferences("pref", Context.MODE_PRIVATE)
        editor = sp.edit()

        setListenerForRoomDataChange()
        setListenerForPlayerDataChange()

        binding.startBtn.setOnClickListener {
            myRef.child(roomUuid).child("started").setValue(true)
        }
        val roomsInJec = myRef.get()
        roomsInJec.addOnCompleteListener {
            if (it.isSuccessful) {
                //if there are no rooms then create a room and set self as presenter
                if (it.result.childrenCount == 0L) {
                    createNewRoom()
                } else {
                    var roomsTraversed = 0L
                    for (childSnapshot in it.result.children) {
                        if(childSnapshot.child("count").value!=null) {
                            val count = childSnapshot.child("count").value as Long
                            if (count < 4) {
                                roomUuid = childSnapshot.key.toString()
                                addPeopleToRoom(roomUuid)
                                break
                            }
                        }
                        roomsTraversed++

                    }
                    if (roomsTraversed == it.result.childrenCount)
                        createNewRoom()
                }
            } else {
                // Handle the error
                val exception = it.exception
                // ...
            }
        }


    }

    private fun setListenerForRoomDataChange() {
            myRef.child(roomUuid).child("count").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        if(dataSnapshot.value!=null) {
                            val count = dataSnapshot.value as Int
                            binding.count.text = "count:$count"
                            if (count > 1)
                                binding.startBtn.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                    }
                })
    }

    private fun addPeopleToRoom(roomUuid: String) {
            myRef.child(roomUuid).child("players")
                .child(sp.getString("uuid", "").toString()).child("presenter")
                .setValue(false)
            myRef.child(roomUuid).child("count").runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentValue = currentData.getValue(Int::class.java) ?: 0
                    currentData.value = currentValue + 1
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    dataSnapshot: DataSnapshot?
                ) {
                    if (error != null) {
                        // Handle the error
                        // ...
                    } else {
                        setListenerForGameStartedDataChange(roomUuid)
                        binding.startBtn.visibility=View.GONE
                    }
                }
            })
    }

    private fun createNewRoom() {
        roomUuid = java.util.UUID.randomUUID().toString()
        setListenerForGameStartedDataChange(roomUuid)
        myRef.child(roomUuid!!).child("players")
            .child(sp.getString("uuid", "").toString()).child("presenter")
            .setValue(true)
        myRef.child(roomUuid!!).child("count").setValue(1)
        myRef.child(roomUuid).child("started").setValue(false)

        binding.startBtn.visibility=View.VISIBLE


    }

    private fun setListenerForPlayerDataChange() {
            myRef.child(roomUuid).child("players").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val playersInRoom = myRef.child(roomUuid).child("players").get()
                    playersInRoom.addOnCompleteListener {
                        if (it.isSuccessful) {

                            var count=0
                            for (childSnapshot in it.result.children) {
                                val uuid = childSnapshot.child("uuid").value
                                Log.d("player$count",uuid.toString())
                                count++
                            }


                        } else {
                            val exception = it.exception
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })
    }

    private fun setListenerForGameStartedDataChange(roomUuid: String) {
        myRef.child(roomUuid).child("started").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("startedOutsideUUId",roomUuid)
                Log.d("startedOutside",dataSnapshot.value.toString())
                if(dataSnapshot.value!=null&&!roomUuid.isEmpty()) {
                    val started=dataSnapshot.value.toString().toBoolean()
                    Log.d("started",started.toString())
                    if(started)
                    {
                        var intent = Intent(this@SearchForRoomActivity, CanvasActivity::class.java)
                        intent.putExtra("roomId", roomUuid)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }

}