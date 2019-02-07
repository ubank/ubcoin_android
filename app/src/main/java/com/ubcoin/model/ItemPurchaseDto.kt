package com.ubcoin.model

data class ItemPurchaseDto (
    var comment: String,
    var currencyType: String,
    var itemId: String,
    var purchaseId: String,
    var withDelivery: Boolean
)