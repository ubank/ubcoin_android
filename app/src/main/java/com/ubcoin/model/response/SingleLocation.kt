package com.ubcoin.model.response

/**
 * Created by Yuriy Aizenberg
 */

data class SingleLocation(
        val text: String,
        val countryCode: String?,
        val longPoint: String,
        val latPoint: String
)
