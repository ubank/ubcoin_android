package com.ubcoin.activity

import android.content.Intent
import android.os.Bundle
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.NotImplementedYetFragment
import com.ubcoin.fragment.deals.DealsParentFragment
import com.ubcoin.fragment.favorite.FavoriteListFragment
import com.ubcoin.fragment.market.MarketListFragment
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.view.menu.IMenuViewCallback
import com.ubcoin.view.menu.MenuItems
import com.ubcoin.view.menu.MenuSingleView
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Yuriy Aizenberg
 */
class MainActivity : BaseActivity() {

    companion object {
        val REQUEST_CODE = 10001
    }

    override fun getResourceId(): Int = R.layout.activity_main

    override fun getFragmentContainerId(): Int = R.id.mainContainer

    override fun getFooter() = menuBottomView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    }

    private fun checkProfileLoggedIn() {
        if (ProfileHolder.profile == null) {
            menuBottomView.activateSignIn(false)
        } else {
            menuBottomView.activateProfile(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            checkProfileLoggedIn()
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
}