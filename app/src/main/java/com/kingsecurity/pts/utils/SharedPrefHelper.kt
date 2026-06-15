package com.kingsecurity.pts.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("PTS_PREFS", Context.MODE_PRIVATE)

    fun setUserEmail(email: String) {
        sharedPreferences.edit().putString("USER_EMAIL", email).apply()
    }

    fun getUserEmail(): String {
        return sharedPreferences.getString("USER_EMAIL", "") ?: ""
    }

    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("BIOMETRIC_ENABLED", enabled).apply()
    }

    fun getBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean("BIOMETRIC_ENABLED", false)
    }

    fun setLastActivityTime(time: Long) {
        sharedPreferences.edit().putLong("LAST_ACTIVITY_TIME", time).apply()
    }

    fun getLastActivityTime(): Long {
        return sharedPreferences.getLong("LAST_ACTIVITY_TIME", 0L)
    }

    fun setInactivityTimeout(timeout: Long) {
        sharedPreferences.edit().putLong("INACTIVITY_TIMEOUT", timeout).apply()
    }

    fun getInactivityTimeout(): Long {
        return sharedPreferences.getLong("INACTIVITY_TIMEOUT", 5 * 60 * 1000) // 5 minutes default
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}