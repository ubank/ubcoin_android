package com.ubcoin.network.request

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class ActivateDeactivateRequest(
        val itemId: String
) : Serializable