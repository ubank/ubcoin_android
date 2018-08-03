package com.ubcoin.adapter

import android.content.Context
import android.graphics.Rect
import android.support.annotation.DimenRes
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Yuriy Aizenberg
 */

class GridItemOffsetDecoration : RecyclerView.ItemDecoration {

    val itemOffset : Int

    constructor(itemOffset: Int) : super() {
        this.itemOffset = itemOffset
    }

    constructor(context: Context, @DimenRes dimen: Int) : this(context.resources.getDimensionPixelSize(dimen))

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(itemOffset, itemOffset, itemOffset, itemOffset)
    }

}