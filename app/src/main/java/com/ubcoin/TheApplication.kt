package com.ubcoin

import android.app.Application

/**
 * Created by Yuriy Aizenberg
 */
class TheApplication : Application() {



    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: TheApplication
            private set
    }

}