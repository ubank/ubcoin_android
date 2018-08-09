package com.ubcoin.fragment.login

import android.text.Editable
import android.view.View
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.TextWatcherAdatepr

/**
 * Created by Yuriy Aizenberg
 */

class CompleteRegistrationFragment : BaseFragment() {

    override fun getLayoutResId() = R.layout.fragment_complete_registration

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
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
    }

    private fun validateInput(materialEditText: MaterialEditText) = !materialEditText.text.toString().isBlank()

    override fun getHeaderText() = R.string.confirmation


    override fun getHeaderIcon() = R.drawable.ic_close

    private fun goNext() {
        getSwitcher()?.clearBackStack()?.addTo(EndRegistrationFragment::class.java)
    }

    override fun onBackPressed(): Boolean {
        performBack()
        return true
    }

    override fun onIconClick() {
        super.onIconClick()
        performBack()
    }

    private fun performBack() {
        getSwitcher()?.clearBackStack()?.addTo(StartupFragment::class.java)
    }
}