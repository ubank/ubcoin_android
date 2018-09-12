package com.ubcoin.model

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class PurchaseUser(
        val id: String,
        val name: String,
        val rating: Float,
        val reviewsCount: Int,
        val itemsCount: Int,
        val avatarUrl: String?
) : Serializable