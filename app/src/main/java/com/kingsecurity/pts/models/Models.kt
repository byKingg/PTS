package com.kingsecurity.pts.models

data class User(
    val userId: String = "",
    val email: String = "",
    val isApproved: Boolean = false,
    val isAdmin: Boolean = false,
    val createdAt: Long = 0L,
    val lastLogin: Long = 0L
)

data class SuspiciousPlate(
    val plateNumber: String = "",
    val reason: String = "",
    val createdAt: Long = 0L,
    val createdBy: String = "",
    val severity: String = "ORTA",
    var documentId: String = ""
)

data class DetectionResult(
    val plateNumber: String = "",
    val confidence: Float = 0f,
    val timestamp: Long = 0L,
    val userId: String = "",
    val isSuspicious: Boolean = false,
    val location: String = ""
)

data class PlateData(
    val id: String = "",
    val plateNumber: String = "",
    val detectionTime: Long = 0L,
    val userId: String = "",
    val imageUrl: String = ""
)