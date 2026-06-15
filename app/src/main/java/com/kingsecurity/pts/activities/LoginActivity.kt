package com.kingsecurity.pts.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kingsecurity.pts.databinding.ActivityLoginBinding
import com.kingsecurity.pts.utils.SharedPrefHelper
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPref: SharedPrefHelper
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
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
                        checkUserApprovalStatus(user.uid, email)
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

    private fun checkUserApprovalStatus(userId: String, email: String) {
        scope.launch {
            try {
                val document = firestore.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener { doc ->
                        val isApproved = doc.getBoolean("isApproved") ?: false
                        val isAdmin = doc.getBoolean("isAdmin") ?: false
                        
                        if (isApproved) {
                            sharedPref.setUserEmail(email)
                            sharedPref.setUserId(userId)
                            sharedPref.setIsAdmin(isAdmin)
                            sharedPref.setBiometricEnabled(false)
                            
                            Toast.makeText(
                                this@LoginActivity,
                                "Hoş geldiniz!",
                                Toast.LENGTH_SHORT
                            ).show()
                            
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Hesabınız henüz onaylanmamıştır. Lütfen admin onayını bekleyin.",
                                Toast.LENGTH_LONG
                            ).show()
                            auth.signOut()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this@LoginActivity,
                            "Kontrol hatası: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Hata: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}