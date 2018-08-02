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

class EndRegistrationFragment : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_end_registration, container, false)
        view.findViewById<View>(R.id.llClose).setOnClickListener { performBack() }
        return view
    }

    override fun showHeader(): Boolean = true

    override fun getHeaderText(): Int = R.string.sign_up


    override fun getHeaderIcon(): Int = R.drawable.ic_close

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