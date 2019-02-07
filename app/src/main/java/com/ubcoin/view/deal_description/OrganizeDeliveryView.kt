package com.ubcoin.view.deal_description

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import com.ubcoin.R

class OrganizeDeliveryView: LinearLayout {
    private lateinit var btnOrganizeDelivery: Button

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
        LinearLayout.inflate(context, R.layout.view_organize_delivery, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        btnOrganizeDelivery = findViewById(R.id.btnOrganizeDelivery)
        btnOrganizeDelivery.setOnClickListener{ if(buttonClickListener!= null) buttonClickListener!!.onOrganizeDelivery()}
    }

    interface OnButtonClickListener{
        fun onOrganizeDelivery()
    }
}