package com.ubcoin.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.model.Currency
import com.ubcoin.model.response.MarketItem
import com.ubcoin.model.response.MarketItemHeader
import com.ubcoin.model.response.MarketItemMarker
import com.ubcoin.model.response.MarketItemStatus
import com.ubcoin.utils.CollectionExtensions
import com.ubcoin.utils.WordUtils
import com.ubcoin.utils.moneyFormat
import com.ubcoin.utils.moneyFormatETH

@Suppress("PrivatePropertyName")
/**
 * Created by Yuriy Aizenberg
 */
class SellsListAdapter(context: Context) : BaseRecyclerAdapter<MarketItemMarker, SellsListAdapter.AbsSellHolder>(context) {

    private val TYPE_HEADER = 0
    private val TYPE_MARKET = 1

    private val defaultStatusTextColor: Int = ContextCompat.getColor(context, R.color.haveAccountColor)
    private val blockedStatusTextColor: Int = ContextCompat.getColor(context, R.color.itemStatusBlock)


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): AbsSellHolder {
        return if (viewType == TYPE_HEADER) {
            HeaderVHoler(inflate(R.layout.item_market_in_deals_header, p0))
        } else {
            VHolder(inflate(R.layout.item_market_in_deals, p0))
        }
    }

    override fun onBindViewHolder(vHolder: AbsSellHolder, position: Int) {
        val marketItem = getItem(position)
        if (getItemViewType(position) == TYPE_MARKET) {
            bindMarketItem(marketItem as MarketItem, vHolder as VHolder)
        } else {
            bindMarketHeader(marketItem as MarketItemHeader, vHolder as HeaderVHoler)
        }
    }

    private fun bindMarketItem(marketItem: MarketItem, vHolder: VHolder) {
        val images = marketItem.images
        if (CollectionExtensions.nullOrEmpty(images)) {
            vHolder.imgDealsItemLogo.setImageResource(R.drawable.img_photo_placeholder)
        } else {
            val dimensionPixelSize = context.resources.getDimensionPixelSize(R.dimen.marketInFavoriteHeightImage)

            GlideApp.with(context)
                    .load(images!![0])
                    .override(dimensionPixelSize, dimensionPixelSize)
                    .centerCrop()
                    .transform(RoundedCorners(10))
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.img_photo_placeholder)
                    .error(R.drawable.img_photo_placeholder)
                    .into(vHolder.imgDealsItemLogo)
        }
        vHolder.txtDealsItemPrice.text = marketItem.title
        vHolder.txtDealsItemName.text = (marketItem.price?.moneyFormat() + " UBC")

        if(marketItem.purchases != null && marketItem.purchases.size > 0)
        {
            if(marketItem.purchases.get(0).currencyType == Currency.ETH)
                vHolder.txtDealsItemName.text = (marketItem.priceETH?.moneyFormatETH() + " ETH")
        }

        setupCorrectStatus(marketItem, vHolder)
        bindTouchListener(vHolder.itemView, vHolder.adapterPosition, marketItem)
    }

    private fun setupCorrectStatus(marketItem: MarketItem, vHolder: VHolder) {
        vHolder.txtDealsItemStatus.text = marketItem.statusDescription
    }

    private fun bindMarketHeader(marketItemHeader: MarketItemHeader, vHolder: HeaderVHoler) {
        vHolder.txtHeader.text = marketItemHeader.name
        unbindTouchListener(vHolder.itemView)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item is MarketItem) TYPE_MARKET else TYPE_HEADER
    }

    abstract class AbsSellHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView)

    class VHolder(itemView: View) : AbsSellHolder(itemView) {
        val imgDealsItemLogo: ImageView = findView(R.id.imgDealsItemLogo)
        val txtDealsItemPrice: TextView = findView(R.id.txtDealsItemPrice)
        val txtDealsItemName: TextView = findView(R.id.txtDealsItemName)
        val txtDealsItemStatus: TextView = findView(R.id.txtDealsItemStatus)
    }

    class HeaderVHoler(itemView: View) : AbsSellHolder(itemView) {
        val txtHeader: TextView = findView(R.id.txtMarketType)
    }

}
