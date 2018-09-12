package com.ubcoin.utils

import android.content.Context
import android.net.ConnectivityManager
import com.ubcoin.TheApplication


/**
 * Created by Yuriy Aizenberg
 */
object NetworkConnectivityAwareManager {

    private val application: TheApplication by lazy {
        TheApplication.instance
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

}