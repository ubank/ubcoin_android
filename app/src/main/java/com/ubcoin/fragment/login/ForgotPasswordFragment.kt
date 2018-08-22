package com.ubcoin.fragment.login

import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.network.DataProvider
import com.ubcoin.network.HttpRequestException
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.TextWatcherAdatepr
import io.reactivex.functions.Consumer
import retrofit2.Response

/**
 * Created by Yuriy Aizenberg
 */
class ForgotPasswordFragment : BaseFragment() {

    private lateinit var edtForgotEmail: MaterialEditText
    private lateinit var imgForgotPasswordSend: Button

    override fun getLayoutResId() = R.layout.fragment_forgot_password

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        edtForgotEmail = view.findViewById(R.id.edtForgotEmail)
        edtForgotEmail.setOnEditorActionListener(object : ImeDoneActionHandler() {
            override fun onActionCall() {
                if (isEmailValid()) {
                    hideKeyboard()
                    sendEmail()
                }
            }
        })
        imgForgotPasswordSend = view.findViewById(R.id.imgForgotSend)
        edtForgotEmail.addTextChangedListener(getTextChangeListener())
    }

    private fun getTextChangeListener(): TextWatcher {
        return object : TextWatcherAdatepr() {
            override fun afterTextChanged(p0: Editable?) {
                super.afterTextChanged(p0)
                changeSendImage(isEmailValid())
            }
        }
    }

    private fun isEmailValid() = Patterns.EMAIL_ADDRESS.matcher(edtForgotEmail.text.toString()).matches()

    private fun changeSendImage(isValid: Boolean) {
        imgForgotPasswordSend.run {
            if (isValid) {
                setBackgroundResource(R.drawable.rounded_green_filled_button_smallr)
                setOnClickListener { sendEmail() }
            } else {
                setBackgroundResource(R.drawable.rounded_green_filled_transparent_button_smallr)
                setOnClickListener(null)
            }
        }
    }

    private fun sendEmail() {
        showProgressDialog("Wait please", "")
        DataProvider.sendForgotEmail(edtForgotEmail.text!!.toString(), object : SilentConsumer<Response<Unit>> {
            override fun onConsume(t: Response<Unit>) {
                hideProgressDialog()
                getSwitcher()?.addTo(SendForgotPasswordFragment::class.java, SendForgotPasswordFragment.createBundle(edtForgotEmail.text!!.toString()), true)
            }
        }, Consumer {
            handleException(it)
        })
    }

    override fun onUnauthorized(httpRequestException: HttpRequestException): Boolean {
        showSweetAlertDialog("Error", "Email not found")
        return true
    }

    override fun handleException(t: Throwable) {
        hideProgressDialog()
        super.handleException(t)
    }

    override fun getHeaderText() = R.string.forgot_password

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }
}