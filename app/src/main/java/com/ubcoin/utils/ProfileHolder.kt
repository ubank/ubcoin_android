package com.ubcoin.utils

import com.onesignal.OneSignal
import com.ubcoin.preferences.ThePreferences
import com.ubcoin.model.event.UserEventWrapper
import com.ubcoin.model.response.MyBalance
import com.ubcoin.model.response.User
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import org.greenrobot.eventbus.EventBus

/**
 * Created by Yuriy Aizenberg
 */
object ProfileHolder {

    private var user: User? = ThePreferences().getCurrentUser()

    fun updateUser() {
        user?.let {
            ThePreferences().setCurrentUser(it)
        }
    }

    private var balance: MyBalance? = null

    fun getUserId(): String?{
        return user?.id
    }

    fun getUserName(): String?{
        return user?.name
    }

    fun getUser(): User?{
        return user
    }

    fun logout(){
        user = null
        balance = null
        EventBus.getDefault().postSticky(UserEventWrapper(null))
    }

    fun setUser(user: User){
        this.user = user
        EventBus.getDefault().postSticky(UserEventWrapper(user))

        if(ProfileHolder.isAuthorized()) {
            if(ThePreferences().getOneSignalToken() == null) {
                OneSignal.idsAvailable { userId, registrationId ->
                    var map = HashMap<String, String>()
                    map.put("playerId", userId)
                    DataProvider.subscribePush(map, object : SilentConsumer<Any> {
                        override fun onConsume(t: Any) {
                            ThePreferences().setOneSignalToken(userId)
                        }

                    }, object : SilentConsumer<Throwable> {
                        override fun onConsume(t: Throwable) {
                        }
                    })
                }
            }
        }
    }

    fun setBalance(balance: MyBalance){
        this.balance = balance
    }

    fun isAuthorized() = user != null

    fun getUBCBalance(): Double{
        return balance?.effectiveAmount?:0.0
    }

    fun getUBCBalanceString(): String{
        return getUBCBalance().moneyFormat()
    }

    fun getETHBalance(): Double{
        return balance?.effectiveAmountETH?:0.0
    }

    fun getETHBalanceString(): String{
        return getETHBalance().moneyFormatETH()
    }


}