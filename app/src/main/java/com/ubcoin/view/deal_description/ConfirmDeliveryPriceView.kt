package com.ubcoin.view.deal_description

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.ubcoin.R
import com.ubcoin.model.Currency
import com.ubcoin.model.Purchase
import com.ubcoin.model.response.PurchaseItemStatus
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.utils.moneyFormat
import com.ubcoin.utils.moneyFormatETH
import com.ubcoin.utils.moneyRoundedFormatETH

class ConfirmDeliveryPriceView: LinearLayout {
    private lateinit var tvDeliveryPrice: TextView
    private lateinit var tvBalance: TextView
    private lateinit var btnConfirm: Button

    var item: Purchase? = null
        set(value) {
            field = value
            var currency = "UBC"
            tvBalance.text = context.getString(R.string.text_your_balance) + " " + (ProfileHolder.balance?.effectiveAmount ?: .0).moneyFormat() + " UBC"
            if(value?.item?.priceETH != null) {
                currency == "ETH"
                tvBalance.text = context.getString(R.string.text_your_balance) + " " + (ProfileHolder.balance?.effectiveAmountETH ?: .0).moneyFormatETH() + " ETH"
            }

            var price = item!!.deliveryPrice
            var amount = ProfileHolder.balance?.effectiveAmount
            if(value?.item?.priceETH != null) {
                amount = ProfileHolder.balance?.effectiveAmountETH
            }

            if(amount != null) {
                if (amount!! < price!!)
                    tvBalance.setTextColor(context.resources.getColor(R.color.red))
                else
                    tvBalance.setTextColor(Color.parseColor("#403d45"))
            }
            tvDeliveryPrice.text = value!!.deliveryPrice.toString() + " " + currency

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
        LinearLayout.inflate(context, R.layout.view_confirm_delivery_price, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        tvDeliveryPrice = findViewById(R.id.tvDeliveryPrice)
        tvBalance = findViewById(R.id.tvBalance)
        btnConfirm = findViewById(R.id.btnConfirm)
        btnConfirm.setOnClickListener{

            var price = item!!.deliveryPrice
            var amount = ProfileHolder.balance?.effectiveAmount
            var text = context.getString(R.string.text_not_enough_ubc)
            var priceText = (price ?: .0).moneyFormat() + " UBC"

            if(item?.item?.priceETH != null)
            {
                amount = ProfileHolder.balance?.effectiveAmountETH
                text = context.getString(R.string.text_not_enough_eth)
                priceText = price.moneyRoundedFormatETH() + " ETH"
            }

            if(amount!! < price!!)
            {
                val materialDialog = MaterialDialog.Builder(context)
                        .content(text)
                        .title(context.getString(R.string.text_please_top_up_your_balance))
                        .positiveText(context.getString(R.string.text_ok))
                        .positiveColor(ContextCompat.getColor(context, R.color.greenMainColor))
                        .onPositive { dialog, _ -> dialog.dismiss() }
                        .build()
                materialDialog!!.show()
            }
            else
            {
                val materialDialog = MaterialDialog.Builder(context)
                        .content(context.getString(R.string.text_confirm_purchace))
                        .title(priceText + " " + context.getString(R.string.text_will_be_blocked))
                        .positiveText(context.getString(R.string.confirm))
                        .negativeText(context.getString(R.string.cancel))
                        .positiveColor(ContextCompat.getColor(context, R.color.greenMainColor))
                        .negativeColor(ContextCompat.getColor(context, R.color.greenMainColor))
                        .onPositive { dialog, _ -> buyItem()
                            dialog.dismiss() }
                        .onNegative { dialog, _ -> dialog.dismiss() }
                        .build()
                materialDialog!!.show()
            }
        }
    }

    interface OnButtonClickListener{
        fun onConfirm()
    }

    fun buyItem() {
        if(buttonClickListener != null)
            buttonClickListener!!.onConfirm()
    }
}