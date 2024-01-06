package com.example.campushive.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.campushive.data.PostData
import com.example.campushive.util.FirebaseUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

class PostRepository {
    var posts= MutableLiveData<List<PostData>>()
    var isImageUploaded=MutableLiveData<Boolean>()
    var isPostCreated=MutableLiveData<Boolean>()
    fun getAllPosts(db:FirebaseFirestore)
    {
        val listOfPosts= mutableListOf<PostData>()
        db.collection("jec/rkYswF3u76XLLEww2NpT/posts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    listOfPosts.add(PostData(user = document.getString("user")?:"", content = document.getString("textContent")?:"",url=document.getString("url")?:"" ))
                }
                posts.postValue(listOfPosts)
            }
            .addOnFailureListener { exception ->
                Log.e("asdfgh",exception.localizedMessage?:"")
            }
    }

    fun createPost(db:FirebaseFirestore,postData:PostData) {
        val post= hashMapOf("textContent" to postData.content,"url" to postData.url, "user" to FirebaseUtil.user)
        db.collection("jec/rkYswF3u76XLLEww2NpT/posts")
            .add(post)
            .addOnSuccessListener {
                isPostCreated.postValue(true)
            }
            .addOnFailureListener { exception ->
                Log.e("asdfgh",exception.localizedMessage?:"")
            }
    }

    fun uploadImage(uri: Uri,imgRef:StorageReference){
        imgRef.putFile(uri).addOnSuccessListener {
            Log.d("asasdasd","success")
            isImageUploaded.postValue(true)
        }
    }
}