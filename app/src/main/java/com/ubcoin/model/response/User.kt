package com.ubcoin.model.response

import com.ubcoin.model.response.profile.Settings
import com.ubcoin.model.response.profile.Wallet
import java.io.Serializable
import java.util.*

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
        var location: String?,
        var createdDate: Date?,
        var phone: String?,
        var settings: Settings?,
        var walletNumber: Long?,
        var wallet: Wallet?,
        var authorizedInTg: Boolean?,
        var shareLink: String?
) : Serializable