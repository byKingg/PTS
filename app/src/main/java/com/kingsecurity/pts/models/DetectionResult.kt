package com.kingsecurity.pts.models

data class DetectionResult(
    val plateNumber: String = "",
    val confidence: Float = 0f,
    val timestamp: Long = 0L,
    val detectedBy: String = "",
    val isSuspicious: Boolean = false,
    val location: String = ""
)