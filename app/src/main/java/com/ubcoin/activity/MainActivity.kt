package com.ubcoin.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.NotImplementedYetFragment
import com.ubcoin.fragment.favorite.FavoriteListFragment
import com.ubcoin.fragment.market.MarketListFragment
import com.ubcoin.view.menu.IMenuViewCallback
import com.ubcoin.view.menu.MenuItems
import com.ubcoin.view.menu.MenuSingleView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_header.*

/**
 * Created by Yuriy Aizenberg
 */
class MainActivity : BaseActivity() {
    override fun getTopGradient(): View? = llMainGradient

    override fun getResourceId(): Int = R.layout.activity_main

    override fun getFragmentContainerId(): Int = R.id.mainContainer

    override fun getHeader() = mainHeader

    override fun getFooter() = menuBottomView

    override fun getTopImageView(): ImageView = imgHeaderLeft

    override fun getTopTextView(): TextView = txtHeader

    override fun getTopImageContainer(): View = llHeaderLeft

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentSwitcher?.clearBackStack()?.addTo(MarketListFragment::class.java)

        menuBottomView?.menuViewCallback = object : IMenuViewCallback {
            override fun onMenuSelected(menuItems: MenuItems, menuSingleView: MenuSingleView, isAlreadyActivated: Boolean) {
                if (!isAlreadyActivated) {
                    menuBottomView.activate(menuItems)
                    when (menuItems) {
                        MenuItems.MARKET -> {
                            fragmentSwitcher?.clearBackStack()?.addTo(MarketListFragment::class.java)
                        }
                        MenuItems.FAVOURITE -> {
                            fragmentSwitcher?.clearBackStack()?.addTo(FavoriteListFragment::class.java)
                        }
                        MenuItems.SELL -> {
                            goStub()
                        }
                        MenuItems.DEALS -> {
                            goStub()
                        }
                        MenuItems.PROFILE -> {
                            goStub()
                        }
                        MenuItems.SIGN_IN -> {
                            goStub()
                        }
                    }
                }
            }
        }
        menuBottomView.activateSignIn(false)
        menuBottomView.activate(MenuItems.MARKET)
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