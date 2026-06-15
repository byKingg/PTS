package com.kingsecurity.pts.models

data class SuspiciousPlate(
    val plateNumber: String = "",
    val reason: String = "",
    val createdAt: Long = 0L,
    val createdBy: String = "",
    val severity: String = "MEDIUM",
    var documentId: String = ""
)