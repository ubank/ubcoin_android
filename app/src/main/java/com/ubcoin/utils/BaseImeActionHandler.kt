package com.ubcoin.utils

import android.view.KeyEvent
import android.widget.TextView

/**
 * Created by Yuriy Aizenberg
 */
abstract class BaseImeActionHandler : TextView.OnEditorActionListener {

    abstract fun getActionId() : Int

    abstract fun onActionCall()

    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        if (p1 == getActionId()) {
            onActionCall()
            return true
        }
        return false
    }



}