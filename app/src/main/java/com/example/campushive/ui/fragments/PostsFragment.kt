package com.example.campushive.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campushive.R
import com.example.campushive.databinding.FragmentPostsBinding
import com.example.campushive.ui.CreatePostActivity
import com.example.campushive.ui.adapter.PostsAdapter
import com.example.campushive.util.FirebaseUtil
import com.example.campushive.viewmodel.PostViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostsFragment : Fragment() {

    lateinit var binding:FragmentPostsBinding
    lateinit var db:FirebaseFirestore
    lateinit var viewModel:PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireActivity())
        db=Firebase.firestore
        viewModel= ViewModelProvider(requireActivity())[PostViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_posts,null,false)
        binding.loader.isVisible=true
        observePostData()
        initListeners()
        callGetAllPostsApi()

        return binding.root
    }

    private fun initListeners() {
        binding.addPost.setOnClickListener{
            val intent= Intent(requireContext(),CreatePostActivity::class.java)
            startActivity(intent)
        }

        binding.pragyan.setOnClickListener {
            FirebaseUtil.user="Pragyan"
            binding.userChooser.isVisible=false
        }
        binding.sourav.setOnClickListener {
            FirebaseUtil.user="Sourav"
            binding.userChooser.isVisible=false
        }
        binding.vaskarjya.setOnClickListener {
            FirebaseUtil.user="Vaskarjya"
            binding.userChooser.isVisible=false
        }
    }

    private fun observePostData()
    {
        viewModel.posts.observe(viewLifecycleOwner){
            binding.loader.isVisible=false
            binding.postsRV.apply {
                layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                adapter=PostsAdapter(it,requireContext())
            }
        }
    }

    private fun callGetAllPostsApi()
    {
        binding.loader.isVisible=true
        viewModel.getAllPosts(db)
    }

    override fun onResume() {
        super.onResume()
        callGetAllPostsApi()
    }
}