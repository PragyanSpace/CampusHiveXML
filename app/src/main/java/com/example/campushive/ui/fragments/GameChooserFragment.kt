package com.example.campushive.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.campushive.R
import com.example.campushive.databinding.FragmentChatRoomChooserBinding
import com.example.campushive.databinding.FragmentGameChooserBinding

class GameChooserFragment : Fragment() {

    lateinit var binding: FragmentGameChooserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_game_chooser,null,false)

        binding.apply {
            Glide.with(requireContext()).load(R.drawable.scribble_img).into(scribbleGame.img)
            scribbleGame.name.text="Scribble"
        }




        return binding.root
    }

}