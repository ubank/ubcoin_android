package com.ubcoin.view.deal_description

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.model.Currency
import com.ubcoin.model.response.MarketItem
import com.ubcoin.utils.moneyFormat
import com.ubcoin.utils.moneyRoundedFormatETH

class ItemDescriptionView: LinearLayout {
    var marketItem: MarketItem? = null
        set(value) {
            field = value
            initView()
        }

    var currency: Currency? = null
        set(value) {
            field = value
            initView()
        }

    private lateinit var tvName: TextView
    private lateinit var tvPrice: TextView
    private lateinit var ivImage: ImageView

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
        inflate(context, R.layout.view_item_description, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        tvName = findViewById(R.id.tvName)
        tvPrice = findViewById(R.id.tvPrice)
        ivImage = findViewById(R.id.ivImage)
    }

    fun initView(){
        GlideApp.with(context!!).load(marketItem?.images?.get(0))
                .centerCrop()
                .placeholder(R.drawable.img_profile_default)
                .error(R.drawable.img_profile_default)
                .into(ivImage)
        tvName.setText(marketItem?.title)

        var priceUBC: String? = null
        var priceETH: String? = null

        var resText = ""

        if(marketItem?.price != null && (currency == null || currency == Currency.UBC)) {
            priceUBC = (marketItem?.price ?: .0).moneyFormat() + " UBC"
            resText += priceUBC
        }

        if(marketItem?.priceETH != null && (currency == null || currency == Currency.ETH)) {
            priceETH = (marketItem?.priceETH ?: .0).moneyRoundedFormatETH() + " ETH"
            if(resText.length > 0) resText += " / "
            resText += priceETH
        }

        tvPrice.setText(resText)
    }
}