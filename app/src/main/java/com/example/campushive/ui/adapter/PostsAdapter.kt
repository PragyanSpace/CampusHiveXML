package com.example.campushive.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campushive.R
import com.example.campushive.data.PostData
import com.example.campushive.databinding.PostItemBinding

class PostsAdapter(private val posts:List<PostData>, val context: Context): RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: PostItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:PostData){
            binding.apply{
                username.text=item.user
                textContent.text=item.content
                Glide.with(context).load(item.url).placeholder(R.drawable.sample_image).into(imageContent)
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PostItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }

}