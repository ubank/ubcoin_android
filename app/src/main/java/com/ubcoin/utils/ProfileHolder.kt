package com.ubcoin.utils

import com.ubcoin.ThePreferences
import com.ubcoin.model.event.UserEventWrapper
import com.ubcoin.model.response.MyBalance
import com.ubcoin.model.response.User
import org.greenrobot.eventbus.EventBus

/**
 * Created by Yuriy Aizenberg
 */
object ProfileHolder {

    var user: User? = ThePreferences().getCurrentUser()
    set(value) {
        field = value
        EventBus.getDefault().postSticky(UserEventWrapper(field))
    }

    var balance: MyBalance? = null

    fun isAuthorized() = user != null


}