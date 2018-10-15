package com.ubcoin.model.ui.order

/**
 * Created by Yuriy Aizenberg
 */
enum class OrderDirection {
    NONE,
    ASC,
    DESC;

    fun getQueryRepresentation() = name.toLowerCase()
}