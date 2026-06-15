package com.kingsecurity.pts.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.kingsecurity.pts.activities.LoginActivity
import android.content.Intent
import android.util.Log

class InactivityManager(private val context: Context, private val activity: AppCompatActivity) {
    
    private val handler = Handler(Looper.getMainLooper())
    private val sharedPref = SharedPrefHelper(context)
    private var inactivityRunnable: Runnable? = null
    private val inactivityTimeout = 5 * 60 * 1000L // 5 dakika
    
    fun resetInactivityTimer() {
        inactivityRunnable?.let { handler.removeCallbacks(it) }
        
        inactivityRunnable = Runnable {
            logout()
        }
        
        handler.postDelayed(inactivityRunnable!!, inactivityTimeout)
        sharedPref.setLastActivityTime(System.currentTimeMillis())
        Log.d("InactivityManager", "İnaktivite sayacı sıfırlandı")
    }
    
    fun startListening() {
        resetInactivityTimer()
        Log.d("InactivityManager", "İnaktivite dinlemesi başladı")
    }
    
    fun stopListening() {
        inactivityRunnable?.let { handler.removeCallbacks(it) }
        Log.d("InactivityManager", "İnaktivite dinlemesi durduruldu")
    }
    
    private fun logout() {
        Log.d("InactivityManager", "İnaktivite zaman aşımı - otomatik çıkış")
        sharedPref.clearAll()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        activity.finish()
    }
}