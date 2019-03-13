package com.ubcoin.model.response

import android.content.Context
import com.ubcoin.R
import com.ubcoin.model.Currency
import com.ubcoin.utils.moneyFormat
import com.ubcoin.utils.moneyFormatETH
import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class SingleTransaction(
        val id: String,
        val amountUBC: Double,
        val amountETH: Double,
        val status: String,
        val type: String,
        val from: String,
        val to: String,
        val createdDate: String,
        val currencyType: Currency

) : Serializable {

    fun isPositive(): Boolean {

        if(currencyType == Currency.UBC)
            return amountUBC > .0

        if(currencyType == Currency.ETH)
            return amountETH > .0

        return false
    }

    fun getStringValue(context: Context): String{
        val ubcPostfix: String = context.getString(R.string.ubc_postfix)
        val ethPostfix: String = context.getString(R.string.eth_postfix)
        if(currencyType == Currency.UBC)
            return """${if (amountUBC > .0) "+ " else ""}${amountUBC.moneyFormat()} $ubcPostfix"""

        if(currencyType == Currency.ETH)
            return """${if (amountETH > .0) "+ " else ""}${amountETH.moneyFormatETH()} $ethPostfix"""

        return ""
    }

    fun isPending() = "IN_PROGRESS" == status

    fun isRejected() = "REJECTED" == status

}