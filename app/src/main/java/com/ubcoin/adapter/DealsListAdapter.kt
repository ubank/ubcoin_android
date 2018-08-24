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
import com.ubcoin.utils.CollectionExtensions
import com.ubcoin.utils.moneyFormat

/**
 * Created by Yuriy Aizenberg
 */
class DealsListAdapter(context: Context) : BaseRecyclerAdapter<DealItemWrapper, DealsListAdapter.VHolder>(context) {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VHolder {
        return VHolder(inflate(R.layout.item_market_in_deals, p0))
    }

    override fun onBindViewHolder(vHolder: VHolder, p1: Int) {
        val itemWrapper = getItem(p1)
        val item = itemWrapper.dealItem
        val images = item.images
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
        vHolder.txtDealsItemPrice.text = item.title
        vHolder.txtDealsItemName.text = (item.price.moneyFormat() + " UBC")
        bindTouchListener(vHolder.itemView, vHolder.adapterPosition, itemWrapper)
    }


    class VHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
        val imgDealsItemLogo: ImageView = findView(R.id.imgDealsItemLogo)
        val txtDealsItemPrice: TextView = findView(R.id.txtDealsItemPrice)
        val txtDealsItemName: TextView = findView(R.id.txtDealsItemName)
    }

}
