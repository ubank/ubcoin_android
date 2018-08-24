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

    fun isPositive() = amountUBC > .0

    fun isPending() = "IN_PROGRESS" == status

}