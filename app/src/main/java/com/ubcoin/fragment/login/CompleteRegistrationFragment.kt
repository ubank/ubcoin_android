package com.ubcoin.fragment.login

import android.os.Bundle
import android.text.Editable
import android.view.View
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.ThePreferences
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.profile.ProfileCompleteResponse
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.utils.TextWatcherAdatepr
import kotlinx.android.synthetic.main.fragment_complete_registration.*

/**
 * Created by Yuriy Aizenberg
 */

private const val BUNDLE_EMAIL = "Bundle_email"

class CompleteRegistrationFragment : BaseFragment() {

    private lateinit var email: String

    companion object {
        fun getBundle(email: String): Bundle {
            val args = Bundle()
            args.putString(BUNDLE_EMAIL, email)
            return args
        }
    }

    override fun getLayoutResId() = R.layout.fragment_complete_registration

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        email = arguments?.getString(BUNDLE_EMAIL) ?: ""
        val edtCode = view.findViewById<MaterialEditText>(R.id.edtCode)
        val llSend = view.findViewById<View>(R.id.llSend)
        val imgSend = view.findViewById<View>(R.id.imgSend)
        edtCode.setOnEditorActionListener(object : ImeDoneActionHandler() {
            override fun onActionCall() {
                if (validateInput(edtCode)) goNext()
            }
        })
        edtCode.addTextChangedListener(object : TextWatcherAdatepr() {
            override fun afterTextChanged(p0: Editable?) {
                super.afterTextChanged(p0)
                if (validateInput(edtCode)) {
                    llSend.setOnClickListener { goNext() }
                    imgSend.setBackgroundResource(R.drawable.rounded_green_filled_button)
                } else {
                    llSend.setOnClickListener(null)
                    imgSend.setBackgroundResource(R.drawable.rounded_green_filled_transparent_button)
                }
            }
        })
    }

    private fun validateInput(materialEditText: MaterialEditText) = !materialEditText.text.toString().isBlank()

    override fun getHeaderText() = R.string.confirmation


    override fun getHeaderIcon() = R.drawable.ic_close

    private fun goNext() {
        hideKeyboard()
        showProgressDialog("Confirmation", "Wait please")
        DataProvider.confirmRegistrationEmail(email,
                edtCode.text.toString().trim(),
                object :SilentConsumer<ProfileCompleteResponse> {
                    override fun onConsume(t: ProfileCompleteResponse) {
                        hideProgressDialog()
                        ThePreferences().setToken(t.accessToken)
                        ProfileHolder.user = t.user
                        getSwitcher()?.clearBackStack()?.addTo(EndRegistrationFragment::class.java)
                    }
                },
                object : SilentConsumer<Throwable> {
                    override fun onConsume(t: Throwable) {
                        handleException(t)
                    }
                })
    }

    override fun handleException(t: Throwable) {
        hideProgressDialog()
        super.handleException(t)
    }

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