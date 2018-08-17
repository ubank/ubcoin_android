package com.ubcoin.fragment.login

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.ThePreferences
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.profile.ProfileCompleteResponse
import com.ubcoin.network.DataProvider
import com.ubcoin.network.HttpRequestException
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.utils.TextWatcherAdatepr
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_complete_registration.*
import retrofit2.Response

/**
 * Created by Yuriy Aizenberg
 */

private const val BUNDLE_EMAIL = "Bundle_email"
private const val BUNDLE_NAME = "Bundle_name"
private const val BUNDLE_PASSWORD = "Bundle_password"

class CompleteRegistrationFragment : BaseFragment() {

    private lateinit var email: String
    private lateinit var userName: String
    private lateinit var password: String
    private lateinit var llResendCode : View

    companion object {
        fun getBundle(email: String, userName: String, password: String): Bundle {
            val args = Bundle()
            args.putString(BUNDLE_EMAIL, email)
            args.putString(BUNDLE_NAME, userName)
            args.putString(BUNDLE_PASSWORD, password)

            return args
        }
    }

    override fun getLayoutResId() = R.layout.fragment_complete_registration

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        email = arguments?.getString(BUNDLE_EMAIL) ?: ""
        userName = arguments?.getString(BUNDLE_NAME) ?: ""
        password = arguments?.getString(BUNDLE_PASSWORD) ?: ""
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
        view.findViewById<TextView>(R.id.txtWeSentLatter).text = getString(R.string.we_sent_a_verification_letter, email)
        llResendCode = view.findViewById(R.id.llResendCode)

        llResendCode.setOnClickListener { resendCode() }
    }

    private fun resendCode() {
        showProgressDialog("Wait please", "")
        DataProvider.registrations(email, password, userName, object : SilentConsumer<Response<Unit>> {
            override fun onConsume(t: Response<Unit>) {
                hideProgressDialog()
                activity?.run {
                    Toast.makeText(activity, "An email has been successfully sent", Toast.LENGTH_SHORT).show()
                }
            }
        }, Consumer { handleException(it) })
    }


    override fun onUnauthorized(httpRequestException: HttpRequestException): Boolean {
        hideProgressDialog()
        //todo
        showSweetAlertDialog("Error", "Confirmation code invalid")
        return true
    }

    private fun validateInput(materialEditText: MaterialEditText) = !materialEditText.text.toString().isBlank()

    override fun getHeaderText() = R.string.confirmation


    override fun getHeaderIcon() = R.drawable.ic_back

    private fun goNext() {
        hideKeyboard()
        //todo
        showProgressDialog("Confirmation", "Wait please")
        DataProvider.confirmRegistrationEmail(email,
                edtCode.text.toString().trim(),
                object :SilentConsumer<ProfileCompleteResponse> {
                    override fun onConsume(t: ProfileCompleteResponse) {
                        hideProgressDialog()
                        ThePreferences().setToken(t.accessToken)
                        ThePreferences().setCurrentUser(t.user)
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

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

}