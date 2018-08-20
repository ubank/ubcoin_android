package com.ubcoin

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.multidex.MultiDexApplication
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.ubcoin.model.response.User
import com.ubcoin.network.DataProvider
import com.ubcoin.network.NetworkModule
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.ProfileHolder
import io.fabric.sdk.android.Fabric
import org.greenrobot.eventbus.EventBus


/**
 * Created by Yuriy Aizenberg
 */
private const val TAG = "TheApplication"

class TheApplication : MultiDexApplication() {

    var favoriteIdForRemove: String? = null
    var currentLocation: LatLng? = null
        set(value) {
            field = value
            if (value != null) {
                EventBus.getDefault().post(value)
            }
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val build = Picasso.Builder(this).downloader(OkHttp3Downloader(NetworkModule.client())).build()
        Picasso.setSingletonInstance(build)
        installCrashlytics()
        val token = ThePreferences().getToken()
        if (token != null) {
            DataProvider.profile(
                    object : SilentConsumer<User> {
                        override fun onConsume(t: User) {
                            ProfileHolder.user = t
                        }
                    },
                    object : SilentConsumer<Throwable> {
                        override fun onConsume(t: Throwable) {
                            Log.e(TAG, "${t.message}", t)
                        }
                    })
        }
    }

    private fun installCrashlytics() {
        Fabric.with(this, Crashlytics())
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun isAppAvailable(appName: String): Boolean {
        return try {
            packageManager.getPackageInfo(appName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun isTelegramAvailable() = isAppAvailable(TELEGRAM_PACKAGE_NAME)

    fun openTelegramIntent(fullUrl: String) {
        val telegramIntent = Intent(Intent.ACTION_VIEW)
        telegramIntent.data = Uri.parse(fullUrl)
        startActivity(Intent.createChooser(telegramIntent, getString(R.string.open_with_outer_app_label)))
    }

    fun openGeoMap(lat: Double, lon: Double, text: String) {
        val gmmIntentUri = Uri.parse("""geo:$lat,$lon?q=${Uri.encode(text)}""")
        val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }


    companion object {
        lateinit var instance: TheApplication
            private set
        const val TELEGRAM_PACKAGE_NAME = "org.telegram.messenger"
    }

}