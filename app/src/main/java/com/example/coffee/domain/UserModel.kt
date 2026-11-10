package com.example.coffee.domain

import java.io.Serializable

data class UserModel(
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var address: String = "",
    var memberTier: String = "Gold Member",
    var points: Int = 240,
    var ordersCount: Int = 8,
    var lastUpdated: Long = System.currentTimeMillis()
) : Serializable
