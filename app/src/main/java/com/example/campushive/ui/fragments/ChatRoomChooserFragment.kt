package com.example.campushive.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.campushive.R
import com.example.campushive.chat.ChatActivity
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

        binding.apply {
            Glide.with(requireContext()).load(R.drawable.coding_img).into(coding.img)
            coding.name.text="Coding"
            Glide.with(requireContext()).load(R.drawable.android_img).into(android.img)
            android.name.text="Android"
            coding.root.setOnClickListener {
                startActivity(Intent(requireContext(), ChatActivity::class.java))
            }
            android.root.setOnClickListener {
                startActivity(Intent(requireContext(), ChatActivity::class.java))
            }
        }




        return binding.root
    }

}