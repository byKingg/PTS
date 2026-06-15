package com.kingsecurity.pts.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kingsecurity.pts.databinding.ActivityLoginBinding
import com.kingsecurity.pts.utils.SharedPrefHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPref = SharedPrefHelper(this)

        setupListeners()
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (validateInputs(email, password)) {
                loginUser(email, password)
            }
        }

        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.emailEditText.error = "Email alanı boş olamaz"
                false
            }
            password.isEmpty() -> {
                binding.passwordEditText.error = "Şifre alanı boş olamaz"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailEditText.error = "Geçerli bir email girin"
                false
            }
            password.length < 6 -> {
                binding.passwordEditText.error = "Şifre en az 6 karakter olmalı"
                false
            }
            else -> true
        }
    }

    private fun loginUser(email: String, password: String) {
        binding.loginButton.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = android.view.View.GONE
                binding.loginButton.isEnabled = true

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Check if user is approved by admin
                        checkUserApprovalStatus(user.uid)
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Giriş başarısız: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun checkUserApprovalStatus(userId: String) {
        // TODO: Check in Firestore if user is approved
        // For now, assume approved and navigate
        sharedPref.setUserEmail(auth.currentUser?.email ?: "")
        sharedPref.setBiometricEnabled(false)
        
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}