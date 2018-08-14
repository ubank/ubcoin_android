package com.ubcoin.network.request

/**
 * Created by Yuriy Aizenberg
 */
data class ChangeForgotPassword(
        val email: String,
        val value: String,
        val type: String,
        val code: String
)