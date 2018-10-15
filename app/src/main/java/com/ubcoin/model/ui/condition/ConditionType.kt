package com.ubcoin.model.ui.condition
import com.ubcoin.R
/**
 * Created by Yuriy Aizenberg
 */
enum class ConditionType {
    NONE(-1),
    NEW(R.string.condition_new),
    USED(R.string.condition_used);

    private val resId: Int

    constructor(resId: Int) {
        this.resId = resId
    }

    fun getResId() = resId
}