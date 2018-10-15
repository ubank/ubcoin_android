package com.ubcoin.utils

import android.view.View

/**
 * Created by Yuriy Aizenberg
 */


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}