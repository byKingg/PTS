package com.kingsecurity.pts.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kingsecurity.pts.R
import com.kingsecurity.pts.databinding.ActivitySplashBinding
import com.kingsecurity.pts.utils.SharedPrefHelper

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth
    private val SPLASH_TIME_OUT: Long = 3000 // 3 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        auth = FirebaseAuth.getInstance()
        
        // Start logo animation
        binding.lottieAnimation.playAnimation()
        
        // Navigate after splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, SPLASH_TIME_OUT)
    }
    
    private fun navigateToNextScreen() {
        val currentUser = auth.currentUser
        val sharedPref = SharedPrefHelper(this)
        val isBiometricEnabled = sharedPref.getBiometricEnabled()
        
        val intent = when {
            currentUser == null -> Intent(this, LoginActivity::class.java)
            isBiometricEnabled -> Intent(this, BiometricActivity::class.java)
            else -> Intent(this, MainActivity::class.java)
        }
        
        startActivity(intent)
        finish()
    }
}