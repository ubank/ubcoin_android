package com.ubcoin.view.menu

/**
 * Created by Yuriy Aizenberg
 */
interface IMenuViewCallback {

    fun onMenuSelected(menuItems: MenuItems, menuSingleView: MenuSingleView, isAlreadyActivated : Boolean)

}