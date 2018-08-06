package com.ubcoin.activity

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by Yuriy Aizenberg
 */
interface IActivity {

    @LayoutRes fun getResourceId() : Int

    @IdRes fun getFragmentContainerId() : Int

    fun getCurrentFragment() : Fragment?

    fun getHeader() : View?

    fun getFooter() : View?

    fun getTopGradient() : View?

    fun getTopImageView() : ImageView?

    fun getTopTextView() : TextView?

    fun getTopImageContainer() : View?

}