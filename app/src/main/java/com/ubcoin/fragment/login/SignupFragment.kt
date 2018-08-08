package com.ubcoin.fragment.login

import android.text.Editable
import android.view.View
import android.view.inputmethod.EditorInfo
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.ImeNextActionHandler
import com.ubcoin.view.PasswordInputExtension

/**
 * Created by Yuriy Aizenberg
 */
class SignupFragment : BaseFragment() {

    override fun getLayoutResId() = R.layout.fragment_signup

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        view.findViewById<View>(R.id.llSignUp).setOnClickListener { onSignUpClick() }
        view.findViewById<View>(R.id.llAlreadyHaveAccount).setOnClickListener {
            getSwitcher()?.run {
                clearBackStack().addTo(StartupFragment::class.java, false).addTo(LoginFragment::class.java)
            }
        }

        val edtSignUpName = view.findViewById<MaterialEditText>(R.id.edtSignUpName)
        val edtSignUpEmail = view.findViewById<MaterialEditText>(R.id.edtSignUpEmail)
        val edtPasswordInput = view.findViewById<PasswordInputExtension>(R.id.edtPasswordInput)
        edtPasswordInput?.edtPasswordInputExtension?.imeOptions = EditorInfo.IME_ACTION_DONE

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
                hideKeyboard()
            }
        })

        view.findViewById<View>(R.id.llUserAgreement).setOnClickListener {  showUserAgreement() }

    }

    private fun onSignUpClick() {
        getSwitcher()?.addTo(CompleteRegistrationFragment::class.java)
    }

    override fun showHeader(): Boolean = true

    override fun getHeaderIcon()=  R.drawable.ic_back

    override fun getHeaderText()= R.string.sign_up

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }


}