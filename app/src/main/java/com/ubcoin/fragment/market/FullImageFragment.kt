package com.ubcoin.fragment.market

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import java.lang.Exception

/**
 * Created by Yuriy Aizenberg
 */

private const val BUNDLE_KEY = "url"

class FullImageFragment : BaseFragment() {

    companion object {
        fun getBundle(url: String) : Bundle {
            val args = Bundle()
            args.putString(BUNDLE_KEY, url)
            return args
        }
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        view.findViewById<View>(R.id.llHeaderLeftSimple).setOnClickListener { activity!!.onBackPressed() }
        val imgFullscreen = view.findViewById<ImageView>(R.id.imgFullscreen)
        val progressCenter = view.findViewById<View>(R.id.progressCenter)
        val url = arguments?.getString(BUNDLE_KEY, null)
        if (url == null) {
            activity!!.onBackPressed()
            return
        }
        progressCenter.visibility = View.VISIBLE
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        Picasso.get()
                .load(url)
                .resize(0, metrics.widthPixels)
                .centerCrop()
                .into(imgFullscreen, object : Callback {
                    override fun onSuccess() {
                        progressCenter.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        showSweetAlertDialog("Error", getString(R.string.unable_to_load_photo))
                        progressCenter.visibility = View.GONE
                    }

                })
    }

    override fun getLayoutResId() = R.layout.fragment_fullscreen_view

    override fun isFooterShow() = false

}