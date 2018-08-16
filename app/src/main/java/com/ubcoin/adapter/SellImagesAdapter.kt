package com.ubcoin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ubcoin.R
import com.ubcoin.model.SellImageModel

/**
 * Created by Yuriy Aizenberg
 */
class SellImagesAdapter(context: Context) : BaseRecyclerAdapter<SellImageModel, SellImagesAdapter.SellImageVH>(context) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SellImageVH {
        return SellImageVH(inflate(R.layout.item_sell_image, p0))
    }

    override fun onBindViewHolder(p0: SellImageVH, p1: Int) {
        val item = getItem(p1)
        if (item.hasImage()) {

        } else {

        }

        bindTouchListener(p0.itemView, p0.adapterPosition, item)
    }


    class SellImageVH(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
        val imgSellImage = findView<ImageView>(R.id.imgSellImage)
    }
}