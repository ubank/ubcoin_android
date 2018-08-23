package com.ubcoin.fragment.sell

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.cocosw.bottomsheet.BottomSheet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.SellImagesAdapter
import com.ubcoin.fragment.FirstLineFragment
import com.ubcoin.model.SellImageModel
import com.ubcoin.model.response.TgLink
import com.ubcoin.model.response.TgLinks
import com.ubcoin.model.response.base.IdResponse
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.network.request.CreateProductRequest
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.utils.SellCreateDataHolder
import com.ubcoin.utils.moneyFormat

/**
 * Created by Yuriy Aizenberg
 */
class SellFragment : FirstLineFragment(), IRecyclerTouchListener<SellImageModel>, OnMapReadyCallback {

    private val datum = ArrayList<SellImageModel>()
    private lateinit var adapter: SellImagesAdapter
    private var bottomSheet: BottomSheet? = null
    private var fromCamera = false
    private var googleMap: GoogleMap? = null
    private var currentPrice: Double = .0
    private var marker: Marker? = null

    private lateinit var mapView: MapView
    private lateinit var edtSellTitle: MaterialEditText
    private lateinit var edtSellCategory: MaterialEditText
    private lateinit var edtSellPrice: MaterialEditText
    private lateinit var edtSellDescription: MaterialEditText
    private lateinit var txtSellLocation: TextView
    private lateinit var llSellLocation: View
    private lateinit var btnSellDone: View


    init {
        (0..4).forEach { datum.add(SellImageModel()) }
    }

    override fun getLayoutResId() = R.layout.fragment_sell

    override fun isFooterShow() = false

    override fun onViewInflated(view: View, savedInstanceState: Bundle?) {
        super.onViewInflated(view, savedInstanceState)
        SellCreateDataHolder.reset()

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
        edtSellPrice = view.findViewById(R.id.edtSellPrice)
        edtSellDescription = view.findViewById(R.id.edtSellDescription)
        txtSellLocation = view.findViewById(R.id.txtSellLocation)
        llSellLocation = view.findViewById(R.id.llSellLocation)
        btnSellDone = view.findViewById(R.id.btnSellDone)

        mapView.getMapAsync(this)
        mapView.onCreate(savedInstanceState)

        setupData()

        view.findViewById<View>(R.id.llSellContainer).setOnTouchListener { _, p1 ->
            if (p1!!.action == MotionEvent.ACTION_UP) {
                openPriceDialog()
            }
            true
        }
        view.findViewById<View>(R.id.llCategoryContainer).setOnTouchListener { _, motionEvent ->
            if (motionEvent!!.action == MotionEvent.ACTION_UP) {
                getSwitcher()?.addTo(SelectCategoryFragment::class.java, SelectCategoryFragment.createBundle(SellCreateDataHolder.category?.id), true)
            }
            true
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

    override fun onResume() {
        super.onResume()
        if (SellCreateDataHolder.hasChanges) {
            setupData()
        }
        mapView.onResume()
    }

    private val requestCode = 19990

    private fun validateTgUser(): Boolean {
        val authorizedInTg = ProfileHolder.user!!.authorizedInTg ?: false
        if (!authorizedInTg) {
            showProgressDialog("Loading", "Wait please")
            DataProvider.getTgLink(object : SilentConsumer<TgLink> {
                override fun onConsume(t: TgLink) {
                    hideProgressDialog()
                    ProfileHolder.user!!.authorizedInTg = t.user?.authorizedInTg?:false
                    TheApplication.instance.openTelegramIntent(t.url, t.appUrl, this@SellFragment, requestCode)
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
        showProgressDialog("Create product", "Wait please")
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
                        Toast.makeText(activity, "Product created", Toast.LENGTH_SHORT).show()
                        activity?.onBackPressed()
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
            showSweetAlertDialog("Error", getString(R.string.err_title_missing))
            return null
        }
        if (currentPrice <= 0f) {
            showSweetAlertDialog("Error", getString(R.string.err_price_required))
            return null
        }

        val categoryId = SellCreateDataHolder.category?.id
        if (categoryId == null) {
            showSweetAlertDialog("Error", getString(R.string.err_category_missing))
            return null
        }

        val location = SellCreateDataHolder.location
        if (location == null || !location.isAddressPresented()) {
            showSweetAlertDialog("Error", getString(R.string.err_location_missing))
            return null
        }

        if (!hasImages()) {
            showSweetAlertDialog("Error", getString(R.string.err_image_missing))
            return null
        }

        return CreateProductRequest(
                categoryId,
                title,
                edtSellDescription.text.toString().trim(),
                currentPrice.toDouble(),
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
        setCurrentPrice()
    }

    private fun openPriceDialog() {
        val materialDialog = MaterialDialog.Builder(activity!!)
                .customView(R.layout.fragment_content_select_price, false)
                .build()
        val edtPrice: MaterialEditText = materialDialog.findViewById(R.id.edtPrice) as MaterialEditText

        materialDialog.findViewById(R.id.btnDialogCancel).setOnClickListener {
            materialDialog.dismiss()
        }

        materialDialog.findViewById(R.id.btnDialogDone).setOnClickListener {
            materialDialog.dismiss()
            currentPrice = try {
                edtPrice.text.toString().toDouble()
            } catch (e: Exception) {
                .0
            }
            setCurrentPrice()

        }
        if (currentPrice > 0f) {
            edtPrice.setText(currentPrice.toString())
        }
        materialDialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        materialDialog.show()
    }

    private fun setCurrentPrice() {
        edtSellPrice.setText(getFormattedPrice())
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
        showSweetAlertDialog("Error", "Unable to take photo")
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

    private fun getFormattedPrice(): String {
        if (currentPrice <= .0) {
            currentPrice = .0
            return getString(R.string.balance_placeholder, "0.00")
        }
        return getString(R.string.balance_placeholder, currentPrice.moneyFormat())
    }

/*    private fun formatPriceWithTwoDigits(): String {
        return String.format("%.2f", currentPrice)
    }*/

}