package com.ubcoin.view.rating

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.IntRange
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.ubcoin.R

/**
 * Created by Yuriy Aizenberg
 */

class RatingBarView : LinearLayout {

    var stars: List<ImageView?> = ArrayList<ImageView>()

    var activeDrawable: Drawable? = null
    var inActiveDrawable: Drawable? = null
    var isTransparentInactive: Boolean = false

    constructor(context: Context?) : super(context) {
        initialize(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        inflate(context, R.layout.view_mini_rating_bar, this)

        stars = listOf(
                findViewById(R.id.star_1),
                findViewById(R.id.star_2),
                findViewById(R.id.star_3),
                findViewById(R.id.star_4),
                findViewById(R.id.star_5)
        )


        attrs?.run {
            val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.RatingBarViewStyle, 0, 0)

            activeDrawable = attributes.getDrawable(R.styleable.RatingBarViewStyle_RatingIcon)
            inActiveDrawable = attributes.getDrawable(R.styleable.RatingBarViewStyle_RatingInactiveIcon)
            isTransparentInactive = attributes.getBoolean(R.styleable.RatingBarViewStyle_RatingInactiveTransparent, true)

            setRating(attributes.getInt(R.styleable.RatingBarViewStyle_RatingValue, 0))
            attributes.recycle()
        }


    }

    private fun reset() {
        stars.forEach {
            if (inActiveDrawable != null) {
                it?.setImageDrawable(inActiveDrawable)
            } else {
                if (isTransparentInactive) {
                    it?.visibility = View.INVISIBLE
                    it?.isClickable = false
                } else {
                    it?.visibility = View.VISIBLE
                    it?.isClickable = true
                }
            }
        }
    }

    fun setRating(@IntRange(from = 0, to = 5) range: Int) {
        reset()
        (0 until range)
                .map(stars::get)
                .forEach {
                    it?.visibility = View.VISIBLE
                    it?.setImageDrawable(activeDrawable)
                }
    }
}