package com.ubcoin.network.request

/**
 * Created by Yuriy Aizenberg
 */
data class ConfirmEmailRegistration(
        val type: String,
        val email: String,
        val code: String
)