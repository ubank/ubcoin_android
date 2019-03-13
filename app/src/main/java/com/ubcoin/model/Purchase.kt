package com.ubcoin.model

import com.ubcoin.model.response.MarketItem
import com.ubcoin.model.response.PurchaseItemStatus
import com.ubcoin.model.response.StatusDescription
import com.ubcoin.model.response.User
import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class Purchase(
        val id: String,
        val buyer: User,
        val seller: User,
        val status: PurchaseItemStatus?,
        val createdDate: String,
        val updatedDate: String,
        val comment: String,
        val statusDescriptions: List<StatusDescription>,
        val item: MarketItem,
        val withDelivery: Boolean,
        val deliveryPrice: Double,
        val currencyType: Currency,
        val needAction: Boolean?
) : IPurchaseObject, Serializable