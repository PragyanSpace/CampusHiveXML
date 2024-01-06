package com.example.campushive.data

import com.example.campushive.util.AppConst
import com.example.campushive.util.FirebaseUtil
import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("username") val username:String,
    @SerializedName("message") val message:String,
    @SerializedName("time") val time:String
){
    constructor() : this("", "", "")
//    val isSentByUser:Boolean=username==FirebaseUtil.user
}
