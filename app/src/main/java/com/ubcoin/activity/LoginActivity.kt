package com.ubcoin.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.fragment.login.StartupFragment
import kotlinx.android.synthetic.main.common_header.*

class LoginActivity : BaseActivity() {

    override fun getFragmentContainerId() = R.id.fragment_container

    override fun getHeader(): View {
        return findViewById(R.id.loginHeader)
    }

    override fun getFooter() = null

    override fun getTopGradient(): View? = null

    override fun getTopImageView(): ImageView = imgHeaderLeft

    override fun getTopTextView(): TextView = txtHeader

    override fun getResourceId() = R.layout.login_activity

    override fun getTopImageContainer(): View = llHeaderLeft

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentSwitcher?.replaceTo(StartupFragment::class.java)
    }

}
