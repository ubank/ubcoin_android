package com.ubcoin.fragment.sell

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.cocosw.bottomsheet.BottomSheet
import com.ubcoin.R
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.SellImagesAdapter
import com.ubcoin.fragment.FirstLineFragment
import com.ubcoin.model.SellImageModel
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import io.reactivex.functions.Consumer

/**
 * Created by Yuriy Aizenberg
 */
class SellFragment : FirstLineFragment(), IRecyclerTouchListener<SellImageModel> {

    private val datum = ArrayList<SellImageModel>()
    private lateinit var adapter: SellImagesAdapter
    private var bottomSheet: BottomSheet? = null
    private var fromCamera = false

    init {
        (0..4).forEach { datum.add(SellImageModel()) }
    }

    override fun getLayoutResId() = R.layout.fragment_sell


    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvSellImages)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)

        adapter = SellImagesAdapter(activity!!)
        adapter.setHasStableIds(true)

        recyclerView.adapter = adapter
        adapter.data = datum

        adapter.recyclerTouchListener = this

        DataProvider.getBuyersItems(10, 0, Consumer {

        }, Consumer {  })
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

}