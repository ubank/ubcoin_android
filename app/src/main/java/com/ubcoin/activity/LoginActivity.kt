package com.ubcoin.activity

import android.os.Bundle
import com.ubcoin.R
import com.ubcoin.fragment.login.StartupFragment

class LoginActivity : BaseActivity() {

    override fun getFragmentContainerId() = R.id.fragment_container

    override fun getFooter() = null

    override fun getResourceId() = R.layout.login_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentSwitcher?.replaceTo(StartupFragment::class.java)
    }

}
