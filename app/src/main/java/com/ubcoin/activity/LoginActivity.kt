package com.ubcoin.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.fragment.login.StartupFragment

class LoginActivity : BaseActivity() {
    override fun getFragmentContainerId(): Int {
        return R.id.fragment_container
    }

    override fun getCurrentFragment(): Fragment {
        return supportFragmentManager.findFragmentById(getFragmentContainerId())!!
    }

    override fun getHeader(): View {
        return findViewById(R.id.loginHeader)
    }

    override fun getTopImageView(): ImageView {
        return findViewById(R.id.imgHeaderLeft) as ImageView
    }

    override fun getTopTextView(): TextView {
        return findViewById(R.id.txtHeader) as TextView
    }

    override fun getResourceId(): Int {
        return R.id.fragment_container
    }

    override fun getTopImageContainer(): View {
        return findViewById(R.id.llHeaderLeft) as View
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        fragmentSwitcher?.replaceTo(StartupFragment::class.java)
    }

}
