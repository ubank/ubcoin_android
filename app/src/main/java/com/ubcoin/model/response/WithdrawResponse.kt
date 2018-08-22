package com.ubcoin.model.response

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class WithdrawResponse(
        val resultCode: Int,
        val message: String

) : Serializable {

    fun isSuccess() = resultCode == 0

}