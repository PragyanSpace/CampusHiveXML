package com.example.campushive.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.campushive.R
import com.example.campushive.ui.ChatActivity
import com.example.campushive.databinding.FragmentChatRoomChooserBinding

class ChatRoomChooserFragment : Fragment() {
    lateinit var binding:FragmentChatRoomChooserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_chat_room_chooser,null,false)
        binding.initListeners()

        binding.apply {
            Glide.with(requireContext()).load(R.drawable.coding_img).into(coding.img)
            coding.name.text="Coding"
            Glide.with(requireContext()).load(R.drawable.android_img).into(android.img)
            android.name.text="Android"
        }




        return binding.root
    }

    private fun FragmentChatRoomChooserBinding.initListeners()
    {
        coding.root.setOnClickListener {
            val intent=Intent(requireContext(), ChatActivity::class.java).apply {this.putExtra("roomName","coding")}
            startActivity(intent)
        }
        android.root.setOnClickListener {
            val intent=Intent(requireContext(), ChatActivity::class.java).apply {this.putExtra("roomName","android")}
            startActivity(intent)
        }
    }

}