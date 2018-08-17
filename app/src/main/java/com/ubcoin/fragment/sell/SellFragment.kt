package com.ubcoin.fragment.sell

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.cocosw.bottomsheet.BottomSheet
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.SellImagesAdapter
import com.ubcoin.fragment.FirstLineFragment
import com.ubcoin.model.SellImageModel
import com.ubcoin.utils.SellCreateDataHolder
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

/**
 * Created by Yuriy Aizenberg
 */
class SellFragment : FirstLineFragment(), IRecyclerTouchListener<SellImageModel>, OnMapReadyCallback {

    private val datum = ArrayList<SellImageModel>()
    private lateinit var adapter: SellImagesAdapter
    private var bottomSheet: BottomSheet? = null
    private var fromCamera = false
    private var googleMap: GoogleMap?= null
    private var currentPrice: Float = 0f

    private lateinit var mapView: MapView
    private lateinit var edtSellTitle : MaterialEditText
    private lateinit var edtSellCategory : MaterialEditText
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
    }

    override fun onResume() {
        super.onResume()
        if (SellCreateDataHolder.hasChanges) {
            setupData()
        }
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
            txtSellLocation.text = location.text
        }
        setCurrentPrice()
    }

    private fun openPriceDialog() {
        val materialDialog = MaterialDialog.Builder(activity!!)
                .customView(R.layout.fragment_content_select_price, false)
                .build()
        val edtPrice : MaterialEditText = materialDialog.findViewById(R.id.edtPrice) as MaterialEditText

        materialDialog.findViewById(R.id.btnDialogCancel).setOnClickListener {
            materialDialog.dismiss()
        }

        materialDialog.findViewById(R.id.btnDialogDone).setOnClickListener {
            materialDialog.dismiss()
            currentPrice = try {
                edtPrice.text.toString().toFloat()
            } catch (e: Exception) {
                0f
            }
            setCurrentPrice()

        }
        if (currentPrice > 0f) {
            edtPrice.setText(currentPrice.toString())
        }
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
    }

    private fun getFormattedPrice() : String {
        if (currentPrice <= 0f) {
            currentPrice = 0f
            return getString(R.string.balance_placeholder, "0.00")
        }
        val format = formatPriceWithTwoDigits()
        return getString(R.string.balance_placeholder, format)
    }

    private fun formatPriceWithTwoDigits(): String {
       /* val formatter = NumberFormat.getInstance(Locale.US)
        formatter.maximumFractionDigits = 2
        formatter.minimumFractionDigits = 2
        return formatter.format(currentPrice)*/
        return String.format("%.2f", currentPrice)
    }

}