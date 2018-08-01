package com.ubcoin.fragment.login

import android.media.Image
import android.os.Bundle
import android.text.BoringLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment

/**
 * Created by Yuriy Aizenberg
 */
class ForgotPasswordFragment : BaseFragment() {

    var edtForgotEmail : MaterialEditText? = null
    var imgForgotPasswordSend : ImageView?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)
        edtForgotEmail = view.findViewById(R.id.edtForgotEmail)
        imgForgotPasswordSend = view.findViewById(R.id.imgForgotSend)
        edtForgotEmail?.addTextChangedListener(getTextChangeListener())
        return view
    }

    private fun getTextChangeListener() : TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                changeSendImage(Patterns.EMAIL_ADDRESS.matcher(edtForgotEmail?.text.toString()).matches())
            }
        }
    }

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

    override fun showHeader(): Boolean {
        return true
    }

    override fun getHeaderText(): Int {
        return R.string.forgot_password
    }

    override fun getHeaderIcon(): Int {
        return R.drawable.ic_back
    }

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }
}