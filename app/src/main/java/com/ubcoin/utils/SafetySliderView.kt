package com.ubcoin.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.daimajia.slider.library.R

/**
 * Created by Yuriy Aizenberg
 */

class SafetySliderView(context: Context) : CustomBaseSliderView(context) {

    override fun getView(): View {
        val view = LayoutInflater.from(context).inflate(R.layout.render_type_text, null)
        val imageView = view.findViewById<ImageView>(R.id.daimajia_slider_image)
        bindEventAndShow(view, imageView, true)
        return view
    }


}