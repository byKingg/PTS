package com.kingsecurity.pts.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("PTS_PREFS", Context.MODE_PRIVATE)

    // Kullanıcı Email
    fun setUserEmail(email: String) {
        sharedPreferences.edit().putString("KULLANICI_EMAIL", email).apply()
    }

    fun getUserEmail(): String {
        return sharedPreferences.getString("KULLANICI_EMAIL", "") ?: ""
    }

    // Biometrik Etkinleştirme
    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("BIOMETRIK_ETKIN", enabled).apply()
    }

    fun getBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean("BIOMETRIK_ETKIN", false)
    }

    // Son Aktivite Zamanı
    fun setLastActivityTime(time: Long) {
        sharedPreferences.edit().putLong("SON_AKTIVITE_ZAMANI", time).apply()
    }

    fun getLastActivityTime(): Long {
        return sharedPreferences.getLong("SON_AKTIVITE_ZAMANI", 0L)
    }

    // İnaktivite Zaman Aşımı
    fun setInactivityTimeout(timeout: Long) {
        sharedPreferences.edit().putLong("INAKTIVITE_TIMEOUT", timeout).apply()
    }

    fun getInactivityTimeout(): Long {
        return sharedPreferences.getLong("INAKTIVITE_TIMEOUT", 5 * 60 * 1000) // 5 dakika varsayılan
    }

    // Kullanıcı ID
    fun setUserId(userId: String) {
        sharedPreferences.edit().putString("KULLANICI_ID", userId).apply()
    }

    fun getUserId(): String {
        return sharedPreferences.getString("KULLANICI_ID", "") ?: ""
    }

    // Admin Durumu
    fun setIsAdmin(isAdmin: Boolean) {
        sharedPreferences.edit().putBoolean("YONETICI_MI", isAdmin).apply()
    }

    fun getIsAdmin(): Boolean {
        return sharedPreferences.getBoolean("YONETICI_MI", false)
    }

    // Tüm Verileri Temizle
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}