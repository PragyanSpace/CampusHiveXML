package com.example.campushive.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.campushive.repository.PostRepository
import com.example.campushive.data.PostData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

class PostViewModel: ViewModel() {

    var repository: PostRepository = PostRepository()
    var posts:LiveData<List<PostData>> = repository.posts
    var isImageUploaded:LiveData<Boolean> = repository.isImageUploaded
    var isPostCreated:LiveData<Boolean> = repository.isPostCreated


    fun getAllPosts(db:FirebaseFirestore)
    {
        repository.getAllPosts(db)
    }

    fun createPost(db: FirebaseFirestore, post:PostData)
    {
        repository.createPost(db,post)
    }

    fun uploadImage(imgRef: StorageReference, uri: Uri)
    {
        repository.uploadImage(uri,imgRef)
    }
}