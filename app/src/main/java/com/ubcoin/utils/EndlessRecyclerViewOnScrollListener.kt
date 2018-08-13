package com.ubcoin.utils

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

abstract class EndlessRecyclerViewOnScrollListener(linearLayoutManager: RecyclerView.LayoutManager) : RecyclerView.OnScrollListener() {

    private var mVisibleThreshold = DEFAULT_VISIBLE_THRESHOLD

    private var mLinearLayoutManager: RecyclerView.LayoutManager? = linearLayoutManager


    fun setVisibleThreshold(visibleThreshold: Int) {
        mVisibleThreshold = visibleThreshold
    }

    fun setLinearLayoutManager(linearLayoutManager: LinearLayoutManager) {
        mLinearLayoutManager = linearLayoutManager
    }


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (mLinearLayoutManager == null) {
            throw IllegalStateException("LinearLayoutManager cannot be null. Set the LinearLayoutManager before the user is allowed to scroll!")
        }

        val visibleItemCount = recyclerView.childCount
        val totalItemCount = mLinearLayoutManager!!.itemCount

        val firstVisibleItemPosition = if (mLinearLayoutManager is LinearLayoutManager) {
            (mLinearLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        } else {
            (mLinearLayoutManager as GridLayoutManager).findFirstVisibleItemPosition()
        }


        if (totalItemCount - visibleItemCount <= firstVisibleItemPosition + mVisibleThreshold) {
            onLoadMore()
        }
    }

    abstract fun onLoadMore()

    companion object {

        private val DEFAULT_VISIBLE_THRESHOLD = 5
    }
}