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
            
            val user = document.toObject(User::class.java)
            user?.isApproved ?: false
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error checking approval: ${e.message}")
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
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error fetching plates: ${e.message}")
            emptyList()
        }
    }

    fun setupRealtimePlateUpdates(callback: (List<String>) -> Unit) {
        firestore.collection("suspicious_plates")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("FirebaseManager", "Listen error: ${error.message}")
                    return@addSnapshotListener
                }

                val plates = snapshots?.mapNotNull { doc ->
                    doc.getString("plateNumber")
                } ?: emptyList()

                callback(plates)
            }
    }
}