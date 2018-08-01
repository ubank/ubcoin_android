package com.ubcoin.view

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.widget.TextView
import com.ubcoin.R

/**
 * Created by Yuriy Aizenberg
 */
class RobotoRegularTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {




    override fun onFinishInflate() {
        super.onFinishInflate()
        typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
    }

}