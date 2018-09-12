package com.ubcoin.model.response

import com.google.gson.annotations.SerializedName
import com.ubcoin.model.PurchaseUser

/**
 * Created by Yuriy Aizenberg
 */
data class DealItemWrapper(
        val id: String,
        val status: MarketItemStatus,
        val createdDate: String,
        @SerializedName("item")
        val dealItem: DealItem,
        val buyer: PurchaseUser,
        val seller: PurchaseUser
)