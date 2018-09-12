package com.ubcoin.utils

import com.ubcoin.model.response.MarketItem

/**
 * Created by Yuriy Aizenberg
 */
object MarketItemsSorterBySections {

    fun sort(marketItem: List<MarketItem>): List<MarketItem> {
        return marketItem.sortedWith(Comparator { o1, o2 -> o1!!.status!!.name.toUpperCase().compareTo(o2!!.status!!.name.toUpperCase()) })
    }

}