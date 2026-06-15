package com.kingsecurity.pts.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.kingsecurity.pts.databinding.ActivityBiometricBinding
import com.kingsecurity.pts.utils.SharedPrefHelper

class BiometricActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBiometricBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPrefHelper
    private lateinit var biometricPrompt: BiometricPrompt
    private var isAuthenticationSuccessful = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPref = SharedPrefHelper(this)

        setupBiometric()
        setupListeners()

        // Biometrik kimlik doğrulamasını otomatik başlat
        performBiometricAuthentication()
    }

    private fun setupBiometric() {
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        this@BiometricActivity,
                        "Kimlik doğrulama hatası: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    isAuthenticationSuccessful = true
                    proceedToMainActivity()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        this@BiometricActivity,
                        "Parmak izi eşleşmedi, lütfen tekrar deneyin",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setupListeners() {
        binding.retryButton.setOnClickListener {
            performBiometricAuthentication()
        }

        binding.usePasswordButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun performBiometricAuthentication() {
        val canAuthenticate = BiometricManager.from(this).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )

        when (canAuthenticate) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Parmak İzi ile Giriş")
                    .setSubtitle("Cihazınızı açmak için parmak izinizi kullanın")
                    .setNegativeButtonText("Şifre Kullan")
                    .build()

                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "Biometrik donanım bulunamadı", Toast.LENGTH_SHORT).show()
                fallbackToPassword()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "Biometrik donanım şu anda kullanılamıyor", Toast.LENGTH_SHORT)
                    .show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(
                    this,
                    "Hiçbir parmak izi kayıtlı değil. Şifre ile giriş yapınız.",
                    Toast.LENGTH_SHORT
                ).show()
                fallbackToPassword()
            }
        }
    }

    private fun proceedToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun fallbackToPassword() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        // Biometrik ekrandan geri dönüşe izin verme
    }
}