package com.ubcoin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.model.response.ExchangeMarket

/**
 * Created by Yuriy Aizenberg
 */
class ExchangeMarketAdapter(context: Context) : BaseRecyclerAdapter<ExchangeMarket, ExchangeMarketAdapter.ExchangeVHolder>(context) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ExchangeVHolder {
        return ExchangeVHolder(inflate(R.layout.item_exchange_market, p0))
    }

    override fun onBindViewHolder(holder: ExchangeVHolder, position: Int) {
        val market = getItem(position)
        if (market.hasIcon()) {
            val dimensionPixelSize = context.resources.getDimensionPixelSize(R.dimen.detailsSubProfileHeight)
            GlideApp.with(context)
                    .load(market.icon)
                    .override(dimensionPixelSize, dimensionPixelSize)
                    .centerCrop()
                    .transform(RoundedCorners(dimensionPixelSize))
                    .placeholder(R.drawable.img_photo_placeholder)
                    .error(R.drawable.img_photo_placeholder)
                    .into(holder.imgExchangeMarketLogo)
        } else {
            holder.imgExchangeMarketLogo.setImageDrawable(null)
        }
        holder.txtExchangeMarketName.text = market.name
        bindTouchListener(holder.itemView, position, market)
    }


    class ExchangeVHolder(itemView: View) : VHolder(itemView) {
        val imgExchangeMarketLogo = findView<ImageView>(R.id.imgExchangeMarketLogo)
        val txtExchangeMarketName = findView<TextView>(R.id.txtExchangeMarketName)
        val txtExchangeMarketQuotationETH = findView<TextView>(R.id.txtExchangeMarketQuotationETH)
        val txtExchangeMarketQuotationUSD = findView<TextView>(R.id.txtExchangeMarketQuotationUSD)
        val llExchangeMarketQuotationUSD = findView<View>(R.id.llExchangeMarketQuotationUSD)
    }
}