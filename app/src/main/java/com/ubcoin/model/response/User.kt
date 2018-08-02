package com.ubcoin.model.response

/**
 * Created by Yuriy Aizenberg
 */
data class User(
        val rating: Float,
        val reviewsCount: Int,
        val itemsCount: Int,
        val avatarUrl: String,
        var birthDate: String,
        var email: String,
        var id: String,
        var name: String,
        var phone: String,
        var settings: String,
        var walletNumber: Long
) {
}