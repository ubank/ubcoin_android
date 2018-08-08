package com.ubcoin.view

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.ubcoin.R


/**
 * Created by Yuriy Aizenberg
 */
class ScrollingBehavior : CoordinatorLayout.Behavior<RelativeLayout> {
    private val toolbarHeight: Int


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        toolbarHeight = context?.resources?.getDimensionPixelSize(R.dimen.header_size)!!
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, layout: RelativeLayout, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            val lp = layout.layoutParams as CoordinatorLayout.LayoutParams
            val fabBottomMargin = lp.bottomMargin
            val distanceToScroll = layout.height + fabBottomMargin
            val ratio = dependency.getY() / toolbarHeight as Float
            layout.translationY = -distanceToScroll * ratio
        }
        return true
    }




}