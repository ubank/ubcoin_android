package com.ubcoin.network

import com.ubcoin.model.ErrorWrapper

/**
 * Created by Yuriy Aizenberg
 */
class HttpRequestException : Throwable {

    var throwable: Throwable? = null
    var errorWrapper: ErrorWrapper? = null
    var errorCode: Int = -1


    constructor(throwable: Throwable?, errorWrapper: ErrorWrapper?) : this(throwable, errorWrapper, -1)

    constructor(throwable: Throwable?, errorWrapper: ErrorWrapper?, errorCode: Int) {
        this.errorCode = errorCode
        this.throwable = throwable
        this.errorWrapper = errorWrapper
    }

    constructor() : super()

    fun isServerError(): Boolean = throwable == null

    fun toServerErrorString(): String {
        return errorWrapper?.error?.toString() ?: "Unknown"
    }
}