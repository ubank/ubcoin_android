package com.ubcoin.model

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class ConversionResponse (
        val currencyFrom: String,
        val currencyTo: String,
        val amount: Double

) : Serializable
