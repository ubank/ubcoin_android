package com.ubcoin.model

import com.ubcoin.model.response.PurchaseItemStatus
import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class Purchase(
        val id: String,
        val buyer: PurchaseUser,
        val seller: PurchaseUser,
        val status: PurchaseItemStatus?,
        val createdDate: String,
        val updatedDate: String
) : IPurchaseObject, Serializable