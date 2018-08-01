package com.ubcoin.switcher

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.ubcoin.activity.BaseActivity

/**
 * Created by Yuriy Aizenberg
 */
class FragmentSwitcher(private val appCompatActivity: BaseActivity) {

    fun clearBackStack() : FragmentSwitcher {
        val supportFragmentManager = appCompatActivity.supportFragmentManager
        for (item in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        return this
    }

    fun addTo(fragmentClass: Class<out Fragment>, addToBackStack: Boolean, bundle: Bundle?, isReplace: Boolean) : FragmentSwitcher {
        val supportFragmentManager = appCompatActivity.supportFragmentManager
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = Fragment.instantiate(appCompatActivity, fragmentClass.name)
        fragment.arguments = bundle
        if (!isReplace) {
            transaction.add(appCompatActivity.getResourceId(), fragment)
        } else {
            transaction.replace(appCompatActivity.getResourceId(), fragment)
        }
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
        return this
    }

    fun addTo(fragmentClass: Class<out Fragment>, addToBackStack: Boolean) : FragmentSwitcher {
        return addTo(fragmentClass, addToBackStack, null, false)
    }

    fun addTo(fragmentClass: Class<out Fragment>, bundle: Bundle?) : FragmentSwitcher {
        return addTo(fragmentClass, true, bundle, false)
    }

    fun addTo(fragmentClass: Class<out Fragment>) : FragmentSwitcher {
        return addTo(fragmentClass, true, null, false)
    }


    fun replaceTo(fragmentClass: Class<out Fragment>, addToBackStack: Boolean) : FragmentSwitcher {
        return addTo(fragmentClass, addToBackStack, null, true)
    }

    fun replaceTo(fragmentClass: Class<out Fragment>) : FragmentSwitcher {
        return addTo(fragmentClass, true, null, true)
    }

    fun replaceTo(fragmentClass: Class<out Fragment>, bundle: Bundle?) : FragmentSwitcher {
        return addTo(fragmentClass, true, bundle, true)
    }

}