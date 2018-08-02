package com.ubcoin.fragment.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.activity.MainActivity
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.ImeNextActionHandler
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * Created by Yuriy Aizenberg
 */
class LoginFragment : BaseFragment() {

    var llForgotPassword: View? = null
    var txtLoginError: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_login, container, false)
        inflate.findViewById<View>(R.id.llDontHaveAccount).setOnClickListener {
            getSwitcher()?.run {
                clearBackStack().addTo(StartupFragment::class.java).addTo(SignupFragment::class.java)
            }
        }
        inflate.findViewById<View>(R.id.llLogin).setOnClickListener {
            processLogin()
        }
        llForgotPassword = inflate.findViewById(R.id.llForgotPassword)
        txtLoginError = inflate.findViewById(R.id.txtLoginError)

        val edtLoginEmail = inflate.findViewById<MaterialEditText>(R.id.edtLoginEmail)
        val edtLoginPassword = inflate.findViewById<MaterialEditText>(R.id.edtLoginPassword)

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
        return inflate
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

    override fun showHeader(): Boolean {
        return true
    }

    override fun getHeaderText(): Int {
        return R.string.log_in
    }

    override fun getHeaderIcon(): Int {
        return R.drawable.ic_back
    }

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

}