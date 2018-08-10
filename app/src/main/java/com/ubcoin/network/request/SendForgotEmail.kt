package com.ubcoin.network.request

/**
 * Created by Yuriy Aizenberg
 */
data class SendForgotEmail(
        val type: String,
        val email: String
)