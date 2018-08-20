package com.ubcoin.fragment.market

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.MarketItem
import com.ubcoin.model.response.TgLink
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.*
import com.ubcoin.view.rating.RatingBarView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_market_item_details.*
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Created by Yuriy Aizenberg
 */

class MarketDetailsFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var marketItem: MarketItem
    private lateinit var sliderLayout: SliderLayout
    private lateinit var pageIndicator: PagerIndicator
    private lateinit var fabActive: FloatingActionButton
    private lateinit var fabInactive: FloatingActionButton
    private lateinit var txtLocationDistance: TextView
    private lateinit var mapView: MapView
    private var idForRemove: String? = null
    private val isFavoriteProcessing = AtomicBoolean(false)

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
        txtLocationDistance = view.findViewById(R.id.txtLocationDistance)
        fabActive = view.findViewById(R.id.fabActive)
        fabInactive = view.findViewById(R.id.fabInactive)
        view.findViewById<View>(R.id.llHeaderLeftSimple).setOnClickListener { activity?.onBackPressed() }
        setFavorite(marketItem.favorite)

        /*fab.setOnClickListener {
            requestFavorite(!marketItem.favorite)
        }*/

        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)

        if (!CollectionExtensions.nullOrEmpty(marketItem.images)) {
            marketItem.images?.forEach {
                val textSliderView = SafetySliderView(activity!!, 0, metrics.widthPixels)
                textSliderView.scaleType = BaseSliderView.ScaleType.CenterCrop
                textSliderView.picasso = Picasso.get()
                textSliderView.image(it)
                textSliderView.description(null)
                textSliderView.error(R.drawable.img_photo_placeholder)
                sliderLayout.addSlider(textSliderView)
            }
            if (marketItem.images?.size == 1) {
                val ghostView = view.findViewById<View>(R.id.ghostView)
                ghostView.setOnClickListener { View.OnClickListener { } }
                ghostView.visibility = View.VISIBLE
                pageIndicator.visibility = View.GONE
            }
        } else {
            pageIndicator.visibility = View.GONE
            val textSliderView = SafetySliderView(activity!!, 0, metrics.widthPixels)
            textSliderView.scaleType = BaseSliderView.ScaleType.CenterCrop
            textSliderView.picasso = Picasso.get()
            textSliderView.image(R.drawable.img_photo_placeholder)
            sliderLayout.addSlider(textSliderView)
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
        if (avatarUrl == null) {
            imageView.setImageResource(R.drawable.img_profile_default)
        } else {
            Picasso.get().load(avatarUrl)
                    .resizeDimen(R.dimen.detailsSubProfileHeight, R.dimen.detailsSubProfileHeight)
                    .centerCrop()
                    .transform(CircleTransformation())
                    .placeholder(R.drawable.img_profile_default)
                    .error(R.drawable.img_profile_default)
                    .into(imageView)
        }
        view.findViewById<TextView>(R.id.txtUserName).text = user?.name

        user?.rating?.toInt()?.let { view.findViewById<RatingBarView>(R.id.ratingBarView).setRating(it) }

        val location = marketItem.location

        if (location != null) {
            val itemLocationLatLng = LatLng(location.latPoint?.toDouble()
                    ?: .0, location.longPoint?.toDouble() ?: .0)
            txtLocationDistance.text = DistanceUtils.calculateDistance(itemLocationLatLng, activity!!)
        } else {
            txtLocationDistance.text = null
        }

        mapView = view.findViewById(R.id.mapView)
        mapView.getMapAsync(this)
    }

    override fun onViewInflated(view: View, savedInstanceState: Bundle?) {
        super.onViewInflated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun processMapClick() {
        val location = marketItem.location
        if (location != null) {
            val latLng = LatLng(
                    location.latPoint?.toDouble() ?: .0,
                    location.longPoint?.toDouble() ?: .0)
            TheApplication.instance.openGeoMap(latLng.latitude, latLng.longitude, location.text)
        }
//        TheApplication.instance.openGeoMap()
    }

    override fun onMapReady(p0: GoogleMap?) {
        val location = marketItem.location
        if (location != null) {
            p0?.run {
                val latLng = LatLng(
                        location.latPoint?.toDouble() ?: .0,
                        location.longPoint?.toDouble() ?: .0)
                val markerOptions = MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                p0.addMarker(markerOptions)
                p0.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                setOnMapClickListener {
                    processMapClick()
                }
            }
        }
    }


    private fun callWantToBuy() {
        if (!ProfileHolder.isAuthorized()) {
            showNeedToRegistration()
            return
        }
        showProgressDialog("Wait please", "Wait please")
        DataProvider.getTgLink(marketItem.id, object : SilentConsumer<TgLink> {
            override fun onConsume(t: TgLink) {
                hideProgressDialog()
                val fullUrl = t.url
                if (fullUrl?.isNotBlank() == true) {
                    TheApplication.instance.openTelegramIntent(fullUrl)
                }
            }

        }, Consumer {
            handleException(it)
        })
    }

    private fun requestFavorite(favorite: Boolean) {
        if (isFavoriteProcessing.get()) return
        isFavoriteProcessing.set(true)
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
                isFavoriteProcessing.set(false)
            }

        }
    }

    override fun onBackPressed(): Boolean {
        TheApplication.instance.favoriteIdForRemove = idForRemove
        return super.onBackPressed()
    }

    override fun handleException(t: Throwable) {
        isFavoriteProcessing.set(false)
        hideProgressDialog()
        super.handleException(t)
    }

    private fun setFavorite(isFavorite: Boolean) {
       /* fab.backgroundTintList =
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
        fab.invalidate()*/


        if (!isFavorite) {
            fabActive.show()
            fabActive.visibility = View.VISIBLE
            fabActive.setOnClickListener {
                requestFavorite(!isFavorite)
            }

            fabInactive.hide()
            fabInactive.visibility = View.GONE
            fabInactive.setOnClickListener {  }
        } else {
            fabInactive.show()
            fabInactive.visibility = View.VISIBLE
            fabInactive.setOnClickListener {
                requestFavorite(!isFavorite)
            }

            fabActive.hide()
            fabActive.visibility = View.GONE
            fabActive.setOnClickListener {  }
        }
    }

}