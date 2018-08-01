package com.ubcoin

import android.content.SharedPreferences

/**
 * Created by Yuriy Aizenberg
 */
class ThePreferences {

    private val KEY_TOKEN : String = "TOKEN"
    private val KEY_COOKIE : String = "COOKIE"

    init {
        val theApplication = TheApplication.instance
        instance = theApplication.getSharedPreferences(theApplication.packageName, 0)
    }

    companion object {
        lateinit var instance: SharedPreferences
            private set
    }

    fun getToken(): String? = Companion.instance.getString(KEY_TOKEN, null)

    fun setToken(token: String?) {
        instance.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getCookie(): String? = Companion.instance.getString(KEY_COOKIE, null)

    fun setCookie(token: String?) {
        instance.edit().putString(KEY_COOKIE, token).apply()
    }

}
