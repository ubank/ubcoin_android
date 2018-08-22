package com.ubcoin.model.response

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class TopUp (
        val ubCoinAddress: String,
        val qrURL: String
) : Serializable