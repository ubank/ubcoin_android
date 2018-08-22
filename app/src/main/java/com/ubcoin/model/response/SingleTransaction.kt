package com.ubcoin.model.response

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class SingleTransaction(
        val id: String,
        val amountUBC: Double,
        val status: String,
        val type: String,
        val from: String,
        val to: String,
        val createdDate: String

) : Serializable {

    private fun isSuccess()  = "SUCCESS" == status.toUpperCase()

    enum class Status {
        IN_PROGRESS
    }

    enum class Type {
        TRANSFER
    }

}