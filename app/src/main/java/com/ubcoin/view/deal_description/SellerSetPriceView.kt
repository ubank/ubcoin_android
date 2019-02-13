package com.ubcoin.view.deal_description

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.model.Currency
import com.ubcoin.model.Purchase
import com.ubcoin.model.response.PurchaseItemStatus

class SellerSetPriceView: ConstraintLayout {

    private lateinit var tvAddress: TextView
    private lateinit var etPrice: MaterialEditText
    private lateinit var btnConfirmDeliveryPrice: Button
    var activity: Activity? = null

    var item: Purchase? = null
        set(value) {
            field = value
            initView()
        }

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
        LinearLayout.inflate(context, R.layout.view_seller_set_price, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        tvAddress = findViewById(R.id.tvAddress)
        etPrice = findViewById(R.id.etPrice)
        btnConfirmDeliveryPrice = findViewById(R.id.btnConfirmDeliveryPrice)
        btnConfirmDeliveryPrice.setOnClickListener{ if(buttonClickListener!= null) {
            if (etPrice.text.toString() == null || etPrice.text.toString().length == 0) {
                if (activity != null) {
                    activity?.run {
                        MaterialDialog.Builder(this).title(R.string.error).content(getString(R.string.text_price_is_missing)).build().show()
                    }
                }
                return@setOnClickListener
            }

            buttonClickListener!!.onConfirmDeliveryPrice(etPrice.text.toString().toDouble())
        }}
    }

    interface OnButtonClickListener{
        fun onConfirmDeliveryPrice(price: Double)
    }

    fun initView() {
        if(item?.comment != null && item?.comment!!.length > 0)
            tvAddress.text = item?.comment
        if(item?.item?.price != null)
        {
            etPrice.setHint(R.string.hint_price_in_ubc)
            etPrice.hint = context.getString(R.string.hint_price_in_ubc)
        }

        if(item?.currencyType == Currency.ETH)
        {
            etPrice.setHint(R.string.hint_price_in_eth)
            etPrice.hint = context.getString(R.string.hint_price_in_eth)
        }
    }
}