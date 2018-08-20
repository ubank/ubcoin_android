package com.ubcoin.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Yuriy Aizenberg
 */
open class BaseApplicationModel<T> {

    @SerializedName("error")
    var errorWrapper: ErrorWrapper? = null
    var t: T? = null

}