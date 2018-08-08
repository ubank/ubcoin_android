package com.ubcoin.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class MarketItem(
        val id: String?,
        val category: Category?,
        val user: User?,
        val description: String?,
        @SerializedName("is_favorite")
        val isFavorite: Boolean?,
        val images: List<String>?,
        val createdDate: String?,
        val favorite: Boolean?,
        val status: String?,
        val title: String?,
        val price: Float?
) : Serializable