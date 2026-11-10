package com.example.coffee.activites

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.coffee.Helper.TinyDB
import com.example.coffee.databinding.ActivitySplash2Binding

class SplashActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivitySplash2Binding
    private val handler = Handler(Looper.getMainLooper())
    private var isNavigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplash2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val tinyDB = TinyDB(this)
        val isLoggedIn = tinyDB.getBoolean("is_logged_in")

        // If user is already authenticated, smoothly transition to MainActivity
        if (isLoggedIn) {
            handler.postDelayed({
                navigateTo(MainActivity::class.java)
            }, 1200)
        }

        // Tapping 'Get Started' takes new users to Login / Onboarding
        binding.startBtn.setOnClickListener {
            if (isLoggedIn) {
                navigateTo(MainActivity::class.java)
            } else {
                navigateTo(LoginActivity::class.java)
            }
        }

        // Tapping 'Already have an account? Sign In' takes directly to Login
        binding.signInTextBtn.setOnClickListener {
            navigateTo(LoginActivity::class.java)
        }
    }

    private fun navigateTo(destination: Class<*>) {
        if (isNavigated) return
        isNavigated = true
        handler.removeCallbacksAndMessages(null)

        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
