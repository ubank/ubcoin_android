package com.ubcoin.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DimenRes
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator

/**
 * Created by Yuriy Aizenberg
 */
object TextDrawableUtils {

    fun createTextDrawableRounded(userName: String?, @DimenRes dimen: Int, context: Context) : Drawable {
        return createTextDrawableRounded(userName, context.resources.getDimensionPixelSize(dimen))
    }


    fun createTextDrawableRounded(userName: String?, radius: Int) : Drawable {
        val firstSymbol = if (userName != null && !userName.isEmpty()) userName[0].toString().toUpperCase() else "?"
        val color = ColorGenerator.MATERIAL.getColor(firstSymbol)
        return TextDrawable.builder().buildRoundRect(firstSymbol, color, radius)
    }

}