package com.ubcoin.adapter

import android.annotation.SuppressLint
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
import com.ubcoin.view.rating.RatingBarView
import kotlin.math.roundToInt

/**
 * Created by Yuriy Aizenberg
 */

class MarketListAdapter(context: Context) : BaseRecyclerAdapter<MarketItem, MarketListAdapter.ViewHolder>(context) {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(inflate(R.layout.item_market_in_list, p0))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        val item = getItem(position)
        vh.txtMarketProductName.text = item.title
        vh.txtMarketPrice.text = item.price.toString()

        val images = item.images
        if (CollectionExtensions.nullOrEmpty(images)) {
            vh.imgMarket.setImageResource(R.drawable.stub_image)
            vh.txtImagesCount.text = "0/0"
        } else {
            images?.let {
                Picasso.get().load(it[0])
                        .resize(context.resources.getDimensionPixelSize(R.dimen.default_list_image_size), 0)
                        .centerCrop()
                        .onlyScaleDown()
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .error(R.drawable.img_rejected)
//                        .placeholder(R.drawable.loading_progress)
                        .into(vh.imgMarket)
                vh.txtImagesCount.text = "1/${it.size}"
            }

        }
        val rating = item.user?.rating?.roundToInt()
        vh.ratingBarView.setRating(rating ?: 0)
        bindTouchListener(vh.itemView, vh.adapterPosition, item)
    }


    class ViewHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {

        val imgMarket: ImageView = findView(R.id.imgMarket)
        val txtImagesCount : TextView = findView(R.id.txtImagesCount)
        val txtLocationDistance : TextView = findView(R.id.txtLocationDistance)
        val txtMarketPrice : TextView = findView(R.id.txtMarketPrice)
        val txtMarketProductName : TextView = findView(R.id.txtMarketProductName)
        val ratingBarView: RatingBarView = findView(R.id.ratingBarView)

    }
}