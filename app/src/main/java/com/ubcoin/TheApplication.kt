package com.ubcoin

import android.app.Application
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.ubcoin.network.NetworkModule

/**
 * Created by Yuriy Aizenberg
 */
class TheApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        instance = this
        val build = Picasso.Builder(this).downloader(OkHttp3Downloader(NetworkModule.client())).build()
        Picasso.setSingletonInstance(build)
    }

    companion object {
        lateinit var instance: TheApplication
            private set
    }

}