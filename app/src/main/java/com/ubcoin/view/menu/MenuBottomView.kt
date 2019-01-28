package com.ubcoin.view.menu

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.ubcoin.R
import kotlinx.android.synthetic.main.view_bottom_menu.view.*

/**
 * Created by Yuriy Aizenberg
 */
class MenuBottomView : LinearLayout {

    var menuViewCallback: IMenuViewCallback? = null
    var activeMenuItem: MenuSingleView? = null
    var isExpanded = true
    private val ignored = ArrayList<MenuItems>()

    constructor(context: Context?) : super(context) {
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    fun setIgnored(vararg menuItems: MenuItems) {
        ignored.clear()
        ignored.addAll(menuItems.asList())
    }

    private fun initialize() {
        inflate(context, R.layout.view_bottom_menu, this)
        val iSingleMenuViewCallback = object : ISingleMenuViewCallback {
            override fun onSingleItemClickCallback(menuSingleView: MenuSingleView, isAlreadyActive: Boolean) {
                fireCallback(resolveMenuItem(menuSingleView), menuSingleView, isAlreadyActive)
            }
        }
        menuItemMarket?.singleMenuCallback = iSingleMenuViewCallback
        menuItemFavorite?.singleMenuCallback = iSingleMenuViewCallback
        menuItemSell?.singleMenuCallback = iSingleMenuViewCallback
        menuItemDeals?.singleMenuCallback = iSingleMenuViewCallback
        menuItemMessages?.singleMenuCallback = iSingleMenuViewCallback
        menuItemProfile?.singleMenuCallback = iSingleMenuViewCallback
        menuItemSignIn.singleMenuCallback = iSingleMenuViewCallback
        menuItemDeals.hide()

    }

    fun activate(menuItem: MenuItems) {
        activate(menuItem, true)
    }

    fun activate(menuItem: MenuItems, applyIgnored: Boolean) {
        if (applyIgnored && ignored.contains(menuItem)) return
        val singleView: MenuSingleView = when (menuItem) {
            MenuItems.MARKET -> menuItemMarket
            MenuItems.FAVORITE -> menuItemFavorite
            MenuItems.SELL -> menuItemSell
            MenuItems.DEALS -> menuItemDeals
            MenuItems.PROFILE -> menuItemProfile
            MenuItems.SIGN_IN -> menuItemSignIn
            MenuItems.MESSAGES -> menuItemMessages
        }
        if (activeMenuItem == null || !activeMenuItem?.equals(singleView)!!) {
            deactivateCurrent()
            singleView.activate()
//            singleView.shake()
            activeMenuItem = singleView
        }
    }

    private fun resolveMenuItem(menuSingleView: MenuSingleView): MenuItems {
        return when (menuSingleView) {
            menuItemMarket -> MenuItems.MARKET
            menuItemFavorite -> MenuItems.FAVORITE
            menuItemSell -> MenuItems.SELL
            menuItemDeals -> MenuItems.DEALS
            menuItemProfile -> MenuItems.PROFILE
            menuItemSignIn -> MenuItems.SIGN_IN
            menuItemMessages -> MenuItems.MESSAGES
            else -> throw RuntimeException("Unresolvable menu item")
        }
    }

    fun activateProfile(shouldBeSelected: Boolean) {
        if (shouldBeSelected) activate(MenuItems.PROFILE)

        menuItemProfile.show()
        menuItemSignIn.hide()
    }

    fun activateSignIn(shouldBeSelected: Boolean) {
        if (shouldBeSelected) activate(MenuItems.SIGN_IN)

        menuItemSignIn.show()
        menuItemProfile.hide()
    }

    private fun fireCallback(menuItem: MenuItems, menuSingleView: MenuSingleView, isAlreadyActive: Boolean) {
        menuViewCallback?.onMenuSelected(menuItem, menuSingleView, isAlreadyActive)
    }

    private fun deactivateCurrent() {
        activeMenuItem?.deactivate()
    }


}