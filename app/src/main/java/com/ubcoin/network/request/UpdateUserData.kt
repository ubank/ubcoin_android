package com.ubcoin.network.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Yuriy Aizenberg
 */
data class UpdateUserData(
        @SerializedName("name")
        val userName: String,
        val email: String
)