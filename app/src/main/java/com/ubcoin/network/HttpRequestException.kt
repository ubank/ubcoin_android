package com.ubcoin.network

import com.ubcoin.model.Error

/**
 * Created by Yuriy Aizenberg
 */
class HttpRequestException : Throwable {

    var throwable: Throwable? = null
    var error: Error? = null
    var errorCode: Int = -1


    constructor(throwable: Throwable?, error: Error?) : this(throwable, error, -1)

    constructor(throwable: Throwable?, error: Error?, errorCode: Int) {
        this.errorCode = errorCode
        this.throwable = throwable
        this.error = error
    }

    constructor() : super()

    fun isServerError(): Boolean = throwable == null

    fun toServerErrorString(): String {
        return error!!.toString()
    }
}