package com.ubcoin.view.deal_description

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import com.ubcoin.R

class ProgressDescriptionView: ConstraintLayout {

    var buttonClickListener: OnButtonClickListener? = null

    fun setClickListener(listener: OnButtonClickListener){
        buttonClickListener = listener
    }

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(attrs: AttributeSet?) {
        LinearLayout.inflate(context, R.layout.view_progress_description, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        findViewById<Button>(R.id.btnConfirmFile).setOnClickListener{ if(buttonClickListener!= null) buttonClickListener!!.onConfirmFileClicked()}
        findViewById<Button>(R.id.btnReceivedItem).setOnClickListener{ if(buttonClickListener!= null) buttonClickListener!!.onReceivedItemClicked()}
    }

    interface OnButtonClickListener{
        fun onConfirmFileClicked()
        fun onReceivedItemClicked()
    }
}