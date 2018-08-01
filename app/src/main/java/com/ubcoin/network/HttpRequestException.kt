package com.ubcoin.network

import com.ubcoin.model.Error

/**
 * Created by Yuriy Aizenberg
 */
class HttpRequestException : Throwable() {

    var throwable: Throwable? = null
    var error: Error? = null

}