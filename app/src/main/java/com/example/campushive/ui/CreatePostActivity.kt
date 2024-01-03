package com.example.campushive.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.campushive.R
import com.example.campushive.data.PostData
import com.example.campushive.databinding.ActivityCreatePostBinding
import com.example.campushive.viewmodel.PostViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreatePostActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    lateinit var binding:ActivityCreatePostBinding
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var imageUri:Uri
    private lateinit var viewModel:PostViewModel
    private var url="posts/"
    private lateinit var storageRef:StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_create_post)
        viewModel= ViewModelProvider(this)[PostViewModel::class.java]
        db= Firebase.firestore
        storageRef = FirebaseStorage.getInstance().reference
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onGalleryImagePicked(it) }
        }
        initObserver()
        initListener()
    }

    private fun initListener() {
        binding.close.setOnClickListener{
            finish()
        }
        binding.content.addTextChangedListener(contentTextWatcher)
        binding.addImage.setOnClickListener {
            pickImageFromGallery()
        }
        binding.post.setOnClickListener {
            binding.loader.isVisible=true
            storageRef=storageRef.child(url+imageUri.lastPathSegment)
            viewModel.uploadImage(storageRef.child(url+imageUri.lastPathSegment),imageUri)
        }
    }

    private val contentTextWatcher=object:TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.content.isEnabled = !s.isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {
        }


    }

    private fun pickImageFromGallery() {
        // Launch the gallery picker
        galleryLauncher.launch("image/*")
    }

    private fun onGalleryImagePicked(uri: Uri) {
        imageUri=uri
        Glide.with(this).load(uri).into(binding.uploadImage)
    }

    private fun initObserver(){
        viewModel.isImageUploaded.observe(this){
            storageRef.downloadUrl.addOnSuccessListener {
                url=it.toString()
                viewModel.createPost(db, PostData("",binding.content.text.toString(),url))
            }
        }

        viewModel.isPostCreated.observe(this){
            binding.loader.isVisible=false
            finish()
        }
    }
}