package com.ubcoin.fragment.login

import android.content.Intent
import android.view.View
import android.widget.TextView
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.activity.MainActivity
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.ImeNextActionHandler

/**
 * Created by Yuriy Aizenberg
 */
class LoginFragment : BaseFragment() {

    var llForgotPassword: View? = null
    var txtLoginError: TextView? = null

    override fun getLayoutResId() = R.layout.fragment_login
    
    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        view.findViewById<View>(R.id.llDontHaveAccount).setOnClickListener {
            getSwitcher()?.run {
                clearBackStack().addTo(StartupFragment::class.java, false).addTo(SignupFragment::class.java)
            }
        }
        view.findViewById<View>(R.id.llLogin).setOnClickListener {
            processLogin()
        }
        llForgotPassword = view.findViewById(R.id.llForgotPassword)
        txtLoginError = view.findViewById(R.id.txtLoginError)

        val edtLoginEmail = view.findViewById<MaterialEditText>(R.id.edtLoginEmail)
        val edtLoginPassword = view.findViewById<MaterialEditText>(R.id.edtLoginPassword)

        edtLoginEmail?.setOnEditorActionListener(object : ImeNextActionHandler() {
            override fun onActionCall() {
                edtLoginEmail.clearFocus()
                edtLoginPassword.requestFocus()
            }
        })

        edtLoginPassword?.setOnEditorActionListener(object: ImeDoneActionHandler() {
            override fun onActionCall() {
                hideKeyboard()
                processLogin()
            }
        })

        showForgotPasswordView()

        view.findViewById<View>(R.id.llUserAgreement).setOnClickListener {  showUserAgreement() }

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
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun showForgotPasswordFragment() {
        getSwitcher()?.addTo(ForgotPasswordFragment::class.java)

    }

    override fun showHeader() = true

    override fun getHeaderText() = R.string.log_in

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

}