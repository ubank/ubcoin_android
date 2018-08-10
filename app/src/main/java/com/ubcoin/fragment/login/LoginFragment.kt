package com.ubcoin.fragment.login

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.ThePreferences
import com.ubcoin.activity.MainActivity
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.profile.ProfileCompleteResponse
import com.ubcoin.network.DataProvider
import com.ubcoin.network.HttpRequestException
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.ImeNextActionHandler
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.utils.TextWatcherAdatepr
import io.reactivex.functions.Consumer
import java.net.HttpURLConnection

/**
 * Created by Yuriy Aizenberg
 */
class LoginFragment : BaseFragment() {

    var llForgotPassword: View? = null
    var txtLoginError: TextView? = null
    var edtLoginEmail: MaterialEditText? = null
    var edtLoginPassword: MaterialEditText? = null

    override fun getLayoutResId() = R.layout.fragment_login

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        view.findViewById<View>(R.id.llDontHaveAccount).setOnClickListener {
            getSwitcher()?.run {
                clearBackStack().addTo(StartupFragment::class.java, false).addTo(SignupFragment::class.java)
            }
        }

        val imgLogin = view.findViewById<ImageView>(R.id.imgLogin)

        view.findViewById<View>(R.id.llLogin).setOnClickListener {
            processLogin()
        }
        llForgotPassword = view.findViewById(R.id.llForgotPassword)
        txtLoginError = view.findViewById(R.id.txtLoginError)

        edtLoginEmail = view.findViewById(R.id.edtLoginEmail)
        edtLoginPassword = view.findViewById(R.id.edtLoginPassword)

        val edtLoginEmail = view.findViewById<MaterialEditText>(R.id.edtLoginEmail)
        val edtLoginPassword = view.findViewById<MaterialEditText>(R.id.edtLoginPassword)

        edtLoginEmail?.setOnEditorActionListener(object : ImeNextActionHandler() {
            override fun onActionCall() {
                edtLoginEmail.clearFocus()
                edtLoginPassword.requestFocus()
            }
        })

        edtLoginPassword?.setOnEditorActionListener(object : ImeDoneActionHandler() {
            override fun onActionCall() {
                hideKeyboard()
                processLogin()
            }
        })

        val textWatcherAdapter = object : TextWatcherAdatepr() {
            override fun afterTextChanged(p0: Editable?) {
                txtLoginError?.visibility = View.INVISIBLE
                imgLogin?.run {
                    if (isValidData()) {
                        setImageResource(R.drawable.rounded_green_filled_button)
                    } else {
                        setImageResource(R.drawable.rounded_green_filled_transparent_button)
                    }
                }
            }
        }
        edtLoginEmail?.addTextChangedListener(textWatcherAdapter)
        edtLoginPassword?.addTextChangedListener(textWatcherAdapter)

        showForgotPasswordView()

        view.findViewById<View>(R.id.llUserAgreement).setOnClickListener { showUserAgreement() }

    }

    private fun isValidData(): Boolean {
        return !edtLoginEmail?.text.toString().isBlank() && !edtLoginPassword?.text.toString().isBlank()
    }

    private fun showForgotPasswordView() {
        llForgotPassword?.run {
            visibility = View.VISIBLE
            setOnClickListener { showForgotPasswordFragment() }
        }
    }

    private fun toogleErrorView(error: String?) {
        txtLoginError?.run {
            if (error == null) {
                visibility = View.INVISIBLE
            } else {
                text = error
                visibility = View.VISIBLE
            }
        }
    }

    private fun processLogin() {
        activity?.run {
            if (isValidData()) {
                showProgressDialog("Login", "Login")
                DataProvider.login(edtLoginEmail?.text.toString().trim(), edtLoginPassword?.text.toString().trim(), successConsumer(), Consumer {
                    hideProgressDialog()
                    handleException(it)
                })
            }
        }
    }


    override fun onUnauthorized(httpRequestException: HttpRequestException): Boolean {
        txtLoginError?.visibility = View.VISIBLE
        return true
    }

    private fun successConsumer(): Consumer<ProfileCompleteResponse> {
        return Consumer {
            hideProgressDialog()
            ThePreferences().setToken(it.accessToken)
            ProfileHolder.profile = it.user
            activity?.run {
                setResult(Activity.RESULT_OK)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun showForgotPasswordFragment() {
        getSwitcher()?.addTo(ForgotPasswordFragment::class.java)

    }

    override fun getHeaderText() = R.string.log_in

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

}