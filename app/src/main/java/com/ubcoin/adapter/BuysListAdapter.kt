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
import com.ubcoin.model.response.MarketItemStatus
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
        val images = marketItem.item.images
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
        vHolder.txtDealsItemPrice.text = marketItem.item.title.trim()
        vHolder.txtDealsItemName.text = (marketItem.item.price.moneyFormat() + " UBC")
        if(marketItem.status != null) {
            if(marketItem.status == MarketItemStatus.ACTIVE) {
                if(marketItem.item.categoryId.equals("dc602e1f-80d2-af0d-9588-de6f1956f4ef")) {
                    vHolder.txtDealsItemStatus.text = context.getString(R.string.str_item_status_active_digital)
                }
                else
                {
                    if (marketItem.withDelivery == null || marketItem.withDelivery)
                        vHolder.txtDealsItemStatus.text = context.getString(R.string.str_item_status_active_buyer_delivery)
                    else
                        vHolder.txtDealsItemStatus.text = context.getString(R.string.str_item_status_active_buyer_meet)
                }
            }
            else
                vHolder.txtDealsItemStatus.text = context.getString(marketItem.status.stringResourceId)
        }
        else
            vHolder.txtDealsItemStatus.text = "status null"

        bindTouchListener(vHolder.itemView, vHolder.adapterPosition, marketItem)
    }


    class VHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
        val imgDealsItemLogo: ImageView = findView(R.id.imgDealsItemLogo)
        val txtDealsItemPrice: TextView = findView(R.id.txtDealsItemPrice)
        val txtDealsItemName: TextView = findView(R.id.txtDealsItemName)
        val txtDealsItemStatus: TextView = findView(R.id.txtDealsItemStatus)
    }
}
