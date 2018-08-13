package com.ubcoin.fragment.market

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.Indicators.PagerIndicator
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.squareup.picasso.Picasso
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.MarketItem
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.*
import com.ubcoin.view.rating.RatingBarView
import io.reactivex.functions.Consumer
import retrofit2.Response


/**
 * Created by Yuriy Aizenberg
 */

class MarketDetailsFragment : BaseFragment() {

    private lateinit var marketItem: MarketItem
    private lateinit var sliderLayout: SliderLayout
    private lateinit var pageIndicator: PagerIndicator
    private lateinit var fab: FloatingActionButton
    private var idForRemove: String? = null

    var header: View? = null

    companion object {
        fun getBundle(marketItem: MarketItem): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(MarketItem::class.java.simpleName, marketItem)
            return bundle
        }
    }

    override fun isFooterShow(): Boolean {
        super.isFooterShow()
        return false
    }

    override fun getLayoutResId() = R.layout.fragment_market_item_details

    @Suppress("NestedLambdaShadowedImplicitParameter")
    @SuppressLint("SetTextI18n")
    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        marketItem = arguments?.getSerializable(MarketItem::class.java.simpleName) as MarketItem
        sliderLayout = view.findViewById(R.id.slider)
        pageIndicator = view.findViewById(R.id.custom_indicator)
        fab = view.findViewById(R.id.fab)
        view.findViewById<View>(R.id.llHeaderLeftSimple).setOnClickListener { activity?.onBackPressed() }
        setFavorite(marketItem.favorite)

        fab.setOnClickListener {
            requestFavorite(!marketItem.favorite)
        }

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
                sliderLayout.addSlider(textSliderView)
            }
            if (marketItem.images?.size == 1) {
                val ghostView = view.findViewById<View>(R.id.ghostView)
                ghostView.setOnClickListener { View.OnClickListener { } }
                ghostView.visibility = View.VISIBLE
                pageIndicator.visibility = View.GONE
            }
        }
        sliderLayout.run {
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
        view.findViewById<View>(R.id.llWantToBuy).setOnClickListener { callWantToBuy() }

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

    private fun callWantToBuy() {
        if (!ProfileHolder.isAuthorized()) {
            showNeedToRegistration()
            return
        }
        showProgressDialog("Wait please", "Wait please")
        DataProvider.getTgLink(marketItem.id, object : SilentConsumer<String> {
            override fun onConsume(t: String) {
                hideProgressDialog()
                if (t.isNotBlank()) {
                    TheApplication.instance.openTelegramIntent(t)
                }
            }

        }, Consumer {
            handleException(it)
        })
    }

    private fun requestFavorite(favorite: Boolean) {
        showProgressDialog("Wait please", "Wait please")
        if (favorite) {
            DataProvider.favorite(marketItem.id, successHandler(), silentConsumer())
        } else {
            DataProvider.unfavorite(marketItem.id, successHandler(), silentConsumer())
        }
    }

    private fun silentConsumer(): SilentConsumer<Throwable> {
        return object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                handleException(t)
            }
        }
    }

    private fun successHandler(): SilentConsumer<Response<Unit>> {
        return object : SilentConsumer<Response<Unit>> {
            override fun onConsume(t: Response<Unit>) {
                hideProgressDialog()
                marketItem.favorite = !marketItem.favorite
                idForRemove = if (!marketItem.favorite) {
                    marketItem.id
                } else {
                    null
                }
                setFavorite(marketItem.favorite)
            }

        }
    }

    override fun onBackPressed(): Boolean {
        TheApplication.instance.favoriteIdForRemove = idForRemove
        return super.onBackPressed()
    }

    override fun handleException(t: Throwable) {
        hideProgressDialog()
        super.handleException(t)
    }

    private fun setFavorite(isFavorite: Boolean) {
        fab.backgroundTintList =
                if (isFavorite)
                    ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.greenMainColor))
                else
                    ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.white))

        val drawable =
                if (isFavorite)
                    ContextCompat.getDrawable(activity!!, R.drawable.ic_baseline_favorite_white)
                else
                    ContextCompat.getDrawable(activity!!, R.drawable.ic_baseline_favorite_green)

        fab.setImageDrawable(drawable)

    }

}