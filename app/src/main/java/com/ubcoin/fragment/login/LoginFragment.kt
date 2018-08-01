package com.ubcoin.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment

/**
 * Created by Yuriy Aizenberg
 */
class LoginFragment : BaseFragment() {

    var llForgotPassword: View? = null
    var txtLoginError: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_login, container, false)
        inflate.findViewById<View>(R.id.llDontHaveAccount).setOnClickListener { activity?.onBackPressed() }
        llForgotPassword = inflate.findViewById(R.id.llForgotPassword)
        txtLoginError = inflate.findViewById(R.id.txtLoginError)
        showForgotPasswordView()
        return inflate
    }

    private fun showForgotPasswordView() {
        llForgotPassword?.run {
            visibility = View.VISIBLE
            setOnClickListener { showForgotPasswordFragment() }
        }
    }

    private fun toogleErrorView(error: String?) {
        txtLoginError?.run {
            if (error == null) {
                visibility = View.INVISIBLE
            } else {
                text = error
                visibility = View.VISIBLE
            }
        }
    }

    private fun showForgotPasswordFragment() {
        getSwitcher()?.addTo(ForgotPasswordFragment::class.java)

    }

    override fun showHeader(): Boolean {
        return true
    }

    override fun getHeaderText(): Int {
        return R.string.log_in
    }

    override fun getHeaderIcon(): Int {
        return R.drawable.ic_back
    }

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

}