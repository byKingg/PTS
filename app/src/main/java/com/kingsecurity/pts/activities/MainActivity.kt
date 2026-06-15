package com.kingsecurity.pts.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.kingsecurity.pts.databinding.ActivityMainBinding
import com.kingsecurity.pts.utils.SharedPrefHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPrefHelper
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPref = SharedPrefHelper(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setupListeners()
        checkCameraPermission()
        setupUI()
    }

    private fun setupUI() {
        binding.userEmailTextView.text = auth.currentUser?.email ?: "Bilinmeyen"
    }

    private fun setupListeners() {
        binding.infoButton.setOnClickListener {
            showAboutDialog()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }

        binding.adminPanelButton.setOnClickListener {
            val isAdmin = sharedPref.getIsAdmin()
            if (isAdmin) {
                startActivity(Intent(this, AdminPanelActivity::class.java))
            } else {
                Toast.makeText(this, "Admin paneline erişim yetkiniz yok!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.scanButton.setOnClickListener {
            startScanning()
        }
    }

    private fun checkCameraPermission() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(this, "Kamera başlatma hatası", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun startScanning() {
        binding.scanButton.isEnabled = false
        Toast.makeText(this, "Tarama başladı...", Toast.LENGTH_SHORT).show()
        // TODO: YOLO tespit mantığı eklenecek
        binding.scanButton.isEnabled = true
    }

    private fun showAboutDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle("UYGULAMA HAKKINDA")
            .setMessage(
                "PTS - PLAKA TANIMA SİSTEMİ\n\n" +
                        "Sürüm: 1.0.0\n\n" +
                        "Bu uygulama, şüpheli olarak bildirilen araç plakaları tanımak için tasarlanmıştır.\n\n" +
                        "Güvenlik: Tüm veriler şifreli ve Firebase tarafından korunmaktadır.\n\n" +
                        "Yasal Bilgiler: Bu uygulamayı sadece yasal amaçlarla kullanınız.\n\n" +
                        "© 2026 KingSecurity. Tüm hakları saklıdır."
            )
            .setPositiveButton("Kapat") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Çıkış Yap")
            .setMessage("Uygulamadan çıkış yapmak istediğinize emin misiniz?")
            .setPositiveButton("Evet") { _, _ ->
                auth.signOut()
                sharedPref.clearAll()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Kamera izni gereklidir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}