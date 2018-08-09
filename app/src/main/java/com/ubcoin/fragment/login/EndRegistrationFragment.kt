package com.ubcoin.fragment.login

import android.view.View
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment

/**
 * Created by Yuriy Aizenberg
 */

class EndRegistrationFragment : BaseFragment() {

    override fun getLayoutResId() = R.layout.fragment_end_registration

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        view.findViewById<View>(R.id.llClose).setOnClickListener { performBack() }
    }

    override fun getHeaderText() = R.string.sign_up


    override fun getHeaderIcon() = R.drawable.ic_close

    override fun onBackPressed(): Boolean {
        performBack()
        return true
    }

    override fun onIconClick() {
        super.onIconClick()
        performBack()
    }

    private fun performBack() {
        getSwitcher()?.clearBackStack()?.addTo(StartupFragment::class.java)
    }
}