package com.ubcoin.activity

import android.support.v4.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by Yuriy Aizenberg
 */
interface IActivity {

    fun getResourceId() : Int

    fun getFragmentContainerId() : Int

    fun getCurrentFragment() : Fragment

    fun getHeader() : View?

    fun getTopImageView() : ImageView?

    fun getTopTextView() : TextView?

    fun getTopImageContainer() : View?

}