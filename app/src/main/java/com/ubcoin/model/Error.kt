package com.ubcoin.model

import com.google.gson.annotations.SerializedName
import com.ubcoin.utils.CollectionExtensions

/**
 * Created by Yuriy Aizenberg
 */
class Error {

    @SerializedName("validation")
    var errorValidations: List<ErrorValidation>? = null

    override fun toString(): String {
        if (CollectionExtensions.nullOrEmpty(errorValidations)) return "None"
        var toReturn = ""
        errorValidations?.forEach {
            toReturn += "\n" + it.message
        }
        return toReturn
    }
}
