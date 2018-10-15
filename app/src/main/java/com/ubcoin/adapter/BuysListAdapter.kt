package com.ubcoin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.model.response.DealItemWrapper
import com.ubcoin.model.response.MarketItem
import com.ubcoin.utils.CollectionExtensions
import com.ubcoin.utils.getCapitalizedName
import com.ubcoin.utils.moneyFormat

/**
 * Created by Yuriy Aizenberg
 */
class BuysListAdapter(context: Context) : BaseRecyclerAdapter<DealItemWrapper, BuysListAdapter.VHolder>(context) {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VHolder {
        return VHolder(inflate(R.layout.item_market_in_deals, p0))
    }

    override fun onBindViewHolder(vHolder: VHolder, p1: Int) {
        val marketItem = getItem(p1)
        val images = marketItem.dealItem.images
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
        vHolder.imgDealsSmallIcon.setImageResource(R.drawable.ic_tg)
        vHolder.txtDealsItemPrice.text = marketItem.dealItem.title.trim()
        vHolder.txtDealsItemName.text = (marketItem.dealItem.price.moneyFormat() + " UBC")
        vHolder.txtDealsItemStatus.text = marketItem.seller.getCapitalizedName()
        bindTouchListener(vHolder.itemView, vHolder.adapterPosition, marketItem)
    }


    class VHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
        val imgDealsItemLogo: ImageView = findView(R.id.imgDealsItemLogo)
        val txtDealsItemPrice: TextView = findView(R.id.txtDealsItemPrice)
        val txtDealsItemName: TextView = findView(R.id.txtDealsItemName)
        val imgDealsSmallIcon: ImageView = findView(R.id.imgDealsSmallIcon)
        val txtDealsItemStatus: TextView = findView(R.id.txtDealsItemStatus)
    }

}
