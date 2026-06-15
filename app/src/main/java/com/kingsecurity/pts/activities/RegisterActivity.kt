package com.kingsecurity.pts.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kingsecurity.pts.databinding.ActivityRegisterBinding
import com.kingsecurity.pts.models.User
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupListeners()
    }

    private fun setupListeners() {
        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            if (validateInputs(email, password, confirmPassword)) {
                registerUser(email, password)
            }
        }

        binding.loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
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
            password != confirmPassword -> {
                binding.confirmPasswordEditText.error = "Şifreler eşleşmiyor"
                false
            }
            else -> true
        }
    }

    private fun registerUser(email: String, password: String) {
        binding.registerButton.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = android.view.View.GONE
                binding.registerButton.isEnabled = true

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        saveUserToFirestore(user.uid, email)
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Kayıt başarısız: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveUserToFirestore(userId: String, email: String) {
        val userData = mapOf(
            "userId" to userId,
            "email" to email,
            "isApproved" to false,
            "isAdmin" to false,
            "createdAt" to System.currentTimeMillis(),
            "lastLogin" to System.currentTimeMillis()
        )

        firestore.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Kayıt başarılı! Admin onayı bekleniyor.",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Kayıt hatası: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}