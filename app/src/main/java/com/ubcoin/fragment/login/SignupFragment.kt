package com.ubcoin.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment

/**
 * Created by Yuriy Aizenberg
 */
class SignupFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        view.findViewById<View>(R.id.llSignUp).setOnClickListener { onSignUpClick() }
        return view
    }

    private fun onSignUpClick() {
        getSwitcher()?.clearBackStack()?.addTo(EndRegistrationFragment::class.java)
    }

    override fun showHeader(): Boolean = true

    override fun getHeaderIcon(): Int {
        return R.drawable.ic_back
    }

    override fun getHeaderText(): Int {
        return R.string.sign_up
    }

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }



}