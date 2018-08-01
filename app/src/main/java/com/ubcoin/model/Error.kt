package com.ubcoin.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Yuriy Aizenberg
 */
class Error {

    @SerializedName("validation")
    var errorValidations: List<ErrorValidation>? = null
}
