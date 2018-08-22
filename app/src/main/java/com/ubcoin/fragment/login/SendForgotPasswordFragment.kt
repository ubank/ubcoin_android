package com.ubcoin.fragment.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.ThePreferences
import com.ubcoin.activity.MainActivity
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.profile.ProfileCompleteResponse
import com.ubcoin.network.DataProvider
import com.ubcoin.network.HttpRequestException
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.ImeNextActionHandler
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.utils.TextWatcherAdatepr
import com.ubcoin.view.PasswordInputExtension
import io.reactivex.functions.Consumer
import retrofit2.Response

/**
 * Created by Yuriy Aizenberg
 */
class SendForgotPasswordFragment : BaseFragment() {

    private lateinit var edtCode: MaterialEditText
    private lateinit var llResendCode : View
    private lateinit var edtPasswordInput : PasswordInputExtension
    private lateinit var imgForgotConfirm: Button
    var email: String = ""

    companion object {
        fun createBundle(email: String):  Bundle {
            val args = Bundle()
            args.putString("email", email)
            return args
        }
    }

    override fun getLayoutResId() = R.layout.fragment_forgot_change_password

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        email = arguments!!.getString("email")
        edtCode = view.findViewById(R.id.edtCode)
        llResendCode = view.findViewById(R.id.llResendCode)
        edtPasswordInput = view.findViewById(R.id.edtPasswordInput)

        edtCode.setOnEditorActionListener(object : ImeNextActionHandler() {
            override fun onActionCall() {
                edtCode.clearFocus()
                edtPasswordInput.edtPasswordInputExtension?.requestFocus()
            }
        })

        edtPasswordInput.setImeOptionListener(object : ImeDoneActionHandler() {
            override fun onActionCall() {
                if (isDataInputValid()) {
                    hideKeyboard()
                    sendEmail()
                }
            }
        })
        edtPasswordInput.edtPasswordInputExtension?.addTextChangedListener(getTextChangeListener())
        imgForgotConfirm = view.findViewById(R.id.imgForgotConfirm)
        edtCode.addTextChangedListener(getTextChangeListener())

        llResendCode.setOnClickListener { resendCode() }
    }

    private fun resendCode() {
        showProgressDialog("Wait please", "")
        DataProvider.sendForgotEmail(email, object : SilentConsumer<Response<Unit>> {
            override fun onConsume(t: Response<Unit>) {
                hideProgressDialog()
                activity?.run {
                    Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show()
                }
            }
        }, Consumer { handleException(it) })
    }

    private fun getTextChangeListener(): TextWatcher {
        return object : TextWatcherAdatepr() {
            override fun afterTextChanged(p0: Editable?) {
                super.afterTextChanged(p0)
                changeSendImage(isDataInputValid())
            }
        }
    }

    private fun isDataInputValid() : Boolean {
        return edtCode.text!!.isNotBlank() && edtPasswordInput.getInputText().isNotBlank()
    }

    private fun changeSendImage(isValid: Boolean) {
        imgForgotConfirm.run {
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
        DataProvider.forgotChangePassword(email, edtCode.text!!.toString(), edtPasswordInput.getInputText(), successConsumer(), Consumer { handleException(it) })
    }

    private fun successConsumer(): SilentConsumer<ProfileCompleteResponse> {
        return object : SilentConsumer<ProfileCompleteResponse> {
            override fun onConsume(t: ProfileCompleteResponse) {
                hideProgressDialog()
                ProfileHolder.user = t.user
                ThePreferences().setToken(t.accessToken)
                activity?.run {
                    setResult(Activity.RESULT_OK)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

        }
    }

    override fun onUnauthorized(httpRequestException: HttpRequestException): Boolean {
        showSweetAlertDialog("Error", "Invalid code")
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