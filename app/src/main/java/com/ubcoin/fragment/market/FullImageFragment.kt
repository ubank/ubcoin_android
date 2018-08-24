package com.ubcoin.fragment.market

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment

/**
 * Created by Yuriy Aizenberg
 */

private const val BUNDLE_KEY = "url"

class FullImageFragment : BaseFragment() {

    companion object {
        fun getBundle(url: String): Bundle {
            val args = Bundle()
            args.putString(BUNDLE_KEY, url)
            return args
        }
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        view.findViewById<View>(R.id.llHeaderLeftSimple).setOnClickListener { activity!!.onBackPressed() }
        val imgFullscreen = view.findViewById<PhotoView>(R.id.imgFullscreen)
        val progressCenter = view.findViewById<View>(R.id.progressCenter)
        val url = arguments?.getString(BUNDLE_KEY, null)
        if (url == null) {
            activity!!.onBackPressed()
            return
        }
        progressCenter.visibility = View.VISIBLE
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        GlideApp.with(activity!!)
                .load(url)
                .override(0, metrics.widthPixels)
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        showSweetAlertDialog(R.string.error, R.string.unable_to_load_photo)
                        progressCenter.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        progressCenter.visibility = View.GONE
                        return false
                    }

                })
                .into(imgFullscreen)
    }

    override fun getLayoutResId() = R.layout.fragment_fullscreen_view

    override fun isFooterShow() = false

}