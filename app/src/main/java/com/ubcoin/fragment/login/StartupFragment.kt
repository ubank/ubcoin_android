package com.ubcoin.fragment.login

import android.view.View
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment

/**
 * Created by Yuriy Aizenberg
 */
class StartupFragment : BaseFragment() {

    override fun getLayoutResId() = R.layout.fragment_startup

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        view.findViewById<View>(R.id.llSignUp).setOnClickListener { startSignUp() }
        view.findViewById<View>(R.id.llAlreadyHaveAccount).setOnClickListener { startLogIn() }
    }

    private fun startSignUp() {
        getSwitcher()?.addTo(SignupFragment::class.java)
    }

    private fun startLogIn() {
        val switcher = getSwitcher()
        switcher?.addTo(LoginFragment::class.java)
    }

    override fun showHeader() = false
}