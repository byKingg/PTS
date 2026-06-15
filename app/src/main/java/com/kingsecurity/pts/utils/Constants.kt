package com.kingsecurity.pts.utils

object Constants {
    // Firestore Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_SUSPICIOUS_PLATES = "suspicious_plates"
    const val COLLECTION_DETECTIONS = "detections"
    
    // Test Document IDs (Firebase'den kopyalandı)
    const val TEST_USER_ID = "FMhG01OeZsbKA31jaVku"
    const val TEST_PLATE_ID = "UCKIEixBlblGyqEXZsZy"
    const val TEST_DETECTION_ID = "39QRQvK4z4hCgTJ7vCat"
    
    // Timeouts
    const val INACTIVITY_TIMEOUT = 5 * 60 * 1000 // 5 dakika
    const val SPLASH_SCREEN_DURATION = 3000L // 3 saniye
    
    // ML Model
    const val YOLO_MODEL_PATH = "yolov8n.tflite"
    const val CONFIDENCE_THRESHOLD = 0.5f
    
    // User Roles
    const val ADMIN_ROLE = "yönetici"
    const val USER_ROLE = "kullanıcı"
    
    // Severity Levels
    const val SEVERITY_LOW = "DÜŞÜK"
    const val SEVERITY_MEDIUM = "ORTA"
    const val SEVERITY_HIGH = "YÜKSEK"
    const val SEVERITY_CRITICAL = "KRİTİK"
}