package com.ubcoin.switcher

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.ubcoin.R
import com.ubcoin.activity.BaseActivity

/**
 * Created by Yuriy Aizenberg
 */
class FragmentSwitcher(private val appCompatActivity: BaseActivity) {

    fun clearBackStack(): FragmentSwitcher {
        val supportFragmentManager = appCompatActivity.supportFragmentManager
        for (item in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        return this
    }

    fun addTo(fragmentClass: Class<out Fragment>, addToBackStack: Boolean, bundle: Bundle?, isReplace: Boolean, withAnimation: Boolean): FragmentSwitcher {
        val supportFragmentManager = appCompatActivity.supportFragmentManager
        val transaction = supportFragmentManager.beginTransaction()
        /*if (withAnimation) {
        todo
            transaction.setCustomAnimations(R.anim.fade_in_animation, R.anim.fade_out_animation, R.anim.fade_in_animation, R.anim.fade_out_animation)
        }*/
        val fragment = Fragment.instantiate(appCompatActivity, fragmentClass.name)
        fragment.arguments = bundle
        if (!isReplace) {
            transaction.add(appCompatActivity.getFragmentContainerId(), fragment, fragmentClass.name)
        } else {
            transaction.replace(appCompatActivity.getFragmentContainerId(), fragment, fragmentClass.name)
        }
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.commit()
        return this
    }

    fun addTo(fragmentClass: Class<out Fragment>, addToBackStack: Boolean, withAnimation: Boolean): FragmentSwitcher {
        return addTo(fragmentClass, addToBackStack, null, false, withAnimation)
    }

    fun addTo(fragmentClass: Class<out Fragment>, bundle: Bundle?, withAnimation: Boolean): FragmentSwitcher {
        return addTo(fragmentClass, true, bundle, false, withAnimation)
    }

    fun addTo(fragmentClass: Class<out Fragment>, withAnimation: Boolean): FragmentSwitcher {
        return addTo(fragmentClass, true, null, false, withAnimation)
    }

    fun addTo(fragmentClass: Class<out Fragment>): FragmentSwitcher {
        return addTo(fragmentClass, true, null, false, true)
    }


    fun replaceTo(fragmentClass: Class<out Fragment>, addToBackStack: Boolean, withAnimation: Boolean): FragmentSwitcher {
        return addTo(fragmentClass, addToBackStack, null, true, withAnimation)
    }

    fun replaceTo(fragmentClass: Class<out Fragment>, bundle: Bundle?, withAnimation: Boolean): FragmentSwitcher {
        return addTo(fragmentClass, true, bundle, true, withAnimation)
    }

    fun replaceTo(fragmentClass: Class<out Fragment>, withAnimation: Boolean): FragmentSwitcher {
        return addTo(fragmentClass, true, null, true, withAnimation)
    }

    fun replaceTo(fragmentClass: Class<out Fragment>): FragmentSwitcher {
        return addTo(fragmentClass, true, null, true, true)
    }

}