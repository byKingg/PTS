package com.kingsecurity.pts.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kingsecurity.pts.adapters.SuspiciousPlateAdapter
import com.kingsecurity.pts.databinding.ActivityAdminPanelBinding
import com.kingsecurity.pts.models.SuspiciousPlate
import kotlinx.coroutines.*

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPanelBinding
    private lateinit var firestore: FirebaseFirestore
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val plates = mutableListOf<SuspiciousPlate>()
    private lateinit var adapter: SuspiciousPlateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        setupUI()
        setupAdapter()
        loadSuspiciousPlates()
    }

    private fun setupUI() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.addPlateButton.setOnClickListener {
            showAddPlateDialog()
        }

        // RecyclerView kurulumu
        binding.platesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupAdapter() {
        adapter = SuspiciousPlateAdapter(plates) { plateId ->
            deletePlate(plateId)
        }
        binding.platesRecyclerView.adapter = adapter
    }

    private fun loadSuspiciousPlates() {
        firestore.collection("suspicious_plates")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Yükleme hatası: ${error.message}", Toast.LENGTH_SHORT)
                        .show()
                    return@addSnapshotListener
                }

                plates.clear()
                snapshots?.documents?.forEach { doc ->
                    val plate = SuspiciousPlate(
                        plateNumber = doc.getString("plateNumber") ?: "",
                        reason = doc.getString("reason") ?: "",
                        createdAt = doc.getLong("createdAt") ?: 0L,
                        createdBy = doc.getString("createdBy") ?: "",
                        severity = doc.getString("severity") ?: "ORTA",
                        documentId = doc.id
                    )
                    plates.add(plate)
                }

                adapter.notifyDataSetChanged()
                binding.platesCountTextView.text = "Toplam: ${plates.size} plaka"
            }
    }

    private fun showAddPlateDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        val view = android.widget.EditText(this)
        view.hint = "Plaka numarası (örn: 34ABC1234)"

        builder.setTitle("Şüpheli Plaka Ekle")
            .setView(view)
            .setPositiveButton("Ekle") { dialog, _ ->
                val plateNumber = view.text.toString().trim().uppercase()
                if (plateNumber.isNotEmpty()) {
                    addPlateToDatabase(plateNumber)
                } else {
                    Toast.makeText(this, "Plaka numarasını girin", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addPlateToDatabase(plateNumber: String) {
        val plateData = mapOf(
            "plateNumber" to plateNumber,
            "reason" to "Şüpheli araç",
            "createdAt" to System.currentTimeMillis(),
            "createdBy" to "admin123",
            "severity" to "ORTA"
        )

        firestore.collection("suspicious_plates")
            .add(plateData)
            .addOnSuccessListener { docRef ->
                Toast.makeText(
                    this,
                    "Plaka başarıyla eklendi",
                    Toast.LENGTH_SHORT
                ).show()
                // Liste otomatik yenilenecek (snapshot listener)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Ekleme hatası: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun deletePlate(plateId: String) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Plakayı Sil")
            .setMessage("Bu plakayı silmek istediğinize emin misiniz?")
            .setPositiveButton("Evet") { _, _ ->
                firestore.collection("suspicious_plates")
                    .document(plateId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Plaka silindi",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Liste otomatik yenilenecek (snapshot listener)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Silme hatası: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}