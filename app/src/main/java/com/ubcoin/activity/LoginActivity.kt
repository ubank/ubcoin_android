package com.ubcoin.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ubcoin.R
import com.ubcoin.fragment.login.LoginFragment
import com.ubcoin.fragment.login.SignupFragment
import com.ubcoin.fragment.login.StartupFragment

class LoginActivity : BaseActivity() {

    companion object {

        private const val BUNDLE_KEY_IS_SIGNUP = "isSignUp"

        fun getStartupIntent(context : Context, isSignUp: Boolean) : Intent {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra(BUNDLE_KEY_IS_SIGNUP, isSignUp)
            return intent

        }
    }

    override fun getFragmentContainerId() = R.id.fragment_container

    override fun getFooter() = null

    override fun getResourceId() = R.layout.login_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val booleanExtra = intent.getBooleanExtra(BUNDLE_KEY_IS_SIGNUP, true)
        if (booleanExtra) {
            fragmentSwitcher?.addTo(SignupFragment::class.java)
        } else {
            fragmentSwitcher?.addTo(LoginFragment::class.java)
        }
    }

}
