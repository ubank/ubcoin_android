package com.ubcoin.view.deal_description

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.model.response.PurchaseItemStatus

class NeedDeliveryView: LinearLayout {
    private lateinit var btnNeedDelivery: Button

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
        LinearLayout.inflate(context, R.layout.view_need_delivery, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        btnNeedDelivery = findViewById(R.id.btnNeedDelivery)
        btnNeedDelivery.setOnClickListener{ if(buttonClickListener!= null) buttonClickListener!!.onNeedDelivery()}
    }

    interface OnButtonClickListener{
        fun onNeedDelivery()
    }
}