package com.ubcoin.model.response

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class DealItem(
        val id: String,
         val categoryId: String,
        val title: String,
        val price: Double,
        val description: String,
        val images: List<String>?
) : Serializable