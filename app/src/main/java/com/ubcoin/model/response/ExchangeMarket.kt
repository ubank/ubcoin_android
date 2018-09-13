package com.ubcoin.model.response

import android.text.TextUtils
import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */

data class ExchangeMarket (
        val id: String,
        val name: String,
        val icon: String?,
        val url: String
) : Serializable {

    fun hasIcon() = !TextUtils.isEmpty(icon)

}