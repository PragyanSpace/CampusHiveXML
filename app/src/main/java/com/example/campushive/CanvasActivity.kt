package com.example.campushive

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.campushive.ConversionUtil
import com.example.campushive.agora.media.RtcTokenBuilder2
import com.example.campushive.databinding.ActivityCanvasBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.agora.rtc2.*

class CanvasActivity : AppCompatActivity(), DrawingView.DrawingListener {

    private lateinit var myRef: DatabaseReference
    private lateinit var sp: SharedPreferences
    private lateinit var binding: ActivityCanvasBinding


    //Voice chat variables
    private val appId = "0b3830faf8474d0e9012c7569063b8a0"
    private var appCertificate = "7823aee8257b467cb3866f23336dd83e"
    private var channelName = "jec"
    private var token : String?= null
    private val uid = 0
    private var isJoined = false
    private var agoraEngine: RtcEngine? = null
    private val expirationTimeInSeconds=600
    val PERMISSION_REQ_ID = 22
    val REQUESTED_PERMISSIONS =arrayOf(Manifest.permission.RECORD_AUDIO)
    private var isMuted = true
    private var isSoundOff = false
    private var isPresenter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_canvas)
        val database = Firebase.database(AppConst.dbUrl)
        val roomId = intent.getStringExtra("roomId")
        channelName=roomId.toString()
        myRef = database.getReference("College/jec").child(roomId.toString())
        sp = getSharedPreferences("pref", Context.MODE_PRIVATE)


        setupVoiceSDKEngine()

        binding.clear.setOnClickListener {
            binding.drawingView.clearCanvas()
        }

        binding.pen.setOnClickListener {
            binding.pen.background= resources.getDrawable(R.color.grey)
            binding.eraser.background=resources.getDrawable(R.color.teal_200)
            binding.drawingView.setToPen()
        }

//        binding.eraser.setOnClickListener {
//            binding.pen.background= resources.getDrawable(R.color.teal_200)
//            binding.eraser.background=resources.getDrawable(R.color.grey)
//            binding.drawingView.setToEraser()
//        }

        binding.submitBtn.setOnClickListener {
            val guessWord = binding.guessWord.text.toString().lowercase()
            myRef.child("gWord").setValue(guessWord)
            myRef.child("word").get().addOnSuccessListener { snapshot ->
                val correctWord = snapshot.value.toString()
                if (guessWord == correctWord) {
                    Toast.makeText(this, "Correct answer", Toast.LENGTH_LONG).show()
                    myRef.child("players").child(sp.getString("uuid", "")!!)
                        .child("presenter").setValue(true)
                    binding.drawingView.clearCanvas()
                    setOtherPlayerAsNonPresenter()
                }
            }.addOnFailureListener { exception ->
                // Handle the error
                Log.e(ContentValues.TAG, "Failed to read value.", exception)
            }
        }

        binding.drawingView.setDrawingListener(this)
        setPresenterListener()
        setGuessedWordChangedListener()

        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }

    }

    private fun setOtherPlayerAsNonPresenter() {
        myRef.child("players").get().addOnSuccessListener { snapshot ->
            for (childSnapshot in snapshot.children) {
                val childKey = childSnapshot.key.toString()
                if (childKey != sp.getString("uuid", "")) {
                    myRef.child("players").child(childKey).child("presenter").setValue(false)
                }
            }
        }.addOnFailureListener { exception ->
            // Handle the error
            Log.e(ContentValues.TAG, "Failed to read value.", exception)
        }
    }

    private fun setOtherPlayerAsPresenterOnPresenterLeave() {
        myRef.child("players").get().addOnSuccessListener { snapshot ->
            for (childSnapshot in snapshot.children) {
                val childKey = childSnapshot.key.toString()
                if (childKey != sp.getString("uuid", "")) {
                    myRef.child("players").child(childKey).child("presenter").setValue(true)
                    break
                }
            }
        }.addOnFailureListener { exception ->
            // Handle the error
            Log.e(ContentValues.TAG, "Failed to read value.", exception)
        }
    }

    private fun setPresenterListener() {
        myRef.child("players").child(sp.getString("uuid", "")!!)
            .child("presenter").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    isPresenter = dataSnapshot.value as Boolean? == true
                    if (isPresenter) {
                        binding.word.visibility = View.VISIBLE
                        binding.drawingView.enableDrawing()
                        binding.chatLayout.visibility = View.GONE
                        setTextToWordAndDatabase()
                        binding.drawingView.clearCanvas()
                    } else {
                        binding.drawingView.disableDrawing()
                        setImgStringChangedListener()
                        binding.word.visibility = View.INVISIBLE
                        binding.chatLayout.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })
    }


    private fun setImgStringChangedListener() {
        myRef.child("imgString").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val imgString = dataSnapshot.value.toString()
                Log.d("drawingReceived", imgString)
                if (imgString.isNotEmpty()&&!isPresenter) {
                    val imgAsBit = ConversionUtil().stringToBitmap(imgString)
                    binding.drawingView.setDrawingBitmap(imgAsBit)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun setGuessedWordChangedListener() {
        myRef.child("gWord").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.value?.let {
                    binding.guessWord.setText("")
                    Toast.makeText(this@CanvasActivity, it.toString(), Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }

    override fun onDrawingFinished(drawingBitmap: Bitmap?) {
        Log.d("finished", "Drawing Finished")
        val img = ConversionUtil().bitmapToString(drawingBitmap!!)
        Log.d("drawingSent", img.toString())
        myRef.child("imgString").setValue(img)
    }

    private fun setTextToWordAndDatabase() {
        var word = GameUtil().getWord()
        binding.word.text = word
        myRef.child("word").setValue(word)

    }

    override fun onBackPressed(){
        super.onBackPressed()
        userLeft()

    }

    override fun onDestroy() {
        super.onDestroy()
        userLeft()
    }

    private fun userLeft()
    {
        myRef.child("players").child(sp.getString("uuid","")!!).child("presenter").get().addOnSuccessListener { snapshot ->
            val isPresenter = snapshot.value as Boolean
            if (isPresenter) {
                setOtherPlayerAsPresenterOnPresenterLeave()
            }
        }.addOnFailureListener { exception ->
            // Handle the error
            Log.e(ContentValues.TAG, "Failed to read value.", exception)
        }
        myRef.child("players").child(sp.getString("uuid","")!!).removeValue()
        myRef.child("count").runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentValue = currentData.getValue(Int::class.java) ?: 0
                currentData.value = currentValue - 1
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {

                if (currentData?.value==0L) {
                    myRef.removeValue()
                }
            }
        })
        agoraEngine!!.leaveChannel()

        // Destroy the engine in a sub-thread to avoid congestion
        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    fun showMessage(message: String?) {
        runOnUiThread {
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupVoiceSDKEngine() {
        try {
            val tokenBuilder = RtcTokenBuilder2()
            val timestamp = (System.currentTimeMillis() / 1000 + expirationTimeInSeconds).toInt()

            val result = tokenBuilder.buildTokenWithUid(
                appId, appCertificate,
                channelName, uid, RtcTokenBuilder2.Role.ROLE_PUBLISHER, timestamp, timestamp
            )


            binding.mic.setOnClickListener {
                toggleMute()
            }

            binding.sound.setOnClickListener {
                toggleSoundOff()
            }


            token = result
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            joinChannel()
        } catch (e: Exception) {
            throw RuntimeException("Check the error.")
        }
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread { showMessage("Remote user joined: $uid")}
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            // Successfully joined a channel
            isJoined = true
            showMessage("Joined Channel $channel")
            runOnUiThread { showMessage("Waiting for a remote user to join") }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            // Listen for remote users leaving the channel
            showMessage("Remote user offline $uid $reason")
            if (isJoined) runOnUiThread { showMessage("Waiting for a remote user to join")}
        }

        override fun onLeaveChannel(stats: RtcStats) {
            // Listen for the local user leaving the channel
            runOnUiThread { showMessage("Press the button to join a channel") }
            isJoined = false
        }
    }


    private fun toggleMute() {
        isMuted = !isMuted
        if (isMuted)
        {
            binding.micImg.setImageResource(R.drawable.ic_mic_off_canvas)
        }
        else
        {
            binding.micImg.setImageResource(R.drawable.ic_mic_on_canvas)
        }
        agoraEngine?.muteLocalAudioStream(isMuted)
        showMessage("Microphone ${if (isMuted) "muted" else "unmuted"}")
    }

    private fun toggleSoundOff() {
        isSoundOff = !isSoundOff
        if (isSoundOff)
        {
            binding.soundImg.setImageResource(R.drawable.ic_sound_off_canvas)
        }
        else
        {
            binding.soundImg.setImageResource(R.drawable.ic_sound_on_canvas)
        }
        agoraEngine?.adjustPlaybackSignalVolume(if (isSoundOff) 0 else 100)
        showMessage("Sound ${if (isSoundOff) "off" else "on"}")
    }


    private fun checkSelfPermission():Boolean
    {
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) !=  PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        return true;
    }

    private fun joinChannel() {
        val options = ChannelMediaOptions()
        options.autoSubscribeAudio = true
        // Set both clients as the BROADCASTER.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        // Set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING

        // Enable audio by default
        agoraEngine!!.enableAudio()

        // Set audio mute state
        agoraEngine!!.muteLocalAudioStream(isMuted)

        // Set audio sound off state
        agoraEngine!!.adjustPlaybackSignalVolume(if (isSoundOff) 0 else 100)
        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        agoraEngine!!.joinChannel(token, channelName, uid, options)
    }
}

