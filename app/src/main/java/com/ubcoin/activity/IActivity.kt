package com.ubcoin.activity

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.View

/**
 * Created by Yuriy Aizenberg
 */
interface IActivity {

    @LayoutRes fun getResourceId() : Int

    @IdRes fun getFragmentContainerId() : Int

    fun getCurrentFragment() : Fragment?

    fun getFooter() : View?

    fun getContainer() : View

}