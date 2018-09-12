package com.ubcoin.model.response

import com.google.gson.annotations.SerializedName
import com.ubcoin.R

/**
 * Created by Yuriy Aizenberg
 */
enum class PurchaseItemStatus {

    @SerializedName("CREATED")
    CREATED,
    @SerializedName("ACTIVE")
    ACTIVE,
    @SerializedName("CONFIRMED")
    CONFIRMED,
    @SerializedName("CANCELED")
    CANCELED


}