package com.ubcoin.model.ui

/**
 * Created by Yuriy Aizenberg
 */
open class FilterModel<T>(
        val type: FilterType,
        val model: T
)

enum class FilterType {
    CATEGORY,
    MAX_PRICE,
    MAX_DISTANCE,
    CONDITION,
    SORT_BY
}