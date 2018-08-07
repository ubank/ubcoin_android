package com.ubcoin.utils

import android.content.Context
import android.view.View
import android.widget.ImageView

import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator

import java.io.File

/**
 * Created by Yuriy Aizenberg
 */
abstract class CustomBaseSliderView protected constructor(context: Context) : BaseSliderView(context) {

    private var mUrl: String? = null
    private var mFile: File? = null
    private var mRes: Int = 0
    private val mLoadListener: BaseSliderView.ImageLoadListener? = null

    override fun image(url: String): BaseSliderView {
        mUrl = url
        return super.image(url)
    }

    override fun image(file: File): BaseSliderView {
        mFile = file
        return super.image(file)
    }

    override fun image(res: Int): BaseSliderView {
        mRes = res
        return super.image(res)
    }

    protected fun bindEventAndShow(v: View, targetImageView: ImageView?, disableCacheAndStore: Boolean, height: Int, width: Int) {
        if (!disableCacheAndStore) {
            super.bindEventAndShow(v, targetImageView)
            return
        }

        val me = this

        v.setOnClickListener {
            if (mOnSliderClickListener != null) {
                mOnSliderClickListener.onSliderClick(me)
            }
        }

        if (targetImageView == null)
            return

        mLoadListener?.onStart(me)

        val p = if (picasso != null) picasso else Picasso.get()
        val requestCreator: RequestCreator?
        requestCreator = when {
            url != null -> p.load(mUrl)
            mFile != null -> p.load(mFile!!)
            mRes != 0 -> p.load(mRes)
            else -> return
        }

        if (requestCreator == null) {
            return
        }

        // To Disable Image Caching
        /*   requestCreator.networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE);
        requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
*/
        if (empty != 0) {
            requestCreator.placeholder(empty)
        }

        if (error != 0) {
            requestCreator.error(error)
        }

        when (scaleType) {
            BaseSliderView.ScaleType.Fit -> requestCreator.fit()
            BaseSliderView.ScaleType.FitCenterCrop -> requestCreator.fit().centerCrop()
            BaseSliderView.ScaleType.CenterCrop -> requestCreator.resize(width, height).onlyScaleDown().centerCrop()
            BaseSliderView.ScaleType.CenterInside -> requestCreator.resize(width, height).onlyScaleDown().centerInside()
        }

        requestCreator.into(targetImageView, object : Callback {
            override fun onSuccess() {
                if (v.findViewById<View>(com.daimajia.slider.library.R.id.loading_bar) != null) {
                    v.findViewById<View>(com.daimajia.slider.library.R.id.loading_bar).visibility = View.INVISIBLE
                }
            }

            override fun onError(e: Exception) {
                mLoadListener?.onEnd(false, me)
                if (v.findViewById<View>(com.daimajia.slider.library.R.id.loading_bar) != null) {
                    v.findViewById<View>(com.daimajia.slider.library.R.id.loading_bar).visibility = View.INVISIBLE
                }
            }
        })
    }
}



