package com.ubcoin.model.response.profile

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */

data class Profile(
        val id: String,
        val name: String?,
        val email: String?,
        val wallet: Wallet?,
        val rating: Int,
        val reviewsCount: Int,
        val itemsCount: Int,
        val settings: Settings
) : Serializable