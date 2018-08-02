package com.ubcoin.fragment

import android.app.Activity
import android.support.v4.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.ubcoin.activity.BaseActivity
import com.ubcoin.activity.IActivity
import com.ubcoin.switcher.FragmentSwitcher

/**
 * Created by Yuriy Aizenberg
 */
abstract class BaseFragment : Fragment(), IFragmentBehaviorAware {

    val NO_HEADER_OBJECT = -1

    open fun showHeader(): Boolean = true

    open fun getHeaderText(): Int = NO_HEADER_OBJECT

    open fun getHeaderIcon(): Int = NO_HEADER_OBJECT

    override fun onResume() {
        super.onResume()
        hideKeyboard()
        changeActivityHeader(activity as IActivity)
    }

    fun getSwitcher(): FragmentSwitcher? {
        val activity = activity
        if (activity != null && activity is BaseActivity) return activity.fragmentSwitcher
        return null
    }

    private fun changeActivityHeader(iActivity: IActivity) {
        if (showHeader()) {
            iActivity.getHeader()?.visibility = View.VISIBLE
            changeActivityAttributes(iActivity)
        } else {
            iActivity.getHeader()?.visibility = View.GONE
        }
    }

    private fun changeActivityAttributes(iActivity: IActivity) {
        if (getHeaderText() != NO_HEADER_OBJECT) {
            iActivity.getTopTextView()?.text = getString(getHeaderText())
        }
        if (getHeaderIcon() != NO_HEADER_OBJECT) {
            iActivity.getTopImageView()?.run {
                setImageResource(getHeaderIcon())
                setOnClickListener { onIconClick() }
            }
        }
    }

    fun hideKeyboard() {
        val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    open fun onIconClick() {

    }


}