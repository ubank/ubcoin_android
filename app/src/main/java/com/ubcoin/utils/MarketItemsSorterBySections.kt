package com.ubcoin.utils

import com.ubcoin.model.response.MarketItem
import java.util.*
import kotlin.Comparator

/**
 * Created by Yuriy Aizenberg
 */
object MarketItemsSorterBySections {


    fun sort(marketItem : List<MarketItem>) {
        Collections.sort(marketItem) { o1, o2 -> o1!!.status!!.inListPositionIndex.compareTo(o2!!.status!!.inListPositionIndex) }
    }

}