package com.kingsecurity.pts.models

data class PlateData(
    val id: String = "",
    val plateNumber: String = "",
    val detectionTime: Long = 0L,
    val userId: String = "",
    val imageUrl: String = ""
)