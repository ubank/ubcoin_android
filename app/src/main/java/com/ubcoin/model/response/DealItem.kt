package com.ubcoin.model.response

/**
 * Created by Yuriy Aizenberg
 */
data class DealItem(
        val id: String,
        val categoryId: String?,
        val title: String?,
        val price: Float,
        val description: String?,
        val disableNotifyEmail: Boolean,
        val agreement: Boolean,
        val images: List<String>?
)