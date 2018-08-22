package com.ubcoin.network.request

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class ConversionRequest (
        val currencyFrom: String,
        val currencyTo: String,
        val amount: String

) : Serializable
