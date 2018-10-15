package com.ubcoin.model.response

import com.ubcoin.utils.toDate
import java.io.Serializable
import java.util.*

/**
 * Created by Yuriy Aizenberg
 */
data class TopUp(
        val ubCoinAddress: String,
        val qrURL: String,
        var startUsage: String?,
        var finishUsage: String?
) : Serializable {


    fun startUsageDate() : Date? {
        return startUsage?.toDate()
    }

    fun finishUsageDate() : Date? {
        return finishUsage?.toDate()
    }

    fun millisBetween() : Long {
        val startUsageDate = startUsageDate()
        val finishUsageDate = finishUsageDate()
        if (startUsageDate == null || finishUsageDate == null) return 0L
        return finishUsageDate.time - startUsageDate.time
    }


}