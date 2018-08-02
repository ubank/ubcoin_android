package com.ubcoin.view.menu

import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Yuriy Aizenberg
*/
object Indexer {

    private val currentIndex : AtomicInteger = AtomicInteger()

    fun getIndex() : Int = currentIndex.incrementAndGet()

}