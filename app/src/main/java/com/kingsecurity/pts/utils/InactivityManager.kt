package com.kingsecurity.pts.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.kingsecurity.pts.activities.LoginActivity

class InactivityManager(private val context: Context, private val activity: AppCompatActivity) {
    
    private val handler = Handler(Looper.getMainLooper())
    private val sharedPref = SharedPrefHelper(context)
    private var inactivityRunnable: Runnable? = null
    private val inactivityTimeout = 5 * 60 * 1000L // 5 minutes
    
    fun resetInactivityTimer() {
        inactivityRunnable?.let { handler.removeCallbacks(it) }
        
        inactivityRunnable = Runnable {
            logout()
        }
        
        handler.postDelayed(inactivityRunnable!!, inactivityTimeout)
        sharedPref.setLastActivityTime(System.currentTimeMillis())
    }
    
    fun startListening() {
        resetInactivityTimer()
    }
    
    fun stopListening() {
        inactivityRunnable?.let { handler.removeCallbacks(it) }
    }
    
    private fun logout() {
        sharedPref.clearAll()
        val intent = android.content.Intent(context, LoginActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        activity.finish()
    }
}