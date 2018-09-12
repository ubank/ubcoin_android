package com.ubcoin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.model.SellImageModel
import java.io.File

/**
 * Created by Yuriy Aizenberg
 */
class SellImagesAdapter(context: Context) : BaseRecyclerAdapter<SellImageModel, SellImagesAdapter.SellImageVH>(context) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SellImageVH {
        return SellImageVH(inflate(R.layout.item_sell_image, p0))
    }

    override fun onBindViewHolder(vh: SellImageVH, position: Int) {
        val item = getItem(position)
        if (item.hasImage()) {
            vh.imgSellImage.scaleType = ImageView.ScaleType.CENTER_CROP
            GlideApp.with(context)
                    .load(File(item.filePath))
                    .centerCrop()
                    .into(vh.imgSellImage)
        } else {
            vh.imgSellImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
            vh.imgSellImage.setImageDrawable(null)
            vh.imgSellImage.setImageResource(R.drawable.ic_cam_green)
        }

        bindTouchListener(vh.itemView, vh.adapterPosition, item)
    }


    class SellImageVH(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
        val imgSellImage: ImageView = findView(R.id.imgSellImage)
    }

    fun findFirstEmptyContainerPosition(): Int {
        for ((index, element) in data.withIndex()) {
            if (!element.hasImage()) return index
        }
        return -1
    }
}