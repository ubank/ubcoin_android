package com.ubcoin.fragment.market

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.Indicators.PagerIndicator
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.Transformers.BaseTransformer
import com.daimajia.slider.library.Transformers.DefaultTransformer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.ThePreferences
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.MarketItem
import com.ubcoin.model.response.MarketItemStatus
import com.ubcoin.model.response.TgLink
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.*
import com.ubcoin.view.OpenTelegramDialogManager
import com.ubcoin.view.rating.RatingBarView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_stub.view.*
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
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var txtDescription: TextView
    private lateinit var imgDescription: View
    private lateinit var llDescription: View
    private lateinit var wantToBuyContainer: View

    //Header
    private lateinit var imgHeaderLeft: ImageView
    private lateinit var header: View
    private lateinit var txtHeaderSimple: TextView
    private lateinit var imgHeaderRight: ImageView

    //Status
    private lateinit var llMarketItemStatus: View
    private lateinit var imgMarketItemStatus: ImageView
    private lateinit var txtMarketItemStatus: TextView

    private var idForRemove: String? = null
    private val isFavoriteProcessing = AtomicBoolean(false)

    private var onGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

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

        imgHeaderLeft = view.findViewById(R.id.imgHeaderLeft)
        imgHeaderRight = view.findViewById(R.id.imgHeaderRight)
        header = view.findViewById(R.id.header)
        txtHeaderSimple = view.findViewById(R.id.txtHeaderSimple)

        wantToBuyContainer = view.findViewById(R.id.wantToBuyContainer)

        llMarketItemStatus = view.findViewById(R.id.llMarketItemStatus)
        txtMarketItemStatus = view.findViewById(R.id.txtMarketItemStatus)
        imgMarketItemStatus = view.findViewById(R.id.imgMarketItemStatus)

        sliderLayout = view.findViewById(R.id.slider)
        sliderLayout.setPagerTransformer(false, DefaultTransformer())
        pageIndicator = view.findViewById(R.id.custom_indicator)
        txtLocationDistance = view.findViewById(R.id.txtLocationDistance)
        fabActive = view.findViewById(R.id.fabActive)
        fabInactive = view.findViewById(R.id.fabInactive)
        appBarLayout = view.findViewById(R.id.appBar)
        appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                when (state) {
                    AppBarStateChangeListener.State.EXPANDED -> {
                        onExpand()
                    }
                    AppBarStateChangeListener.State.COLLAPSED -> {
                        onCollapse()
                    }
                    AppBarStateChangeListener.State.IDLE -> {
                        onExpand()
                    }
                }
            }

        })
        view.findViewById<View>(R.id.llHeaderLeftSimple).setOnClickListener { activity?.onBackPressed() }
        setFavorite(marketItem.favorite)

        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)

        if (!CollectionExtensions.nullOrEmpty(marketItem.images)) {
            marketItem.images?.forEach {
                val textSliderView = SafetySliderView(activity!!, 0, metrics.widthPixels)
//                textSliderView.scaleType = BaseSliderView.ScaleType.CenterCrop
                textSliderView.scaleType = BaseSliderView.ScaleType.FitCenterCrop
                textSliderView.picasso = Picasso.get()
                textSliderView.image(it)
                textSliderView.description(null)
                textSliderView.error(R.drawable.img_photo_placeholder)
                textSliderView.onClickListener = createClickListener(it)
                sliderLayout.addSlider(textSliderView)
            }
            if (marketItem.images?.size == 1) {
                sliderLayout.setPagerTransformer(false, object : BaseTransformer() {
                    override fun onTransform(view: View?, position: Float) {

                    }

                })
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
            setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
            setCustomAnimation(DescriptionAnimation())
            stopAutoCycle()
        }

        view.findViewById<TextView>(R.id.txtHeaderSimple).text = marketItem.title
        view.findViewById<TextView>(R.id.txtItemPrice).text = (marketItem.price
                ?: .0).moneyFormat() + " UBC"
        view.findViewById<TextView>(R.id.txtItemCategor).text = marketItem.category?.name
        view.findViewById<TextView>(R.id.txtMarketProductName).text = marketItem.title
        val description = marketItem.description
        txtDescription = view.findViewById(R.id.txtMarketProductDescription)
        txtDescription.viewTreeObserver.addOnGlobalLayoutListener(getGlobalLayoutListener())
        txtDescription.text = description

        llDescription = view.findViewById<View>(R.id.llDescription)
        if (description == null || description.isBlank()) {
            llDescription.visibility = View.GONE
        }
        imgDescription = view.findViewById(R.id.imgDescription)

        view.findViewById<View>(R.id.llWantToBuy).setOnClickListener {
            if (!ProfileHolder.isAuthorized()) {
                showNeedToRegistration()
            } else {
                val thePreferences = ThePreferences()
                if (thePreferences.shouldShowThDialog()) {
                    OpenTelegramDialogManager.showDialog(activity!!, object : OpenTelegramDialogManager.ITelegramDialogCallback {
                        override fun onPositiveClick(materialDialog: MaterialDialog) {
                            materialDialog.dismiss()
                            thePreferences.disableTgDialog()
                            callWantToBuy()
                        }
                    })
                } else {
                    callWantToBuy()
                }
            }

        }

        val imageView = view.findViewById<ImageView>(R.id.imgSellerProfile)
        val user = marketItem.user
        val avatarUrl = user?.avatarUrl
        if (avatarUrl == null) {
            imageView.setImageResource(R.drawable.img_profile_default)
        } else {
            GlideApp.with(activity!!).load(avatarUrl)
                    .override(R.dimen.detailsSubProfileHeight, R.dimen.detailsSubProfileHeight)
                    .centerInside()
                    .transform(RoundedCorners(context!!.resources.getDimensionPixelSize(R.dimen.detailsSubProfileHeight)))
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

        if (!ProfileHolder.isAuthorized()) {
            fabInactive.hide()
            fabActive.hide()
        }

        view.findViewById<View>(R.id.llHeaderRightSimple).setOnClickListener {
            val shareUrl = marketItem.shareUrl
            if (shareUrl != null && !shareUrl.isBlank()) {
                TheApplication.instance.openShareIntent(shareUrl, activity!!)
            }
        }

        if (marketItem.isOwner()) {
            wantToBuyContainer.visibility = View.GONE
            checkMarketItemStatus()
        } else {
            wantToBuyContainer.visibility = View.VISIBLE
        }
    }

    private fun checkMarketItemStatus() {
        if (marketItem.status != null) {
            when (marketItem.status) {
                MarketItemStatus.CHECK, MarketItemStatus.CHECKING -> {
                    llMarketItemStatus.visibility = View.VISIBLE
                    llMarketItemStatus.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.itemStatusCheckTransparent))

                    txtMarketItemStatus.text = getString(marketItem.status!!.description)
                    txtMarketItemStatus.setTextColor(ContextCompat.getColor(activity!!, R.color.itemStatusCheck))

                    imgMarketItemStatus.setImageResource(R.drawable.ic_market_item_moderation)
                }
                MarketItemStatus.BLOCKED -> {
                    llMarketItemStatus.visibility = View.VISIBLE
                    llMarketItemStatus.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.itemStatusBlockTransparent))
                    txtMarketItemStatus.setTextColor(ContextCompat.getColor(activity!!, R.color.itemStatusBlock))
                    imgMarketItemStatus.setImageResource(R.drawable.ic_market_item_blocked)

                    val firstPartString = getString(R.string.str_status_blocked1)
                    val clickablePart = getString(R.string.str_status_blockedClickable)
                    val secondPartString = getString(R.string.str_status_blocked2)



                    val spannableString = SpannableString(firstPartString + clickablePart + secondPartString)
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            showUserAgreement()
                        }
                    }

                    spannableString.setSpan(clickableSpan, firstPartString.length - 1, firstPartString.length + clickablePart.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    txtMarketItemStatus.movementMethod = LinkMovementMethod.getInstance()
                    txtMarketItemStatus.text = spannableString

                }
                else -> {
                    llMarketItemStatus.visibility = View.GONE
                }
            }
        }
    }

    private fun getGlobalLayoutListener(): ViewTreeObserver.OnGlobalLayoutListener {
        if (onGlobalLayoutListener == null) {
            onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
                val layout = txtDescription.layout
                val lineCount = layout?.lineCount ?: 0
                if (lineCount > 0) {
                    if (layout.getEllipsisCount(lineCount - 1) > 0) {
                        showExtendDescription()
                    } else {
                        hideExpandDescription()
                    }
                    txtDescription.viewTreeObserver.removeOnGlobalLayoutListener { getGlobalLayoutListener() }
                }
            }
        }
        return onGlobalLayoutListener as ViewTreeObserver.OnGlobalLayoutListener
    }

    private fun showExtendDescription() {
        imgDescription.visibility = View.VISIBLE
        llDescription.setOnClickListener {
            MaterialDialog.Builder(activity!!)
                    .content(marketItem.description!!)
                    .build()
                    .show()
        }
    }

    private fun hideExpandDescription() {
        imgDescription.visibility = View.GONE
        llDescription.setOnClickListener { }

    }

    private fun createClickListener(filePath: String): SafetySliderView.ClickListener {
        return object : SafetySliderView.ClickListener(filePath) {
            override fun onClick(filePath: String) {
                openFullScreenImage(filePath)
            }

        }
    }

    private fun openFullScreenImage(filePath: String) {
        getSwitcher()?.addTo(FullImageFragment::class.java, FullImageFragment.getBundle(filePath), true)
    }

    private fun onCollapse() {
        txtHeaderSimple.setTextColor(Color.BLACK)
        header.setBackgroundColor(Color.WHITE)
        imgHeaderRight.setImageResource(R.drawable.ic_share_details_black)
        imgHeaderLeft.setImageResource(R.drawable.ic_back_details_black)
    }

    private fun onExpand() {
        txtHeaderSimple.setTextColor(Color.WHITE)
//        header.setBackgroundColor(Color.parseColor("#62000000"))
        header.setBackgroundColor(Color.TRANSPARENT)
        imgHeaderRight.setImageResource(R.drawable.ic_share_details_white)
        imgHeaderLeft.setImageResource(R.drawable.ic_back_details_white)
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
        showProgressDialog(R.string.wait_please_title, R.string.wait_please_message)
        DataProvider.discuss(marketItem.id, object : SilentConsumer<TgLink> {
            override fun onConsume(t: TgLink) {
                hideProgressDialog()
                val fullUrl = t.url
                if (fullUrl.isNotBlank()) {
                    TheApplication.instance.openTelegramIntent(fullUrl, t.appUrl, this@MarketDetailsFragment, 17777)
                }
            }

        }, Consumer {
            handleException(it)
        })
    }

    private fun requestFavorite(favorite: Boolean) {
        if (isFavoriteProcessing.get()) return
        isFavoriteProcessing.set(true)
        showProgressDialog(R.string.wait_please_title, R.string.wait_please_message)
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

    @SuppressLint("RestrictedApi")
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
            fabInactive.setOnClickListener { }
        } else {
            fabInactive.show()
            fabInactive.visibility = View.VISIBLE
            fabInactive.setOnClickListener {
                requestFavorite(!isFavorite)
            }

            fabActive.hide()
            fabActive.visibility = View.GONE
            fabActive.setOnClickListener { }
        }
    }

}