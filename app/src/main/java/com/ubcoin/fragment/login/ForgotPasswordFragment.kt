package com.ubcoin.fragment.login

import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.TextWatcherAdatepr

/**
 * Created by Yuriy Aizenberg
 */
class ForgotPasswordFragment : BaseFragment() {

    var edtForgotEmail : MaterialEditText? = null
    var imgForgotPasswordSend : ImageView?= null

    override fun getLayoutResId() = R.layout.fragment_forgot_password

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        edtForgotEmail = view.findViewById(R.id.edtForgotEmail)
        edtForgotEmail?.setOnEditorActionListener(object : ImeDoneActionHandler() {
            override fun onActionCall() {
                if (isEmailValid()) {
                    hideKeyboard()
                }
            }
        })
        imgForgotPasswordSend = view.findViewById(R.id.imgForgotSend)
        edtForgotEmail?.addTextChangedListener(getTextChangeListener())
    }

    private fun getTextChangeListener() : TextWatcher {
        return object : TextWatcherAdatepr() {
            override fun afterTextChanged(p0: Editable?) {
                super.afterTextChanged(p0)
                changeSendImage(isEmailValid())
            }
        }
    }

    private fun isEmailValid() = Patterns.EMAIL_ADDRESS.matcher(edtForgotEmail?.text.toString()).matches()

    private fun changeSendImage(isValid: Boolean) {
        imgForgotPasswordSend?.run {
            if (isValid) {
                setBackgroundResource(R.drawable.rounded_green_filled_button)
                setOnClickListener {sendEmail()}
            } else {
                setBackgroundResource(R.drawable.rounded_green_filled_transparent_button)
                setOnClickListener(null)
            }
        }
    }

    private fun sendEmail() {
        //todo
    }

    override fun showHeader() = true

    override fun getHeaderText() = R.string.forgot_password

    override fun getHeaderIcon()=  R.drawable.ic_back

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }
}