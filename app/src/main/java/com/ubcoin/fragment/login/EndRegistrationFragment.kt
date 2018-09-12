package com.ubcoin.fragment.login

import android.app.Activity
import android.content.Intent
import android.view.View
import com.ubcoin.R
import com.ubcoin.activity.MainActivity
import com.ubcoin.fragment.BaseFragment

/**
 * Created by Yuriy Aizenberg
 */

class EndRegistrationFragment : BaseFragment() {

    override fun getLayoutResId() = R.layout.fragment_end_registration

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        view.findViewById<View>(R.id.llClose).setOnClickListener { performBack() }
//        view.findViewById<TextView>(R.id.txtWeSentLatter).text = getString(R.string.we_sent_a_verification_letter, ProfileHolder.user?.email?:"")
    }

    override fun getHeaderText() = R.string.sign_up


    override fun getHeaderIcon() = R.drawable.ic_close

    override fun onBackPressed(): Boolean {
        performBack()
        return true
    }

    override fun onIconClick() {
        super.onIconClick()
        performBack()
    }

    private fun performBack() {
        activity?.run {
            setResult(Activity.RESULT_OK)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.KEY_REFRESH_AFTER_LOGIN, true)
            startActivity(intent)
            finish()
        }
    }
}