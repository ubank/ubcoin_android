package com.ubcoin.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
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
import com.ubcoin.fragment.market.MarketListFragment
import com.ubcoin.model.event.UserEventWrapper
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.view.menu.IMenuViewCallback
import com.ubcoin.view.menu.MenuItems
import com.ubcoin.view.menu.MenuSingleView
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
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
        menuBottomView.setIgnored(MenuItems.SIGN_IN)
        menuBottomView.menuViewCallback = object : IMenuViewCallback {
            override fun onMenuSelected(menuItems: MenuItems, menuSingleView: MenuSingleView, isAlreadyActivated: Boolean) {
                if (!isAlreadyActivated) {
                    menuBottomView.activate(menuItems)
                    when (menuItems) {
                        MenuItems.MARKET -> {
                            fragmentSwitcher?.clearBackStack()?.addTo(MarketListFragment::class.java)
                        }
                        MenuItems.FAVORITE -> {
                            if (!ProfileHolder.isAuthorized()) {
                                showNeedToRegistration()
                                return
                            }
                            fragmentSwitcher?.clearBackStack()?.addTo(FavoriteListFragment::class.java)
                        }
                        MenuItems.SELL -> {
                            goStub()
                        }
                        MenuItems.DEALS -> {
                            fragmentSwitcher?.clearBackStack()?.addTo(DealsParentFragment::class.java)
                        }
                        MenuItems.PROFILE -> {
                            goStub()
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
        }
    }

    private fun showNeedToRegistration() {
        MaterialDialog.Builder(this)
                .title("Error")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            checkProfileLoggedIn()
        } else if (requestCode == REQUEST_FINE_LOCATION) {
            if (checkPermissions(false)) {
                startLocationUpdates()
                getLastLocation()
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

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
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
        startActivityForResult(Intent(this, LoginActivity::class.java), REQUEST_CODE)
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