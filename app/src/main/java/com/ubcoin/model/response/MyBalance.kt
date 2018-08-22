package com.ubcoin.model.response

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class MyBalance(
        val effectiveAmount: Double,
        val amountOnHold: Double
): Serializable