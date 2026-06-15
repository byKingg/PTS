package com.kingsecurity.pts.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kingsecurity.pts.models.User
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
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun isAdmin(userId: String): Boolean {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            val user = document.toObject(User::class.java)
            user?.isAdmin ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentUser(): String? = auth.currentUser?.uid
    
    fun getCurrentUserEmail(): String? = auth.currentUser?.email
    
    fun logout() {
        auth.signOut()
    }
}