package com.ubcoin.network.request

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class Withdraw(
        val currencyType: String,
        val amountETH: Double?,
        val amountUBC: Double?,
        val externalAddress: String

) : Serializable