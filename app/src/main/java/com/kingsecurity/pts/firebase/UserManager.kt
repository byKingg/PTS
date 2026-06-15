package com.kingsecurity.pts.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserManager {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun updateLastLogin(userId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .update("lastLogin", System.currentTimeMillis())
                .await()
            Log.d("UserManager", "Son Giriş Zamanı Güncellendi")
        } catch (e: Exception) {
            Log.e("UserManager", "Güncelleme hatası: ${e.message}")
        }
    }

    suspend fun isAdmin(userId: String): Boolean {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            val isAdmin = document.getBoolean("isAdmin") ?: false
            Log.d("UserManager", "Admin Kontrolü: $isAdmin")
            isAdmin
        } catch (e: Exception) {
            Log.e("UserManager", "Admin kontrol hatası: ${e.message}")
            false
        }
    }

    fun getCurrentUser(): String? = auth.currentUser?.uid
    
    fun getCurrentUserEmail(): String? = auth.currentUser?.email
    
    fun logout() {
        auth.signOut()
        Log.d("UserManager", "Kullanıcı Çıkış Yaptı")
    }
}