package com.ubcoin.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation


/**
 * Created by Yuriy Aizenberg
 */

fun View.expand() {
    expand(-1L)
}

fun View.expand(duration: Long) {
    measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targetHeight = measuredHeight

    layoutParams.height = 1
    visibility = View.VISIBLE
    val a = object : Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            layoutParams.height = if (interpolatedTime == 1f)
                ViewGroup.LayoutParams.WRAP_CONTENT
            else
                (targetHeight * interpolatedTime).toInt()
            requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    a.duration = if (duration != -1L) duration else ((targetHeight / context.resources.displayMetrics.density).toInt()).toLong()
    startAnimation(a)
}

fun View.collapse() {
    collapse(-1L)
}

fun View.collapse(duration: Long) {
    val initialHeight = measuredHeight

    val a = object : Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            if (interpolatedTime == 1f) {
                layoutParams.height = 1
                requestLayout()
            } else {
                layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    a.duration = if (duration != -1L) duration else ((initialHeight / context.resources.displayMetrics.density).toInt()).toLong()
    startAnimation(a)
}