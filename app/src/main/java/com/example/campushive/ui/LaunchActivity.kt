package com.example.campushive.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.campushive.R
import com.example.campushive.databinding.ActivityLaunchBinding
import com.example.campushive.ui.fragments.ChatRoomChooserFragment
import com.example.campushive.ui.fragments.GameChooserFragment
import com.example.campushive.ui.fragments.PostsFragment
import com.google.android.material.navigation.NavigationBarView

class LaunchActivity : AppCompatActivity() {
    private var backPressedTime: Long = 0
    lateinit var binding:ActivityLaunchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_launch)

        val bottomNav = binding.bottomNavigationView
        bottomNav.setOnItemSelectedListener(navListener)

        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, PostsFragment()).commit()
    }

    private val navListener = NavigationBarView.OnItemSelectedListener {item->
        lateinit var selectedFragment: Fragment
        when (item.itemId) {
            R.id.posts -> {
                selectedFragment = PostsFragment()
            }
            R.id.chatroom -> {
                selectedFragment = ChatRoomChooserFragment()
            }
            R.id.games -> {
                selectedFragment = GameChooserFragment()
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit()
        true
    }

    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()

            finish()

        } else {
            Toast.makeText(this, "Press back again to exit CampusHive.", Toast.LENGTH_LONG).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}