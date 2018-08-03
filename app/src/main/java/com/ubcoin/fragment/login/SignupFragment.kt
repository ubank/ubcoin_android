package com.ubcoin.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
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

        return view
    }

    private fun onSignUpClick() {
        getSwitcher()?.clearBackStack()?.addTo(EndRegistrationFragment::class.java)
    }

    override fun showHeader(): Boolean = true

    override fun getHeaderIcon(): Int {
        return R.drawable.ic_back
    }

    override fun getHeaderText(): Int {
        return R.string.sign_up
    }

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }


}