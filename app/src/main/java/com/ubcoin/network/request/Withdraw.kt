package com.ubcoin.network.request

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class Withdraw(
        val externalAddress: String,
        val amountUBC: Double

) : Serializable