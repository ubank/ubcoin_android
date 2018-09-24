package com.ubcoin.fragment.market

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.Indicators.PagerIndicator
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.Transformers.BaseTransformer
import com.daimajia.slider.library.Transformers.DefaultTransformer
import com.google.android.gms.common.util.CollectionUtils
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
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.PurchaseUserAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.sell.ActionsDialogManager
import com.ubcoin.fragment.sell.MarketUpdateEvent
import com.ubcoin.fragment.sell.SellFragment
import com.ubcoin.model.FakePurchase
import com.ubcoin.model.IPurchaseObject
import com.ubcoin.model.Purchase
import com.ubcoin.model.PurchaseContainer
import com.ubcoin.model.response.MarketItem
import com.ubcoin.model.response.MarketItemStatus
import com.ubcoin.model.response.PurchaseItemStatus
import com.ubcoin.model.response.TgLink
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.network.request.BuyerPurchaseLinkRequest
import com.ubcoin.network.request.SellerPurchaseLinkRequest
import com.ubcoin.utils.*
import com.ubcoin.view.OpenTelegramDialogManager
import com.ubcoin.view.rating.RatingBarView
import io.reactivex.functions.Consumer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
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
    private lateinit var llWantToBuy: View
    private lateinit var imgDescription: View
    private lateinit var llDescription: View
    private lateinit var wantToBuyContainer: View

    private lateinit var llPurchasesContainer: View
    private lateinit var rvPurchases: RecyclerView

    private lateinit var txtItemCategory: TextView
    private lateinit var txtMarketProductName: TextView
    private lateinit var txtPriceInCurrency: TextView
    private lateinit var txtItemPrice: TextView
    private lateinit var txtActiveDealsCount: TextView

    private lateinit var txtUserName: TextView
    private lateinit var ratingBarView: RatingBarView
    private lateinit var imgSellerProfile: ImageView

    //Header
    private lateinit var imgHeaderLeft: ImageView
    private lateinit var header: View
    private lateinit var txtHeaderSimple: TextView
    private lateinit var imgHeaderRight: ImageView
    private lateinit var llHeaderRightSimple: View

    private lateinit var llHeaderRightMenu: View
    private lateinit var imgHeaderRightMenu: ImageView

    //Status
    private lateinit var llMarketItemStatus: View
    private lateinit var imgMarketItemStatus: ImageView
    private lateinit var txtMarketItemStatus: TextView


    private var itemPositionInList = -1

    private var idForRemove: String? = null
    private val isFavoriteProcessing = AtomicBoolean(false)

    private var onGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    companion object {
        fun getBundle(marketItem: MarketItem): Bundle {
            return getBundle(marketItem, -1)
        }

        fun getBundle(marketItem: MarketItem, position: Int): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(MarketItem::class.java.simpleName, marketItem)
            bundle.putInt("i", position)
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
        itemPositionInList = arguments?.getInt("i", -1) ?: -1
        llPurchasesContainer = view.findViewById(R.id.llPurchasesContainer)
        rvPurchases = view.findViewById(R.id.rvPurchases)

        txtItemCategory = view.findViewById(R.id.txtItemCategor)
        txtMarketProductName = view.findViewById(R.id.txtMarketProductName)
        txtPriceInCurrency = view.findViewById(R.id.txtPriceInCurrency)
        txtItemPrice = view.findViewById(R.id.txtItemPrice)

        imgSellerProfile = view.findViewById(R.id.imgSellerProfile)
        txtActiveDealsCount = view.findViewById(R.id.txtActiveDealsCount)

        txtUserName = view.findViewById(R.id.txtUserName)
        ratingBarView = view.findViewById(R.id.ratingBarView)

        llWantToBuy = view.findViewById<View>(R.id.llWantToBuy)
        imgDescription = view.findViewById(R.id.imgDescription)
        llDescription = view.findViewById<View>(R.id.llDescription)
        txtDescription = view.findViewById(R.id.txtMarketProductDescription)

        imgHeaderLeft = view.findViewById(R.id.imgHeaderLeft)
        imgHeaderRight = view.findViewById(R.id.imgHeaderRight)
        header = view.findViewById(R.id.header)
        txtHeaderSimple = view.findViewById(R.id.txtHeaderSimple)
        llHeaderRightSimple = view.findViewById(R.id.llHeaderRightSimple)
        llHeaderRightMenu = view.findViewById(R.id.llHeaderRightMenu)
        imgHeaderRightMenu = view.findViewById(R.id.imgHeaderRightMenu)

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
        if (marketItem.isOwner()) {
            hideFavorite()
        } else {
            setFavorite(marketItem.favorite)
        }

        mapView = view.findViewById(R.id.mapView)
        mapView.getMapAsync(this)
        installData()
    }

    private fun installData() {
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

        txtHeaderSimple.text = marketItem.title
        txtItemPrice.text = (marketItem.price ?: .0).moneyFormat() + " UBC"

        val itemsCount = marketItem.user?.itemsCount ?: 0
        txtActiveDealsCount.text = resources.getQuantityString(R.plurals.txt_active_deals_count, itemsCount,itemsCount)

        txtPriceInCurrency.text =
                if (marketItem.isPriceInCurrencyPresented()) "~" + marketItem.priceInCurrency!!.moneyRoundedFormat() + marketItem.currency else null

        txtItemCategory.text = marketItem.category?.name
        txtMarketProductName.text = marketItem.title
        val description = marketItem.description

        txtDescription.viewTreeObserver.addOnGlobalLayoutListener(getGlobalLayoutListener())
        txtDescription.text = description


        if (description == null || description.isBlank()) {
            llDescription.visibility = View.GONE
        }


        llWantToBuy.setOnClickListener {
            if (!ProfileHolder.isAuthorized()) {
                showNeedToRegistration()
            } else {
                val thePreferences = ThePreferences()
                if (thePreferences.shouldShowThDialog()) {
                    OpenTelegramDialogManager.showDialog(activity!!, object : OpenTelegramDialogManager.ITelegramDialogCallback {
                        override fun onPositiveClick(materialDialog: MaterialDialog) {
                            materialDialog.dismiss()
                            thePreferences.disableTgDialog()
                            callWantToBuy(marketItem.id, true)
                        }
                    })
                } else {
                    callWantToBuy(marketItem.id, true)
                }
            }

        }


        val user = marketItem.user
        val avatarUrl = user?.avatarUrl
        if (avatarUrl == null) {
            imgSellerProfile.setImageResource(R.drawable.img_profile_default)
        } else {
            GlideApp.with(activity!!).load(avatarUrl)
                    .override(R.dimen.detailsSubProfileHeight, R.dimen.detailsSubProfileHeight)
                    .centerInside()
                    .transform(RoundedCorners(context!!.resources.getDimensionPixelSize(R.dimen.detailsSubProfileHeight)))
                    .placeholder(R.drawable.img_profile_default)
                    .error(R.drawable.img_profile_default)
                    .into(imgSellerProfile)
        }
        txtUserName.text = user?.name

        user?.rating?.toInt()?.let { ratingBarView.setRating(it) }

        val location = marketItem.location

        if (location != null) {
            val itemLocationLatLng = LatLng(location.latPoint?.toDouble()
                    ?: .0, location.longPoint?.toDouble() ?: .0)
            txtLocationDistance.text = DistanceUtils.calculateDistance(itemLocationLatLng, activity!!)
        } else {
            txtLocationDistance.text = null
        }



        if (!ProfileHolder.isAuthorized()) {
            fabInactive.hide()
            fabActive.hide()
        }

        llHeaderRightSimple.setOnClickListener {
            val shareUrl = marketItem.shareUrl
            if (shareUrl != null && !shareUrl.isBlank()) {
                TheApplication.instance.openShareIntent(shareUrl, activity!!)
            }
        }

        setOwnerDependData()
    }

    fun setOwnerDependData() {
        if (marketItem.isOwner()) {
            wantToBuyContainer.visibility = View.GONE
            rvPurchases.visibility = View.VISIBLE
            checkMarketItemStatus()
            setupPurchases()
        } else {
            wantToBuyContainer.visibility = View.VISIBLE
            rvPurchases.visibility = View.GONE
        }



        setupActions()
    }

    private fun setupActions() {
        if (!marketItem.isOwner() || marketItem.status?.whatCanOwnerDo() == MarketItemStatus.DoActions.NOTHING) {
            llHeaderRightMenu.visibility = View.GONE
            llHeaderRightMenu.setOnClickListener { }
        } else {
            llHeaderRightMenu.visibility = View.VISIBLE
            llHeaderRightMenu.setOnClickListener {
                ActionsDialogManager.show(activity!!, marketItem.status?.whatCanOwnerDo()
                        ?: MarketItemStatus.DoActions.NOTHING)
                { items: ActionsDialogManager.Items, dialog: Dialog ->
                    dialog.dismiss()
                    when (items) {
                        ActionsDialogManager.Items.EDIT -> {
                            getSwitcher()?.addTo(SellFragment::class.java, SellFragment.getBundle(marketItem), false)
                        }
                        ActionsDialogManager.Items.ACTIVATE -> {
                            toggleState(true)
                        }
                        ActionsDialogManager.Items.DEACTIVATE -> {
                            toggleState(false)
                        }
                    }
                }
            }
        }
    }


    private fun toggleState(activate: Boolean) {
        val onSuccess = Consumer<MarketItem> { t ->
            hideProgressDialog()
            activity?.let {
                if (activate) {
                    Toast.makeText(it, R.string.the_listining_is_activated, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(it, R.string.the_listining_is_deactivated, Toast.LENGTH_SHORT).show()
                }
            }
            EventBus.getDefault().post(UpdateMarketStateItemEvent(t))
            marketItem.status = t?.status
            setOwnerDependData()
        }

        showProgressDialog(R.string.wait_please_title, R.string.wait_please_message)
        if (activate) {
            DataProvider.activate(marketItem.id, onSuccess, silentConsumer())
        } else {
            DataProvider.deactivate(marketItem.id, onSuccess, silentConsumer())
        }
    }

    private fun setupPurchases() {
        if (!CollectionUtils.isEmpty(marketItem.purchases)) {
            val purchaseUserAdapter = PurchaseUserAdapter(activity!!)
            purchaseUserAdapter.setHasStableIds(true)

            rvPurchases.setHasFixedSize(true)
            rvPurchases.layoutManager = LinearLayoutManager(activity!!)

            rvPurchases.adapter = purchaseUserAdapter

            val purchaseContainer = PurchaseContainer()

            marketItem.purchases.forEach {
                when (it.status) {
                    PurchaseItemStatus.ACTIVE, PurchaseItemStatus.CREATED -> {
                        purchaseContainer.activePurchases.add(it)
                    }
                    else -> {
                        purchaseContainer.otherPurchases.add(it)
                    }
                }
            }
            val list = ArrayList<IPurchaseObject>()
            if (purchaseContainer.shouldDivideByBlocks()) {
                list.add(FakePurchase(getString(R.string.str_item_status_reserved)))
                list.addAll(purchaseContainer.activePurchases)

                list.add(FakePurchase(getString(R.string.str_others)))
                list.addAll(purchaseContainer.otherPurchases)
            } else {
                list.addAll(purchaseContainer.activePurchases)
                list.addAll(purchaseContainer.otherPurchases)
            }
            if (!list.isEmpty()) {
                purchaseUserAdapter.addData(list)
            }
            purchaseUserAdapter.recyclerTouchListener = object : IRecyclerTouchListener<IPurchaseObject> {
                override fun onItemClick(data: IPurchaseObject, position: Int) {
                    val purchase = data as Purchase
                    val thePreferences = ThePreferences()
                    if (thePreferences.shouldShowThDialog()) {
                        OpenTelegramDialogManager.showDialog(activity!!, object : OpenTelegramDialogManager.ITelegramDialogCallback {
                            override fun onPositiveClick(materialDialog: MaterialDialog) {
                                materialDialog.dismiss()
                                thePreferences.disableTgDialog()
                                callWantToBuy(purchase.id, false)
                            }
                        })
                    } else {
                        callWantToBuy(purchase.id, false)
                    }
                }

            }
        }
    }

    private fun checkMarketItemStatus() {
        if (marketItem.status != null) {
            when (marketItem.status) {
                MarketItemStatus.DEACTIVATED -> {
                    llMarketItemStatus.visibility = View.VISIBLE
                    llMarketItemStatus.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.itemStatusDeactivatedTransparent))

                    txtMarketItemStatus.text = getString(marketItem.status!!.description)
                    txtMarketItemStatus.setTextColor(ContextCompat.getColor(activity!!, R.color.itemStatusDeactivated))

                    imgMarketItemStatus.setImageResource(R.drawable.ic_deact)
                }


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

                    spannableString.setSpan(clickableSpan, firstPartString.length, firstPartString.length + clickablePart.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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
        imgHeaderRightMenu.setImageResource(R.drawable.ic_menu_black)
    }

    private fun onExpand() {
        txtHeaderSimple.setTextColor(Color.WHITE)
//        header.setBackgroundColor(Color.parseColor("#62000000"))
        header.setBackgroundColor(Color.TRANSPARENT)
        imgHeaderRight.setImageResource(R.drawable.ic_share_details_white)
        imgHeaderLeft.setImageResource(R.drawable.ic_back_details_white)
        imgHeaderRightMenu.setImageResource(R.drawable.ic_menu_white)
    }

    override fun onViewInflated(view: View, savedInstanceState: Bundle?) {
        super.onViewInflated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        mapView.onResume()
    }

    override fun onPause() {
        EventBus.getDefault().unregister(this)
        super.onPause()
    }

    @Subscribe
    fun onMarketUpdateEvent(marketUpdateEvent: MarketUpdateEvent) {
        if (marketItem.id == marketUpdateEvent.marketItem.id) {
            marketItem = marketUpdateEvent.marketItem
            installData()
        }
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


    private fun callWantToBuy(id: String, fromBuyer: Boolean) {
        showProgressDialog(R.string.wait_please_title, R.string.wait_please_message)
        DataProvider.discuss(if (fromBuyer) BuyerPurchaseLinkRequest(marketItem.id) else SellerPurchaseLinkRequest(id), object : SilentConsumer<TgLink> {
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
                if (itemPositionInList != -1) {
                    EventBus.getDefault().post(UpdateMarketItemEvent(position = itemPositionInList, isFavorite = marketItem.favorite))
                }
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
    private fun hideFavorite() {
        fabActive.show()
        fabActive.visibility = View.GONE
        fabActive.setOnClickListener {
        }
        fabInactive.hide()
        fabInactive.visibility = View.GONE
        fabInactive.setOnClickListener { }

    }

    @SuppressLint("RestrictedApi")
    private fun setFavorite(isFavorite: Boolean) {
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