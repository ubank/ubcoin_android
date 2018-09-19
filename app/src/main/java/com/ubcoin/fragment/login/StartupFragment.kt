package com.ubcoin.fragment.login

import android.view.View
import com.ubcoin.R
import com.ubcoin.activity.MainActivity
import com.ubcoin.fragment.FirstLineFragment

/**
 * Created by Yuriy Aizenberg
 */
class StartupFragment : FirstLineFragment() {

    override fun getLayoutResId() = R.layout.fragment_startup

    override fun isFooterShow() = true

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        view.findViewById<View>(R.id.llSignUp).setOnClickListener { startSignUp() }
        view.findViewById<View>(R.id.llAlreadyHaveAccount).setOnClickListener { startLogIn() }
    }

    private fun startSignUp() {
        if (activity != null && activity is MainActivity) {
            (activity as MainActivity).startSignIn(true)
        }
    }

    private fun startLogIn() {
        if (activity != null && activity is MainActivity) {
            (activity as MainActivity).startSignIn(false)
        }
    }
}