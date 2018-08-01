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
class StartupFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_startup, container, false)
        view.findViewById<View>(R.id.llSignUp).setOnClickListener { startSignUp() }
        view.findViewById<View>(R.id.llAlreadyHaveAccount).setOnClickListener { startLogIn() }
        return view
    }

    private fun startSignUp() {
        getSwitcher()?.addTo(SignupFragment::class.java)
    }

    private fun startLogIn() {
        val switcher = getSwitcher()
        switcher?.addTo(LoginFragment::class.java)
    }

    override fun showHeader(): Boolean {
        return false
    }

    /*override fun getHeaderIcon(): Int {
        return R.drawable.ic_back
    }

    override fun getHeaderText(): Int {
        return R.string.header_startup_fragment
    }

    override fun onBackPressed(): Boolean {
        Toast.makeText(activity, "Back", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onIconClick() {
        super.onIconClick()
        Toast.makeText(activity, "Exit!", Toast.LENGTH_SHORT).show()
        activity?.onBackPressed()
    }*/


}