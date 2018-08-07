package com.ubcoin.fragment.market

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.Indicators.PagerIndicator
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.MarketItem
import com.ubcoin.utils.CircleTransformation
import com.ubcoin.utils.CollectionExtensions
import com.ubcoin.utils.SafetySliderView
import com.ubcoin.utils.TextDrawableUtils
import com.ubcoin.view.rating.RatingBarView
import java.lang.Exception


/**
 * Created by Yuriy Aizenberg
 */

class MarketDetailsFragment : BaseFragment() {

    lateinit var marketItem: MarketItem
    var sliderLayout: SliderLayout? = null
    var pageIndicator: PagerIndicator? = null

    companion object {
        fun getBundle(marketItem: MarketItem): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(MarketItem::class.java.simpleName, marketItem)
            return bundle
        }
    }


    override fun isGradientShow(): Boolean {
        super.isGradientShow()
        return false
    }

    override fun isFooterShow(): Boolean {
        super.isFooterShow()
        return false
    }

    override fun getLayoutResId() = R.layout.fragment_market_item_details

    override fun showHeader() = false

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        marketItem = arguments?.getSerializable(MarketItem::class.java.simpleName) as MarketItem
        sliderLayout = view.findViewById(R.id.slider)
        pageIndicator = view.findViewById(R.id.custom_indicator)

        view.findViewById<View>(R.id.llHeaderLeftSimple).setOnClickListener { activity?.onBackPressed() }

        if (!CollectionExtensions.nullOrEmpty(marketItem.images)) {
            val metrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(metrics)
            marketItem.images?.forEach {
                val textSliderView = SafetySliderView(activity!!, 0, metrics.widthPixels)
                textSliderView.scaleType = BaseSliderView.ScaleType.CenterCrop
                textSliderView.picasso = Picasso.get()
                textSliderView.image(it)
                textSliderView.description(null)
                textSliderView.error(R.drawable.img_rejected)
                sliderLayout?.addSlider(textSliderView)
            }
            if (marketItem.images?.size == 1) {
                val ghostView = view.findViewById<View>(R.id.ghostView)
                ghostView.setOnClickListener { View.OnClickListener { } }
                ghostView.visibility = View.VISIBLE
                pageIndicator?.visibility = View.GONE
            }
        }
        sliderLayout?.run {
            setPresetTransformer(SliderLayout.Transformer.Accordion)
            setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
            setCustomAnimation(DescriptionAnimation())
            stopAutoCycle()
        }

        view.findViewById<TextView>(R.id.txtHeaderSimple).text = marketItem.title
        view.findViewById<TextView>(R.id.txtItemPrice).text = marketItem.price?.toString() + " UBC"
        view.findViewById<TextView>(R.id.txtItemCategor).text = marketItem.category?.name
        view.findViewById<TextView>(R.id.txtMarketProductName).text = marketItem.title
        view.findViewById<TextView>(R.id.txtMarketProductDescription).text = marketItem.description

        val imageView = view.findViewById<ImageView>(R.id.imgSellerProfile)
        val user = marketItem.user
        val avatarUrl = user?.avatarUrl
        val createTextDrawableRounded = TextDrawableUtils.createTextDrawableRounded(user?.name, R.dimen.detailsSubProfileHeight, activity!!)
        if (avatarUrl == null) {
            imageView.setImageDrawable(createTextDrawableRounded)
        } else {
            Picasso.get().load(avatarUrl).error(createTextDrawableRounded)
                    .resizeDimen(R.dimen.detailsSubProfileHeight, R.dimen.detailsSubProfileHeight)
                    .centerCrop()
                    .transform(CircleTransformation())
                    .error(createTextDrawableRounded)
                    .into(imageView)
        }
        view.findViewById<TextView>(R.id.txtUserName).text = user?.name

        user?.rating?.toInt()?.let { view.findViewById<RatingBarView>(R.id.ratingBarView).setRating(it) }


    }

}