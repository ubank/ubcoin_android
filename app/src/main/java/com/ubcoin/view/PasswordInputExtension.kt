package com.ubcoin.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.utils.checkAsPassword
import kotlinx.android.synthetic.main.view_password_input.view.*

/**
 * Created by Yuriy Aizenberg
 */

class PasswordInputExtension : RelativeLayout {

    var edtPasswordInputExtension: MaterialEditText? = null

    constructor(context: Context?) : super(context) {
        create(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        create(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        create(attrs)
    }

    private fun create(attrs: AttributeSet?) {
        inflate(context, R.layout.view_password_input, this)
        edtPasswordInputExtension = findViewById(R.id.edtPasswordInputExtension)
        edtPasswordInputExtension?.addTextChangedListener(watch())
        if (attrs == null) return

        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.PasswordInputExtension, 0, 0)

        val inputHint = attributes.getString(R.styleable.PasswordInputExtension_PasswordInputHint)
        edtPasswordInputExtension?.hint = inputHint ?: context.resources.getString(R.string.password_hint)
        attributes.recycle()

    }

    private fun watch(): TextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            val inputText = getInputText()
            val colorResId: Int
            val hintId: Int

            val check = inputText.checkAsPassword()

            when (check) {
                -1 -> {
                    colorResId = R.color.inputTextColor
                    hintId = R.string.empty
                }
                0, 1 -> {
                    colorResId = R.color.passwordWeak
                    hintId = if (check == 1) R.string.weak else R.string.fair
                }
                2 -> {
                    colorResId = R.color.passwordNormal
                    hintId = R.string.normal
                }
                else -> {
                    colorResId = R.color.passwordStrong
                    hintId = if (check == 3) R.string.strong else R.string.very_strong
                }
            }
            txtPasswordInputExtension?.apply {
                setTextColor(ContextCompat.getColor(context, colorResId))
                setText(hintId)
            }
            edtPasswordInputExtension?.setMetTextColor(ContextCompat.getColor(context, colorResId))
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

    }

    fun getInputText() = edtPasswordInputExtension?.text.toString()

    fun setImeOptionListener(onEditorActionListener: TextView.OnEditorActionListener) {
        edtPasswordInputExtension?.setOnEditorActionListener(onEditorActionListener)
    }
}