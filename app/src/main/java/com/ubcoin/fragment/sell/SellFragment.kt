package com.ubcoin.fragment.sell

import android.location.Address
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.cocosw.bottomsheet.BottomSheet
import com.crashlytics.android.Crashlytics
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.ILocationChangeCallback
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.ThePreferences
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.SellImagesAdapter
import com.ubcoin.fragment.FirstLineFragment
import com.ubcoin.model.SellImageModel
import com.ubcoin.model.response.Location
import com.ubcoin.model.response.TgLink
import com.ubcoin.model.response.TgLinks
import com.ubcoin.model.response.base.IdResponse
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.network.request.CreateProductRequest
import com.ubcoin.utils.MaxValueInputFilter
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.utils.SellCreateDataHolder
import com.ubcoin.utils.moneyFormat
import com.ubcoin.view.OpenTelegramDialogManager
import com.ubcoin.view.RefreshableEditText
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import java.text.DecimalFormat

/**
 * Created by Yuriy Aizenberg
 */
class SellFragment : FirstLineFragment(), IRecyclerTouchListener<SellImageModel>, OnMapReadyCallback, ILocationChangeCallback {

    private val requestCode = 19990
    private var disposable: Disposable? = null
    private var isConversionInProgress = false


    private val datum = ArrayList<SellImageModel>()
    private lateinit var adapter: SellImagesAdapter
    private var bottomSheet: BottomSheet? = null
    private var fromCamera = false
    private var googleMap: GoogleMap? = null
    private var currentPriceInUBC: Double = .0
    private var currentPriceInUSD: Double = .0
    private var marker: Marker? = null

    private lateinit var mapView: MapView
    private lateinit var edtSellTitle: MaterialEditText
    private lateinit var edtSellCategory: MaterialEditText
    private lateinit var edtSellDescription: MaterialEditText
    private lateinit var txtSellLocation: TextView
    private lateinit var llSellLocation: View
    private lateinit var btnSellDone: View

    private lateinit var refreshViewUbc: RefreshableEditText
    private lateinit var refreshViewUsd: RefreshableEditText


    init {
        (0..4).forEach { datum.add(SellImageModel()) }
    }

    override fun getLayoutResId() = R.layout.fragment_sell

    override fun isFooterShow() = false

    override fun onViewInflated(view: View, savedInstanceState: Bundle?) {
        super.onViewInflated(view, savedInstanceState)
        SellCreateDataHolder.reset()

        refreshViewUbc = view.findViewById(R.id.refreshViewUbc)
        refreshViewUsd = view.findViewById(R.id.refreshViewUsd)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvSellImages)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)

        adapter = SellImagesAdapter(activity!!)
        adapter.setHasStableIds(true)

        recyclerView.adapter = adapter
        adapter.data = datum

        adapter.recyclerTouchListener = this
        mapView = view.findViewById(R.id.mapView)
        edtSellTitle = view.findViewById(R.id.edtSellTitle)
        edtSellCategory = view.findViewById(R.id.edtSellCategory)
        edtSellDescription = view.findViewById(R.id.edtSellDescription)
        txtSellLocation = view.findViewById(R.id.txtSellLocation)
        llSellLocation = view.findViewById(R.id.llSellLocation)
        btnSellDone = view.findViewById(R.id.btnSellDone)

        mapView.getMapAsync(this)
        mapView.onCreate(savedInstanceState)

        setupData()


        view.findViewById<View>(R.id.llCategoryContainer).setOnTouchListener { _, motionEvent ->
            if (motionEvent!!.action == MotionEvent.ACTION_UP) {
                getSwitcher()?.addTo(SelectCategoryFragment::class.java, SelectCategoryFragment.createBundle(SellCreateDataHolder.category?.id), true)
            }
            true
        }

        refreshViewUsd.refreshListener = object : RefreshableEditText.IRefreshListener {
            override fun onViewClick() {
                openPriceDialog(false)
            }

            override fun onRefreshClick() {
                setCurrentPrice(true)
            }
        }

        refreshViewUbc.refreshListener = object : RefreshableEditText.IRefreshListener {
            override fun onViewClick() {
                openPriceDialog(true)
            }

            override fun onRefreshClick() {
                setCurrentPrice(false)
            }

        }

        llSellLocation.setOnClickListener {
            getSwitcher()?.addTo(SelectLocationFragment::class.java)
        }

        btnSellDone.setOnClickListener {
            val validateDataAndCreateRequest = validateDataAndCreateRequest()
            if (validateDataAndCreateRequest != null) {
                hideKeyboard()
                if (validateTgUser()) {
                    loadImagesAndCreate(validateDataAndCreateRequest)
                }
            }
        }
    }

    override fun onLatLngChanged(latLng: LatLng) {
        TheApplication.instance.unregisterLatLngCallback(this)
        fetchCurrentLocationAddress(latLng)
    }


    override fun onResume() {
        super.onResume()
        val location = SellCreateDataHolder.location
        if (location == null) {
            val currentLocation = TheApplication.instance.currentLocation
            if (currentLocation == null) {
                TheApplication.instance.registerLatLngCallback(this)
            } else {
                fetchCurrentLocationAddress(currentLocation)
            }
        }
        if (SellCreateDataHolder.hasChanges) {
            setupData()
        }
        mapView.onResume()
    }

    private fun fetchCurrentLocationAddress(currentLocation: LatLng) {
        disposable?.dispose()
        activity?.run {
            disposable = DataProvider.resolveLocation(this,
                    currentLocation.latitude,
                    currentLocation.longitude,
                    object : SilentConsumer<List<Address>> {
                        override fun onConsume(t: List<Address>) {
                            onLocationResolved(t, currentLocation)
                        }

                    }, object : SilentConsumer<Throwable> {
                override fun onConsume(t: Throwable) {
                    Crashlytics.logException(t)
                }

            })
        }
    }

    private fun onLocationResolved(t: List<Address>, latLng: LatLng) {
        if (SellCreateDataHolder.location != null) return

        if (!t.isEmpty()) {
            val address = t[0]
            val addressBuilder = StringBuilder()
            (0..address.maxAddressLineIndex).forEach {
                addressBuilder.append(address.getAddressLine(it))
                if (it < address.maxAddressLineIndex) {
                    addressBuilder.append(",")
                }
            }
            SellCreateDataHolder.location = Location(addressBuilder.toString(), latLng.latitude.toString(), latLng.longitude.toString())
            setupData()
        }
    }

    override fun onPause() {
        super.onPause()
        TheApplication.instance.unregisterLatLngCallback(this)
        disposable?.dispose()
    }

    private fun validateTgUser(): Boolean {
        val authorizedInTg = ProfileHolder.user!!.authorizedInTg ?: false
        if (!authorizedInTg) {
            showProgressDialog(R.string.loading, R.string.wait_please_message)
            DataProvider.getTgLink(object : SilentConsumer<TgLink> {
                override fun onConsume(t: TgLink) {
                    hideProgressDialog()
                    ProfileHolder.user!!.authorizedInTg = t.user?.authorizedInTg ?: false
                    val preferences = ThePreferences()
                    if (preferences.shouldShowThDialog()) {
                        activity?.run {
                            OpenTelegramDialogManager.showDialog(this, object : OpenTelegramDialogManager.ITelegramDialogCallback {
                                override fun onPositiveClick(materialDialog: MaterialDialog) {
                                    materialDialog.dismiss()
                                    preferences.disableTgDialog()
                                    TheApplication.instance.openTelegramIntent(t.url, t.appUrl, this@SellFragment, requestCode)
                                }
                            })
                        }
                    } else {
                        TheApplication.instance.openTelegramIntent(t.url, t.appUrl, this@SellFragment, requestCode)
                    }
                }

            }, object : SilentConsumer<Throwable> {
                override fun onConsume(t: Throwable) {
                    hideProgressDialog()
                    handleException(t)
                }
            })
        }
        return authorizedInTg
    }

    private fun loadImagesAndCreate(createProductRequest: CreateProductRequest) {
        showProgressDialog(R.string.create_product_progress, R.string.wait_please_message)
        val imageUrls = ArrayList<String>()
        datum.forEach {
            if (it.filePath != null) {
                imageUrls.add(it.filePath!!)
            }
        }
        if (!imageUrls.isEmpty()) {
            DataProvider.uploadFiles(imageUrls,
                    object : SilentConsumer<TgLinks> {
                        override fun onConsume(t: TgLinks) {
                            imageUrls.clear()
                            t.tgLinks.forEach {
                                imageUrls.add(it.url)
                            }
                            afterImagesLoaded(createProductRequest, imageUrls)
                        }
                    },
                    object : SilentConsumer<Throwable> {
                        override fun onConsume(t: Throwable) {
                            handleException(t)
                        }
                    })
        } else {
            afterImagesLoaded(createProductRequest, imageUrls)
        }
    }

    private fun afterImagesLoaded(createProductRequest: CreateProductRequest, images: List<String>) {
        createProductRequest.images = images

        DataProvider.createProduct(createProductRequest,
                object : SilentConsumer<IdResponse> {
                    override fun onConsume(t: IdResponse) {
                        hideProgressDialog()
                        activity?.onBackPressed()
                        getSwitcher()?.addTo(SellInModerationDetails::class.java, true, false)
                    }

                },
                object : SilentConsumer<Throwable> {
                    override fun onConsume(t: Throwable) {
                        handleException(t)
                    }

                })
    }

    override fun handleException(t: Throwable) {
        hideProgressDialog()
        super.handleException(t)
    }

    private fun validateDataAndCreateRequest(): CreateProductRequest? {
        val title = edtSellTitle.text.toString()
        if (title.isBlank()) {
            showSweetAlertDialog(R.string.error, R.string.err_title_missing)
            return null
        }
        if (currentPriceInUSD <= 0f) {
            showSweetAlertDialog(R.string.error, R.string.err_price_required)
            return null
        }

        val categoryId = SellCreateDataHolder.category?.id
        if (categoryId == null) {
            showSweetAlertDialog(R.string.error, R.string.err_category_missing)
            return null
        }

        val location = SellCreateDataHolder.location
        if (location == null || !location.isAddressPresented()) {
            showSweetAlertDialog(R.string.error, R.string.err_location_missing)
            return null
        }

        if (!hasImages()) {
            showSweetAlertDialog(R.string.error, R.string.err_image_missing)
            return null
        }

        return CreateProductRequest(
                categoryId,
                title,
                edtSellDescription.text.toString().trim(),
                currentPriceInUSD,
                location,
                true, true, ArrayList()
        )
    }

    private fun hasImages(): Boolean {
        datum.forEach {
            if (it.hasImage()) return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun setupData() {
        val category = SellCreateDataHolder.category
        if (category == null) {
            edtSellCategory.text = null
        } else {
            edtSellCategory.setText(category.name)
        }
        val location = SellCreateDataHolder.location
        if (location == null) {
            txtSellLocation.text = getString(R.string.location_label)
        } else {
            marker?.remove()
            txtSellLocation.text = location.text
            if (location.isAddressPresented() && googleMap != null) {
                val latLng = LatLng(
                        location.latPoint!!.toDouble(),
                        location.longPoint!!.toDouble())
                val markerOptions = MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                marker = googleMap!!.addMarker(markerOptions)
                googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f))
            }
        }
        if (currentPriceInUBC > .0f) {
            setCurrentPrice(true)
        }
    }

    private fun openPriceDialog(shouldConvertToUSD: Boolean) {
        val materialDialog = MaterialDialog.Builder(activity!!)
                .customView(R.layout.fragment_content_select_price, false)
                .build()
        val edtPrice: MaterialEditText = materialDialog.findViewById(R.id.edtPrice) as MaterialEditText

        materialDialog.findViewById(R.id.btnDialogCancel).setOnClickListener {
            materialDialog.dismiss()
        }

        materialDialog.findViewById(R.id.btnDialogDone).setOnClickListener {
            materialDialog.dismiss()
            val value = try {
                edtPrice.text.toString().toDouble()
            } catch (e: Exception) {
                .0
            }
            if (shouldConvertToUSD) currentPriceInUBC = value else currentPriceInUSD = value
            setCurrentPrice(shouldConvertToUSD)

        }
        val decimalFormat = DecimalFormat()
        decimalFormat.maximumFractionDigits = 4
        var format = ""
        if (shouldConvertToUSD && currentPriceInUBC > 0f) {
            format = decimalFormat.format(currentPriceInUBC)
        } else if (!shouldConvertToUSD && currentPriceInUSD > 0f) {
            format = decimalFormat.format(currentPriceInUSD)
        }
        format = java.lang.String(format).replaceAll("\\s+", "").replace(",", ".")
        edtPrice.setText(format)
        edtPrice.filters += MaxValueInputFilter()
        materialDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        materialDialog.show()
    }

    private fun setCurrentPrice(shouldConvertToUSD: Boolean) {
        if (shouldConvertToUSD) {
            if (currentPriceInUBC <= .0f) {
                refreshViewUbc.stopRefreshAndSetValue("")
                refreshViewUsd.stopRefreshAndSetValue("")
                return
            }
            refreshViewUbc.stopRefreshAndSetValue(getFormattedPrice(true))
            refreshViewUsd.setRefreshState(RefreshableEditText.RefreshState.REFRESH_IN_PROGRESS)
            isConversionInProgress = true
            DataProvider.getConversionFromUBC(currentPriceInUBC.toString(), Consumer { t ->
                isConversionInProgress = false
                if (t == null) {
                    refreshViewUsd.setRefreshState(RefreshableEditText.RefreshState.REFRESH_FAILURE)
                } else {
                    currentPriceInUSD = t.amount
                    refreshViewUsd.stopRefreshAndSetValue(getFormattedPrice(false))
                }
            }, Consumer { t ->
                isConversionInProgress = false
                Crashlytics.logException(t)
                refreshViewUsd.setRefreshState(RefreshableEditText.RefreshState.REFRESH_FAILURE)
            })
        } else {
            if (currentPriceInUSD <= .0) {
                currentPriceInUBC = .0
                currentPriceInUSD = .0
                refreshViewUbc.stopRefreshAndSetValue("")
                refreshViewUsd.stopRefreshAndSetValue("")
                return
            }
            isConversionInProgress = true
            refreshViewUsd.stopRefreshAndSetValue(getFormattedPrice(false))
            refreshViewUbc.setRefreshState(RefreshableEditText.RefreshState.REFRESH_IN_PROGRESS)
            DataProvider.getConversionFromUSD(currentPriceInUSD.toString(), Consumer { t ->
                isConversionInProgress = false
                if (t == null) {
                    refreshViewUbc.setRefreshState(RefreshableEditText.RefreshState.REFRESH_FAILURE)
                } else {
                    currentPriceInUBC = t.amount
                    refreshViewUbc.stopRefreshAndSetValue(getFormattedPrice(true))
                }
            }, Consumer { t ->
                isConversionInProgress = false
                Crashlytics.logException(t)
                refreshViewUbc.setRefreshState(RefreshableEditText.RefreshState.REFRESH_FAILURE)
            })
        }
    }

    override fun onItemClick(data: SellImageModel, position: Int) {
        bottomSheet?.dismiss()
        bottomSheet = BottomSheet.Builder(activity!!)
                .title(getString(R.string.select_action))
                .darkTheme()
                .sheet(if (data.hasImage()) R.menu.menu_pick_retake_photo else R.menu.menu_pick_new_photo)
                .listener { dialog, which ->
                    when (which) {
                        R.id.camera, R.id.gallery -> {
                            dialog?.dismiss()
                            fromCamera = which == R.id.camera
                            if (checkPermissions()) {
                                takeImage()
                            } else {
                                requestPermissionsInternal()
                            }
                        }
                        R.id.cancel -> {
                            dialog?.dismiss()
                        }
                        R.id.delete -> {
                            dialog?.dismiss()
                            clearImage(position)
                        }
                    }
                }.build()
        bottomSheet?.show()
    }

    private fun takeImage() {
        if (fromCamera) {
            startCameraIntent()
        } else {
            startGalleryIntent()
        }
    }

    override fun onCameraCaptured(filePath: String) {
        super.onCameraCaptured(filePath)
        insertImage(filePath)
    }

    override fun onGalleryCaptured(filePath: String) {
        super.onGalleryCaptured(filePath)
        insertImage(filePath)
    }

    private fun insertImage(filePath: String) {
        val position = adapter.findFirstEmptyContainerPosition()
        if (position == -1) return
        adapter.getItem(position).filePath = filePath
        if (position == 0) {
            adapter.notifyDataSetChanged()
        } else {
            try {
                adapter.notifyItemChanged(position)
            } catch (e: Exception) {
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCameraFailure() {
        super.onCameraFailure()
        onFailureCapturing()
    }

    override fun onGalleryFailure() {
        super.onGalleryFailure()
        onFailureCapturing()
    }

    private fun onFailureCapturing() {
        showSweetAlertDialog(R.string.error, R.string.err_unable_take_photo)
    }

    override fun onPermissionsGranted() {
        super.onPermissionsGranted()
        takeImage()
    }

    override fun getHeaderText() = R.string.menu_label_sell

    override fun getHeaderIcon() = R.drawable.ic_close

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

    private fun clearImage(position: Int) {
        synchronized(adapter.data) {
            adapter.data.removeAt(position)
            try {
                if (position == 0) {
                    adapter.notifyDataSetChanged()
                } else {
                    adapter.notifyItemRemoved(position)
                }
            } catch (e: Exception) {
                adapter.notifyDataSetChanged()
            } finally {
                addDataLast()
            }
        }
    }

    private fun addDataLast() {
        adapter.addData(SellImageModel())
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
        setupData()
    }

    private fun getFormattedPrice(showUBCBalance: Boolean): String {
        if (showUBCBalance) {
            if (currentPriceInUBC <= .0) {
                currentPriceInUBC = .0
                return getString(R.string.balance_placeholder, "0.00")
            }
            return getString(R.string.balance_placeholder, currentPriceInUBC.moneyFormat())
        } else {
            if (currentPriceInUSD <= .0) {
                currentPriceInUSD = .0
                return getString(R.string.balance_placeholder_usd, "0.00")
            }
            return getString(R.string.balance_placeholder_usd, currentPriceInUSD.moneyFormat())
        }
    }

}