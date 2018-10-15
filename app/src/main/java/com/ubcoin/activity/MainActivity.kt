package com.ubcoin.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.Crashlytics
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.NotImplementedYetFragment
import com.ubcoin.fragment.deals.DealsParentFragment
import com.ubcoin.fragment.favorite.FavoriteListFragment
import com.ubcoin.fragment.login.StartupFragment
import com.ubcoin.fragment.market.MarketDetailsFragment
import com.ubcoin.fragment.market.MarketListFragment
import com.ubcoin.fragment.profile.ProfileMainFragment
import com.ubcoin.fragment.sell.ActionsDialogManager
import com.ubcoin.fragment.sell.SellFragment
import com.ubcoin.model.event.UserEventWrapper
import com.ubcoin.model.response.MarketItemStatus
import com.ubcoin.network.DataProvider
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.utils.gone
import com.ubcoin.utils.visible
import com.ubcoin.view.menu.IMenuViewCallback
import com.ubcoin.view.menu.MenuItems
import com.ubcoin.view.menu.MenuSingleView
import io.fabric.sdk.android.services.common.Crash
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception
import java.util.concurrent.TimeUnit


/**
 * Created by Yuriy Aizenberg
 */

class MainActivity : BaseActivity() {

    companion object {
        const val REQUEST_FINE_LOCATION = 10002
        const val REQUEST_CODE = 10001
        const val KEY_REFRESH_AFTER_LOGIN = "KRAF"
    }


    private var mLocationRequest: LocationRequest? = null

    override fun getResourceId(): Int = R.layout.activity_main

    override fun getFragmentContainerId(): Int = R.id.mainContainer

    override fun getFooter() = menuBottomView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        fragmentSwitcher?.clearBackStack()?.addTo(MarketListFragment::class.java)
//        menuBottomView.setIgnored(MenuItems.SIGN_IN) //Now signin can be selected
        menuBottomView.menuViewCallback = object : IMenuViewCallback {
            override fun onMenuSelected(menuItems: MenuItems, menuSingleView: MenuSingleView, isAlreadyActivated: Boolean) {
                if (!isAlreadyActivated) {
                    when (menuItems) {
                        MenuItems.MARKET -> {
                            menuBottomView.activate(menuItems)
                            fragmentSwitcher?.clearBackStack()?.addTo(MarketListFragment::class.java)
                        }
                        MenuItems.FAVORITE -> {
                            menuBottomView.activate(menuItems)
                            fragmentSwitcher?.clearBackStack()?.addTo(FavoriteListFragment::class.java)
                        }
                        MenuItems.SELL -> {
                            if (!ProfileHolder.isAuthorized()) {
                                startSignIn()
                                return
                            }
                            menuBottomView.activate(menuItems)
                            fragmentSwitcher?.clearBackStack()?.addTo(SellFragment::class.java)
                        }
                        MenuItems.DEALS -> {
                            menuBottomView.activate(menuItems)
                            fragmentSwitcher?.clearBackStack()?.addTo(DealsParentFragment::class.java)
                        }
                        MenuItems.PROFILE -> {
                            if (!ProfileHolder.isAuthorized()) {
                                checkProfileLoggedIn()
                            } else {
                                menuBottomView.activate(menuItems)
                                fragmentSwitcher?.clearBackStack()?.addTo(ProfileMainFragment::class.java)
                            }
                        }
                        MenuItems.SIGN_IN -> {
                           startSignIn()
                        }
                    }
                }
            }
        }
        checkProfileLoggedIn()
        menuBottomView.activate(MenuItems.MARKET)
        if (checkPermissions(true)) {
            startLocationUpdates()
            getLastLocation()
        }
        tryParseIntent(intent)
    }

    private fun checkPermissions(requestPermissions: Boolean): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            if (requestPermissions) {
                requestPermissions()
            }
            false
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.getBooleanExtra(KEY_REFRESH_AFTER_LOGIN, false) == true) {
            fragmentSwitcher?.clearBackStack()?.addTo(MarketListFragment::class.java)
            menuBottomView.activate(MenuItems.MARKET)
        } else {
            tryParseIntent(intent)
        }
    }

    private fun tryParseIntent(intent: Intent?) {
        try {
            val split = intent?.data?.schemeSpecificPart?.split("id=")
            split?.let { list ->
                if (list.size == 2) {
                    val id = list.get(1)
                    val view = findViewById<View>(R.id.progressCenter)
                    view.visible()
                    DataProvider.getMarketItemById(id, Consumer {
                        view.gone()
                        fragmentSwitcher?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(it), false)
                    }, Consumer {
                        view.gone()
                        Crashlytics.logException(it)
                    })
                }
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
    }

    private fun showNeedToRegistration() {
        MaterialDialog.Builder(this)
                .title(getString(R.string.error))
                .content(R.string.need_to_logged_in)
                .build()
                .show()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun checkProfileLoggedIn() {
        if (!ProfileHolder.isAuthorized()) {
            menuBottomView.activateSignIn(false)
        } else {
            menuBottomView.activateProfile(false)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (checkPermissions(false)) {
                startLocationUpdates()
                getLastLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            checkProfileLoggedIn()
            val fragment = supportFragmentManager.findFragmentById(getFragmentContainerId())
            if (fragment is StartupFragment) {
                onBackPressed()
            }
        }
    }

    private fun startLocationUpdates() {

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        mLocationRequest = LocationRequest()
        mLocationRequest?.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = TimeUnit.SECONDS.toMillis(5)
            fastestInterval = TimeUnit.SECONDS.toMillis(5)

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(this)

            val locationSettingsRequest = builder.build()
            val settingsClient = LocationServices.getSettingsClient(applicationContext)
            settingsClient.checkLocationSettings(locationSettingsRequest)

            getFusedLocationProviderClient(this@MainActivity).requestLocationUpdates(mLocationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    if (locationResult != null) {
                        TheApplication.instance.currentLocation = LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
                        getFusedLocationProviderClient(this@MainActivity).removeLocationUpdates(this)
                    }
                }
            }, Looper.myLooper())
        }

    }

    fun getLastLocation() {

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        val locationClient = getFusedLocationProviderClient(this)

        locationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        TheApplication.instance.currentLocation = LatLng(location.latitude, location.longitude)
                    }
                }
                .addOnFailureListener { e ->
                    Crashlytics.logException(e)
                }
    }


    private fun startSignIn() {
        menuBottomView.activateSignIn(true)
        fragmentSwitcher?.addTo(StartupFragment::class.java)
    }

    fun startSignIn(isSignUp: Boolean) {
        startActivityForResult(LoginActivity.getStartupIntent(this, isSignUp), REQUEST_CODE)
    }

    override fun onBackPressed() {
        val currentFragment = if (getCurrentFragment() != null) getCurrentFragment() as BaseFragment else null
        if (currentFragment != null && currentFragment !is MarketListFragment && currentFragment.isFirstLineFragment()) {
            fragmentSwitcher?.clearBackStack()?.addTo(MarketListFragment::class.java)
            menuBottomView?.activate(MenuItems.MARKET)
        } else {
            super.onBackPressed()
        }
    }

    private fun goStub() {
        fragmentSwitcher?.clearBackStack()?.addTo(NotImplementedYetFragment::class.java)
    }

    @Subscribe(sticky = true)
    fun subscribeOnUserEvent(userEventWrapper: UserEventWrapper) {
        checkProfileLoggedIn()
    }
}