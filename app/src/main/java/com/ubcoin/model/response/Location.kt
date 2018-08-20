package com.ubcoin.model.response

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class Location(var text: String, var latPoint: String?, var longPoint: String?) : Serializable {

    fun isAddressPresented() : Boolean {
        return latPoint != null && longPoint != null
    }

}