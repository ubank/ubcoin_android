package com.ubcoin.model.response

import com.ubcoin.model.response.profile.Settings

/**
 * Created by Yuriy Aizenberg
 */
data class User(
        val rating: Float?,
        val reviewsCount: Int?,
        val itemsCount: Int?,
        val avatarUrl: String?,
        var birthDate: String?,
        var email: String?,
        var id: String?,
        var name: String?,
        var phone: String?,
        var settings: Settings?,
        var walletNumber: Long?
) {
}