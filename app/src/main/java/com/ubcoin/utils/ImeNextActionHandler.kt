package com.ubcoin.utils

import android.view.inputmethod.EditorInfo

/**
 * Created by Yuriy Aizenberg
 */
abstract class ImeNextActionHandler : BaseImeActionHandler() {

    override fun getActionId(): Int {
        return EditorInfo.IME_ACTION_NEXT
    }


}