package com.ubcoin.utils

import android.view.inputmethod.EditorInfo

/**
 * Created by Yuriy Aizenberg
 */
abstract class ImeDoneActionHandler : BaseImeActionHandler() {

    override fun getActionId(): Int {
        return EditorInfo.IME_ACTION_DONE
    }


}