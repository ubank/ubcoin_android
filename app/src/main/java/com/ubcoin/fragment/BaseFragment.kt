package com.ubcoin.fragment

import android.support.v4.app.Fragment
import android.view.View
import com.ubcoin.activity.BaseActivity
import com.ubcoin.activity.IActivity
import com.ubcoin.switcher.FragmentSwitcher

/**
 * Created by Yuriy Aizenberg
 */
abstract class BaseFragment : Fragment(), IFragmentBehaviorAware {

    val NO_HEADER_OBJECT = -1

    open fun showHeader(): Boolean = true

    open fun getHeaderText() : Int = NO_HEADER_OBJECT

    open fun getHeaderIcon() : Int = NO_HEADER_OBJECT

    override fun onResume() {
        super.onResume()
        changeActivityHeader(activity as IActivity)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }

    fun getSwitcher() : FragmentSwitcher? {
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
                setOnClickListener {onIconClick()}
            }
        }
    }

    open fun onIconClick() {

    }



}