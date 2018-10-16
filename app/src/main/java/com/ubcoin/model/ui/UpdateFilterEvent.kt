package com.ubcoin.model.ui

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
class UpdateFilterEvent : Serializable {
    val type: FilterType
    val directly: Boolean

    constructor(type: FilterType) : this(type, false)

    constructor(type: FilterType, directly: Boolean) {
        this.type = type
        this.directly = directly
    }


}