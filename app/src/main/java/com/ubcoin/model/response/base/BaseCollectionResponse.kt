package com.ubcoin.model.response.base

/**
 * Created by Yuriy Aizenberg
 */

open class BaseCollectionResponse<T> {
    var data: List<T> = ArrayList()
    var pageData: PageData? = null
}