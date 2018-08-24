package com.ubcoin

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.multidex.MultiDexApplication
import android.support.v4.app.Fragment
import android.text.Html
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
import java.util.concurrent.CopyOnWriteArrayList


/**
 * Created by Yuriy Aizenberg
 */
private const val TAG = "TheApplication"

class TheApplication : MultiDexApplication() {

    private val locationsChangeCallbacks = CopyOnWriteArrayList<ILocationChangeCallback>()

    var favoriteIdForRemove: String? = null
    var currentLocation: LatLng? = null
        set(value) {
            field = value
            if (value != null) {
                EventBus.getDefault().post(value)
                locationsChangeCallbacks.forEach {
                    it.onLatLngChanged(value)
                }
            }
        }

    var isGoToTelegram = false

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

    fun registerLatLngCallback(iLocationChangeCallback: ILocationChangeCallback) {
        if (!locationsChangeCallbacks.contains(iLocationChangeCallback)) {
            locationsChangeCallbacks.add(iLocationChangeCallback)
        }
    }

    fun unregisterLatLngCallback(iLocationChangeCallback: ILocationChangeCallback) {
        locationsChangeCallbacks.remove(iLocationChangeCallback)
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

    fun openTelegramIntent(fullUrl: String, telegramLink: String, fragment: Fragment, requestCode: Int): Boolean {
        return if (!isTelegramAvailable()) {
            val telegramIntent = Intent(Intent.ACTION_VIEW)
            telegramIntent.data = Uri.parse(fullUrl)
            fragment.startActivityForResult(Intent.createChooser(telegramIntent, getString(R.string.open_with_outer_app_label)), requestCode)
            false
        } else {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Html.fromHtml(telegramLink).toString()))
            myIntent.setPackage(TELEGRAM_PACKAGE_NAME)
            fragment.startActivityForResult(Intent.createChooser(myIntent, getString(R.string.open_with_outer_app_label)), requestCode)
            true
        }
    }

    fun openExternalLink(activity: Activity, fullUrl: String) {
        val telegramIntent = Intent(Intent.ACTION_VIEW)
        telegramIntent.data = Uri.parse(fullUrl)
        telegramIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val chooser = Intent.createChooser(telegramIntent, getString(R.string.open_with_outer_app_label))
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(chooser)
    }

    fun openGeoMap(lat: Double, lon: Double, text: String) {
        val gmmIntentUri = Uri.parse("""geo:$lat,$lon?q=${Uri.encode(text)}""")
        val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    fun openShareIntent(url: String, activity: Activity) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, url)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val createChooser = Intent.createChooser(shareIntent, getString(R.string.share_link_chooser))
        createChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(createChooser)
    }


    companion object {
        lateinit var instance: TheApplication
            private set
        const val TELEGRAM_PACKAGE_NAME = "org.telegram.messenger"
    }

}