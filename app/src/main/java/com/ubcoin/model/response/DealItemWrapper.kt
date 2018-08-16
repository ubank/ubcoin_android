package com.ubcoin.model.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Yuriy Aizenberg
 */
data class DealItemWrapper (
        val id: String,
        val buyerId: String,
        val status: String,
        val createdDate: String,
        @SerializedName("item")
        val dealItem: DealItem
)