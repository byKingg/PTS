package com.kingsecurity.pts.models

data class User(
    val userId: String = "",
    val email: String = "",
    val isApproved: Boolean = false,
    val isAdmin: Boolean = false,
    val createdAt: Long = 0L,
    val lastLogin: Long = 0L
)