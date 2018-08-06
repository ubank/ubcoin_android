package com.ubcoin.fragment.market

import android.os.Bundle
import android.view.View
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.Indicators.PagerIndicator
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.TextSliderView
import com.squareup.picasso.Picasso
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.MarketItem
import com.ubcoin.utils.CollectionExtensions
import com.ubcoin.utils.SafetySliderView

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

        if (!CollectionExtensions.nullOrEmpty(marketItem.images)) {
            marketItem.images.forEach {
                val textSliderView = SafetySliderView(activity!!)
                textSliderView.picasso = Picasso.get()
                textSliderView.image(it)
                textSliderView.description(null)
                textSliderView.error(R.drawable.img_rejected)
                sliderLayout?.addSlider(textSliderView)

            }
        }
        sliderLayout?.run {
            setPresetTransformer(SliderLayout.Transformer.Accordion)
            setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
            setCustomAnimation(DescriptionAnimation())
            stopAutoCycle()
        }


    }

}