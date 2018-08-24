package com.ubcoin.fragment.login

import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.network.DataProvider
import com.ubcoin.network.HttpRequestException
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.ImeNextActionHandler
import com.ubcoin.utils.TextWatcherAdatepr
import com.ubcoin.view.PasswordInputExtension
import io.reactivex.functions.Consumer
import retrofit2.Response

/**
 * Created by Yuriy Aizenberg
 */
class SignupFragment : BaseFragment() {

    lateinit var edtSignUpName: MaterialEditText
    lateinit var edtSignUpEmail: MaterialEditText
    lateinit var edtPasswordInput: PasswordInputExtension

    lateinit var imgSignUp: Button

    override fun getLayoutResId() = R.layout.fragment_signup_copy

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        imgSignUp = view.findViewById(R.id.imgSignUp)

//        view.findViewById<View>(R.id.llSignUp).setOnClickListener { onSignUpClick() }
        view.findViewById<View>(R.id.txtAlreadyHaveAccount).setOnClickListener {
            getSwitcher()?.run {
                clearBackStack().addTo(StartupFragment::class.java, false).addTo(LoginFragment::class.java)
            }
        }

        edtSignUpName = view.findViewById(R.id.edtSignUpName)
        edtSignUpEmail = view.findViewById(R.id.edtSignUpEmail)
        edtPasswordInput = view.findViewById(R.id.edtPasswordInput)
        edtPasswordInput.edtPasswordInputExtension?.imeOptions = EditorInfo.IME_ACTION_DONE

        edtSignUpName.setOnEditorActionListener(object : ImeNextActionHandler() {
            override fun onActionCall() {
                edtSignUpName.clearFocus()
                edtSignUpEmail.requestFocus()
            }
        })

        edtSignUpEmail.setOnEditorActionListener(object : ImeNextActionHandler() {
            override fun onActionCall() {
                edtSignUpEmail.clearFocus()
                edtPasswordInput.edtPasswordInputExtension?.requestFocus()
            }
        })

        edtPasswordInput.edtPasswordInputExtension?.setOnEditorActionListener(object : ImeDoneActionHandler() {
            override fun onActionCall() {
                if (isInputValid()) {
                    hideKeyboard()
                    onSignUpClick()
                }
            }
        })

        view.findViewById<View>(R.id.llUserAgreement).setOnClickListener { showUserAgreement() }

        edtPasswordInput.edtPasswordInputExtension!!.addTextChangedListener(getTextChangeListener())
        edtSignUpEmail.addTextChangedListener(getTextChangeListener())
        edtSignUpName.addTextChangedListener(getTextChangeListener())

    }

    private fun getTextChangeListener(): TextWatcher {
        return object : TextWatcherAdatepr() {
            override fun afterTextChanged(p0: Editable?) {
                super.afterTextChanged(p0)
                if (isInputValid()) {
                    imgSignUp.setBackgroundResource(R.drawable.rounded_green_filled_button_smallr)
                    imgSignUp.setOnClickListener { onSignUpClick() }
                } else {
                    imgSignUp.setBackgroundResource(R.drawable.rounded_green_filled_transparent_button_smallr)
                    imgSignUp.setOnClickListener {}
                }
            }
        }
    }

    override fun onUnauthorized(httpRequestException: HttpRequestException): Boolean {
        Toast.makeText(activity, R.string.email_already_taken, Toast.LENGTH_SHORT).show()
        return true
    }

    override fun handleException(t: Throwable) {
        hideProgressDialog()
        super.handleException(t)
    }

    private fun onSignUpClick() {
        if (!isInputValid()) return
        val mail = getEmail().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            Toast.makeText(activity, R.string.email_invalid, Toast.LENGTH_SHORT).show()
            return
        }
        showProgressDialog(R.string.registration, R.string.wait_please_message)
        DataProvider.registrations(
                mail,
                getPassword().trim(),
                edtSignUpName.text.toString().trim(),
                onSuccess(),
                object : SilentConsumer<Throwable> {
                    override fun onConsume(t: Throwable) {
                        handleException(t)
                    }
                }
        )
    }

    private fun getEmail() = edtSignUpEmail.text.toString()

    private fun onSuccess(): Consumer<Response<Unit>> {
        return object : SilentConsumer<Response<Unit>> {
            override fun onConsume(t: Response<Unit>) {
                hideProgressDialog()
                getSwitcher()?.addTo(
                        CompleteRegistrationFragment::class.java,
                        CompleteRegistrationFragment.getBundle(getEmail(), getPassword(), getUserName()),
                        true)
            }

        }
    }

    private fun isInputValid() =
            getEmail().isNotBlank()
                    && Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches()
                    && getPassword().isNotBlank()
                    && getUserName().isNotBlank()

    private fun getUserName() = edtSignUpName.text.toString()

    private fun getPassword() = edtPasswordInput.getInputText()


    override fun getHeaderIcon() = R.drawable.ic_back

    override fun getHeaderText() = R.string.sign_up

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }


}