package com.ubcoin.network.request

import com.ubcoin.model.response.Location

/**
 * Created by Yuriy Aizenberg
 */
data class CreateProductRequest(
        val categoryId: String?,
        val title: String,
        val description: String?,
        val price: Double,
        val location: Location?,
        val disableNotifyToEmail: Boolean,
        val agreement: Boolean,
        var images: List<String>,
        val condition: String?,
        val fileUrl: String?
)