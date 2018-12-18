package com.ubcoin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.model.response.MarketItem
import com.ubcoin.utils.*
import com.ubcoin.view.rating.RatingBarView
import kotlin.math.roundToInt

/**
 * Created by Yuriy Aizenberg
 */

class MarketListAdapter(context: Context) : BaseRecyclerAdapter<MarketItem, MarketListAdapter.ViewHolder>(context) {

    private var ubcPostfix: String? = null
    private var ethPostfix: String? = null

    var favoriteListener: IFavoriteListener? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(inflate(R.layout.item_market_in_list, p0))
    }

    private fun getUbcPostfix(): String? {
        if (ubcPostfix == null) {
            ubcPostfix = context.getString(R.string.ubc_postfix)
        }
        return ubcPostfix
    }

    private fun getEthPostfix(): String? {
        if (ethPostfix == null) {
            ethPostfix = context.getString(R.string.eth_postfix)
        }
        return ethPostfix
    }

    private fun getPriceInCurrency(marketItem: MarketItem): String? {
        if (!marketItem.isPriceInCurrencyPresented()) return null
        return "~" + marketItem.priceInCurrency!!.moneyRoundedFormat() + " " + marketItem.currency
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        val item = getItem(position)
        vh.txtMarketProductName.text = item.title
        vh.txtMarketPrice.text = """${(item.priceETH ?: .0).moneyFormatETH()} ${getEthPostfix()}"""

        val images = item.images
        if (CollectionExtensions.nullOrEmpty(images)) {
            vh.imgMarket.setImageResource(R.drawable.img_photo_placeholder)
            vh.txtImagesCount.text = "0/0"
        } else {
            images?.let {
                GlideApp.with(context)
                        .load(it[0])
                        .centerCrop()
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.img_photo_placeholder)
                        .error(R.drawable.img_photo_placeholder)
                        .into(vh.imgMarket)
                vh.txtImagesCount.text = "1/${it.size}"
            }

        }
        val rating = item.user?.rating?.roundToInt()
        vh.ratingBarView.setRating(rating ?: 0)
        if (!ProfileHolder.isAuthorized() || item.isOwner()) {
            vh.imgFavorite.visibility = View.GONE
            vh.llFavoriteContainer.setOnClickListener { }
        } else {
            vh.imgFavorite.visibility = View.VISIBLE
            vh.imgFavorite.setImageResource(if (item.favorite) R.drawable.ic_favorite_list_on else R.drawable.ic_favorite_list_off)
            vh.llFavoriteContainer.setOnClickListener {
                favoriteListener?.onFavoriteTouch(item, vh.adapterPosition)
            }
        }
        val location = item.location

        if (location != null) {
            val itemLocationLatLng = LatLng(location.latPoint?.toDouble()
                    ?: .0, location.longPoint?.toDouble() ?: .0)
            vh.txtLocationDistance.text = DistanceUtils.calculateDistance(itemLocationLatLng, context)
        } else {
            vh.txtLocationDistance.text = null
        }

        vh.txtPriceInCurrency.text = getPriceInCurrency(item)

        bindTouchListener(vh.itemView, vh.adapterPosition, item)
    }


    class ViewHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {

        val imgMarket: ImageView = findView(R.id.imgMarket)
        val txtImagesCount: TextView = findView(R.id.txtImagesCount)
        val txtLocationDistance: TextView = findView(R.id.txtLocationDistance)
        val txtMarketPrice: TextView = findView(R.id.txtMarketPrice)
        val txtMarketProductName: TextView = findView(R.id.txtMarketProductName)
        val ratingBarView: RatingBarView = findView(R.id.ratingBarView)
        val llFavoriteContainer: View = findView(R.id.llFavoriteContainer)
        val imgFavorite: ImageView = findView(R.id.imgFavorite)
        val txtPriceInCurrency: TextView = findView(R.id.txtPriceInCurrency)

    }


    interface IFavoriteListener {
        fun onFavoriteTouch(data: MarketItem, position: Int)
    }
}