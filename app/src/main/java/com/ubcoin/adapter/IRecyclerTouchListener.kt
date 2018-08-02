package com.ubcoin.adapter

/**
 * Created by Yuriy Aizenberg
 */

interface IRecyclerTouchListener<T> {

    fun onItemClick(data: T, position: Int)

}