package com.ubcoin.model

/**
 * Created by Yuriy Aizenberg
 */
class SellImageModel {

    var filePath: String? = null
    var serverUrl: String?= null

    fun hasImage() = filePath != null

    fun hasServerImage() = serverUrl != null

}