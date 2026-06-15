package com.kingsecurity.pts.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kingsecurity.pts.models.User
import kotlinx.coroutines.tasks.await

class FirebaseManager {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun checkUserApproval(userId: String): Boolean {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            val isApproved = document.getBoolean("isApproved") ?: false
            Log.d("FirebaseManager", "Kullanıcı Onayı: $isApproved")
            isApproved
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Onay kontrol hatası: ${e.message}")
            false
        }
    }

    suspend fun getSuspiciousPlates(): List<String> {
        return try {
            val documents = firestore.collection("suspicious_plates")
                .get()
                .await()
            
            documents.mapNotNull { doc ->
                doc.getString("plateNumber")
            }.also { plates ->
                Log.d("FirebaseManager", "Şüpheli Plakalar Yüklendi: ${plates.size}")
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Plaka yükleme hatası: ${e.message}")
            emptyList()
        }
    }

    fun setupRealtimePlateUpdates(callback: (List<String>) -> Unit) {
        firestore.collection("suspicious_plates")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("FirebaseManager", "Dinleme hatası: ${error.message}")
                    return@addSnapshotListener
                }

                val plates = snapshots?.mapNotNull { doc ->
                    doc.getString("plateNumber")
                } ?: emptyList()

                Log.d("FirebaseManager", "Gerçek Zamanl Plakalar Güncelleştirildi: ${plates.size}")
                callback(plates)
            }
    }
}