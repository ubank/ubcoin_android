package com.ubcoin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.ubcoin.R
import com.ubcoin.model.response.MarketItem
import com.ubcoin.utils.CollectionExtensions
import com.ubcoin.view.rating.RatingBarView
import java.util.*
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
            Picasso.get().load(images[0])
                    .error(R.drawable.img_rejected)
                    .placeholder(R.drawable.img_loading_progress)
                    .into(vh.imgMarket)
            vh.txtImagesCount.text = "1/${images.size}"
        }
//        val rating = item.user.rating.roundToInt()
        val rating = Random().nextInt(5)
        vh.ratingBarView.setRating(rating)
    }


    class ViewHolder: BaseRecyclerAdapter.VHolder{

        val imgMarket: ImageView
        val txtImagesCount : TextView
        val txtLocationDistance : TextView
        val txtMarketPrice : TextView
        val txtMarketProductName : TextView
        val ratingBarView: RatingBarView

        constructor(itemView: View) : super(itemView) {
            imgMarket = findView(R.id.imgMarket)
            txtImagesCount = findView(R.id.txtImagesCount)
            txtLocationDistance = findView(R.id.txtLocationDistance)
            txtMarketPrice = findView(R.id.txtMarketPrice)
            txtMarketProductName = findView(R.id.txtMarketProductName)
            ratingBarView = findView(R.id.ratingBarView)
        }
    }
}