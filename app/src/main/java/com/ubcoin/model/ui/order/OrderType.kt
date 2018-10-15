package com.ubcoin.model.ui.order

import com.ubcoin.R
/**
 * Created by Yuriy Aizenberg
 */
enum class OrderType {
    DATE(R.string.sort_by_date),
    PRICE(R.string.sort_by_price),
    DISTANCE(R.string.sort_by_distance);

    private val resId: Int

    constructor(resId: Int) {
        this.resId = resId
    }

    fun getResId() = resId
}