package com.kingsecurity.pts.utils

object Constants {
    const val COLLECTION_USERS = "users"
    const val COLLECTION_SUSPICIOUS_PLATES = "suspicious_plates"
    const val COLLECTION_DETECTIONS = "detections"
    
    const val INACTIVITY_TIMEOUT = 5 * 60 * 1000 // 5 minutes
    const val SPLASH_SCREEN_DURATION = 3000L // 3 seconds
    
    const val YOLO_MODEL_PATH = "yolov8n.tflite"
    const val CONFIDENCE_THRESHOLD = 0.5f
    
    const val ADMIN_ROLE = "admin"
    const val USER_ROLE = "user"
}