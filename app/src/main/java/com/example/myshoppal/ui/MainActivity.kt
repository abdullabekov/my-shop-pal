package com.example.myshoppal.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityMainBinding
import com.example.myshoppal.utils.Constants.LOGGED_IN_USERNAME
import com.example.myshoppal.utils.Constants.MY_PREFS

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE)
        val username = sharedPreferences.getString(LOGGED_IN_USERNAME, "")
        binding.tvMain.text = "Hello $username!"
    }
}