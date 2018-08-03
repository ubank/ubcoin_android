package com.ubcoin.network

import com.ubcoin.model.Error

/**
 * Created by Yuriy Aizenberg
 */
class HttpRequestException : Throwable {

    var throwable: Throwable? = null
    var error: Error? = null



    constructor(throwable: Throwable?, error: Error?) : super() {
        this.throwable = throwable
        this.error = error
    }

    constructor() : super()

    fun isServerError() : Boolean = error != null

    fun toServerErrorString() : String {
        return error!!.toString()
    }
}