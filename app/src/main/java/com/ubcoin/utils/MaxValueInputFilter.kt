package com.ubcoin.utils

import android.text.InputFilter
import android.text.Spanned

/**
 * Created by Yuriy Aizenberg
 */

const val DEFAULT_MAX_VALUE : Double = 1000000000.toDouble()

class MaxValueInputFilter : InputFilter {

    private val maxFilterValue: Double

    constructor(maxFilterValue: Double) {
        this.maxFilterValue = maxFilterValue
    }

    constructor() {
        maxFilterValue = DEFAULT_MAX_VALUE
    }

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toDouble()
            if (input <= maxFilterValue) {
                return null
            }
        } catch (e: Exception) {
        }
        return ""
    }

}
