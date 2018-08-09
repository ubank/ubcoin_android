package com.ubcoin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.ubcoin.R
import com.ubcoin.model.response.MarketItem
import com.ubcoin.utils.CollectionExtensions
import com.ubcoin.utils.RoundedCornersTransform

/**
 * Created by Yuriy Aizenberg
 */
class DealsListAdapter(context: Context) : BaseRecyclerAdapter<MarketItem, DealsListAdapter.VHolder>(context) {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VHolder {
        return VHolder(inflate(R.layout.item_market_in_deals, p0))
    }

    override fun onBindViewHolder(vHolder: VHolder, p1: Int) {
        val item = getItem(p1)
        val images = item.images
        if (CollectionExtensions.nullOrEmpty(images)) {
            vHolder.imgDealsItemLogo.setImageResource(R.drawable.img_rejected)
        } else {
            val dimensionPixelSize = context.resources.getDimensionPixelSize(R.dimen.marketInFavoriteHeightImage)
            Picasso.get().load(images!![0])
                    .resize(dimensionPixelSize, dimensionPixelSize)
                    .centerCrop()
                    .onlyScaleDown()
                    .transform(RoundedCornersTransform())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(R.drawable.img_rejected)
                    .into(vHolder.imgDealsItemLogo)
        }
        vHolder.txtDealsItemPrice.text = item.title
        vHolder.txtDealsItemName.text = (item.price.toString() + " UBC")
        bindTouchListener(vHolder.itemView, vHolder.adapterPosition, item)
    }


    class VHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
        val imgDealsItemLogo: ImageView = findView(R.id.imgDealsItemLogo)
        val txtDealsItemPrice: TextView = findView(R.id.txtDealsItemPrice)
        val txtDealsItemName: TextView = findView(R.id.txtDealsItemName)
    }

}
