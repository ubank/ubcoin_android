package com.ubcoin.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageSwitcher
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

/**
 * Created by Yuriy Aizenberg
 */
class ImageSwitcherTarget : Target {

    private val imageSwitcher : ImageSwitcher
    private val context : Context

    constructor(imageSwitcher: ImageSwitcher, context: Context) {
        this.imageSwitcher = imageSwitcher
        this.context = context
    }


    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

    }

    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

    }

    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
        imageSwitcher.setImageDrawable(BitmapDrawable(context.resources, bitmap))
    }
}