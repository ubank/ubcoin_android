package com.ubcoin.view.deal_description

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.model.Currency
import com.ubcoin.model.Purchase
import com.ubcoin.model.response.PurchaseItemStatus
import com.ubcoin.utils.MaxValueInputFilter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class ProgressDescriptionView: ConstraintLayout {

    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var ivImage: ImageView
    private lateinit var btnConfirmFile: Button
    private lateinit var btnReceivedItem: Button
    private lateinit var btnConfirmDeliveryStart: Button
    private lateinit var btnConfirmNewDeliveryPrice: Button
    private lateinit var llNewDeliveryPrice: LinearLayout
    private lateinit var tvAddress: TextView
    private lateinit var tvPrice: TextView
    private lateinit var llNewPrice: LinearLayout

    var newPrice: Double = .0

    var status: PurchaseItemStatus? = null
        set(value) {
            field = value
            initView()
        }

    var isDigital:Boolean = false
    set(value){
        field = value
        initView()
    }

    var isSeller:Boolean = false
        set(value){
            field = value
            initView()
        }

    var isDelivery:Boolean = false
        set(value){
            field = value
            initView()
        }

    var item:Purchase? = null
    set(value){
        field = value

        var currency = "UBC"
        if(value?.item?.priceETH != null)
            currency == "ETH"
        newPrice = value!!.deliveryPrice
        tvPrice.text = newPrice.toString() + " " + currency
        if(value.comment != null && value.comment.length > 0)
            tvAddress.text = value.comment
    }

    var activity: Activity? = null


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
        llNewPrice = findViewById(R.id.llNewPrice)
        llNewPrice.setOnClickListener {
            val materialDialog = MaterialDialog.Builder(activity!!)
                    .customView(R.layout.fragment_content_select_price, false)
                    .build()
            val edtPrice: MaterialEditText = materialDialog.findViewById(R.id.edtPrice) as MaterialEditText
            edtPrice.hint = context.getString(R.string.text_delivery_price)

            materialDialog.findViewById(R.id.btnDialogCancel).setOnClickListener {
                materialDialog.dismiss()
            }

            materialDialog.findViewById(R.id.btnDialogDone).setOnClickListener {
                materialDialog.dismiss()
                val value = try {
                    edtPrice.text.toString().toDouble()
                } catch (e: Exception) {
                    .0
                }

                var currency = "UBC"
                if(item?.item?.priceETH != null)
                    currency == "ETH"
                newPrice = value
                tvPrice.text = newPrice.toString() + " " + currency

                if(newPrice != item!!.deliveryPrice)
                    btnConfirmNewDeliveryPrice.visibility = View.VISIBLE
                else
                    btnConfirmNewDeliveryPrice.visibility = View.GONE
            }

            materialDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            materialDialog.show()
        }
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
        ivImage = findViewById(R.id.ivImage)
        tvAddress = findViewById(R.id.tvAddress)
        tvPrice = findViewById(R.id.tvPrice)
        llNewDeliveryPrice = findViewById(R.id.llNewDeliveryPrice)
        btnConfirmFile = findViewById(R.id.btnConfirmFile)
        btnConfirmFile.setOnClickListener{ if(buttonClickListener!= null) buttonClickListener!!.onConfirmFileClicked()}
        btnReceivedItem = findViewById(R.id.btnReceivedItem)
        btnReceivedItem.setOnClickListener{ if(buttonClickListener!= null) buttonClickListener!!.onReceivedItemClicked()}
        btnConfirmDeliveryStart = findViewById(R.id.btnConfirmDeliveryStart)
        btnConfirmDeliveryStart.setOnClickListener{ if(buttonClickListener!= null) buttonClickListener!!.onConfirmDeliveryStart()}
        btnConfirmNewDeliveryPrice = findViewById(R.id.btnConfirmNewDeliveryPrice)
        btnConfirmNewDeliveryPrice.setOnClickListener{
            if(newPrice != item!!.deliveryPrice)
                if(buttonClickListener!= null) buttonClickListener!!.onConfirmNewDeliveryPrice(newPrice)
            else{
                    val materialDialog = MaterialDialog.Builder(context)
                            .content(context.getString(R.string.text_new_price_equal_old))
                            .positiveText(context.getString(R.string.text_ok))
                            .positiveColor(ContextCompat.getColor(context, R.color.greenMainColor))
                            .onPositive { dialog, _ -> dialog.dismiss() }
                            .build()
                    materialDialog!!.show()
                }
        }
    }

    interface OnButtonClickListener{
        fun onConfirmFileClicked()
        fun onReceivedItemClicked()
        fun onConfirmDeliveryStart()
        fun onConfirmNewDeliveryPrice(price: Double)
    }

    fun initView() {
        btnConfirmFile.visibility = View.GONE
        btnReceivedItem.visibility = View.GONE
        btnConfirmDeliveryStart.visibility = View.GONE
        llNewDeliveryPrice.visibility = View.GONE
        when(status){
            PurchaseItemStatus.CREATED -> {
            }

            PurchaseItemStatus.ACTIVE -> {
                if(isDigital) {
                    if (isSeller) {
                        tvTitle.text = context.getString(R.string.text_digital_active_purchase_title_seller)
                        tvDescription.text = context.getString(R.string.text_digital_active_purchase_description_seller)
                        ivImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_status_booked))
                    }
                    else{
                        tvTitle.text = context.getString(R.string.text_digital_active_purchase_title_buyer)
                        tvDescription.text = context.getString(R.string.text_digital_active_purchase_description_buyer)
                        ivImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_status_booked))
                        btnConfirmFile.visibility = View.VISIBLE
                    }
                }
                else{
                    if(isDelivery) {
                        if (isSeller) {
                            tvTitle.text = context.getString(R.string.text_delivery_active_purchase_title_seller)
                            tvDescription.text = context.getString(R.string.text_delivery_active_purchase_description_seller)
                            ivImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_status_booked))
                        }
                        else{
                            tvTitle.text = context.getString(R.string.text_delivery_active_purchase_title_buyer)
                            tvDescription.text = context.getString(R.string.text_delivery_active_purchase_description_buyer)
                            ivImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_status_booked))
                        }
                    }
                    else{
                        if (isSeller) {
                            tvTitle.text = context.getString(R.string.text_active_purchase_title_seller)
                            tvDescription.text = context.getString(R.string.text_active_purchase_description_seller)
                            ivImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_status_booked))
                        }
                        else{
                            tvTitle.text = context.getString(R.string.text_active_purchase_title_buyer)
                            tvDescription.text = context.getString(R.string.text_active_purchase_description_buyer)
                            ivImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_status_booked))
                            btnReceivedItem.visibility = View.VISIBLE
                        }
                    }
                }
            }

            PurchaseItemStatus.DELIVERY_PRICE_DEFINED -> {
                if(isSeller) {
                    tvTitle.text = context.getString(R.string.text_delivery_price_defined_title_seller)
                    tvDescription.text = context.getString(R.string.text_delivery_price_defined_description_seller)
                    llNewDeliveryPrice.visibility = View.VISIBLE
                }
                else{
                    tvTitle.text = context.getString(R.string.text_delivery_price_defined_title_buyer)
                    tvDescription.text = context.getString(R.string.text_delivery_price_defined_description_buyer)
                }
                ivImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_status_booked))
            }

            PurchaseItemStatus.DELIVERY_PRICE_CONFIRMED -> {
                if(isSeller) {
                    tvTitle.text = context.getString(R.string.text_delivery_price_confirmed_title_seller)
                    tvDescription.text = context.getString(R.string.text_delivery_price_confirmed_description_seller)
                    btnConfirmDeliveryStart.visibility = View.VISIBLE
                }
                else{
                    tvTitle.text = context.getString(R.string.text_delivery_price_confirmed_title_buyer)
                    tvDescription.text = context.getString(R.string.text_delivery_price_confirmed_description_buyer)
                }
                ivImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_status_frozen))
            }

            PurchaseItemStatus.DELIVERY -> {
                if(isSeller) {
                    tvTitle.text = context.getString(R.string.text_delivery_title_seller)
                    tvDescription.text = context.getString(R.string.text_delivery_description_seller)
                }
                else{
                    tvTitle.text = context.getString(R.string.text_delivery_title_buyer)
                    tvDescription.text = context.getString(R.string.text_delivery_description_buyer)
                    btnReceivedItem.visibility = View.VISIBLE
                }
                ivImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_status_process))
            }

            PurchaseItemStatus.CONFIRMED -> {
                if(isDigital){
                    if(isSeller) {
                        tvTitle.text = context.getString(R.string.text_digital_confirmed_title_seller)
                        tvDescription.text = context.getString(R.string.text_digital_confirmed_description_seller)
                    }
                    else{
                        tvTitle.text = context.getString(R.string.text_digital_confirmed_title_buyer)
                        tvDescription.text = context.getString(R.string.text_digital_confirmed_description_buyer)
                    }
                }
                else{
                    if(isDelivery) {
                        if (isSeller) {
                            tvTitle.text = context.getString(R.string.text_delivery_confirmed_title_seller)
                            tvDescription.text = context.getString(R.string.text_delivery_confirmed_description_seller)
                        } else {
                            tvTitle.text = context.getString(R.string.text_delivery_confirmed_title_buyer)
                            tvDescription.text = context.getString(R.string.text_delivery_confirmed_description_buyer)
                        }
                    }
                    else{
                        if (isSeller) {
                            tvTitle.text = context.getString(R.string.text_confirmed_title_seller)
                            tvDescription.text = context.getString(R.string.text_confirmed_description_seller)
                        } else {
                            tvTitle.text = context.getString(R.string.text_confirmed_title_buyer)
                            tvDescription.text = context.getString(R.string.text_confirmed_description_buyer)
                        }
                    }
                }
                ivImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_green))
            }

            PurchaseItemStatus.CANCELED -> {

            }
        }
    }
}