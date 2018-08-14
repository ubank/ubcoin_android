package com.ubcoin.model.response

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class MarketItem(
        val id: String,
        val category: Category?,
        val user: User?,
        val description: String?,
        val images: List<String>?,
        val location: Location?,
        val createdDate: String?,
        var favorite: Boolean,
        val status: String?,
        val title: String?,
        val price: Float?
) : Serializable