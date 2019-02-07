package com.ubcoin.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class DealItemWrapper(
        val id: String,
        val status: MarketItemStatus,
        val createdDate: String,
        @SerializedName("item")
        val item: DealItem,
        val buyer: User,
        val seller: User,
        val withDelivery: Boolean?
) : Serializable