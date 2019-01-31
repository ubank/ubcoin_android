package com.ubcoin.view.deal_description

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.maps.model.LatLng
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.adapter.ProgressAdapter
import com.ubcoin.model.Progress
import com.ubcoin.model.response.Location
import com.ubcoin.model.response.User
import com.ubcoin.utils.DistanceUtils

class SellerLocationView: LinearLayout {

    var location: Location? = null
        set(value) {
            field = value
            initView()
        }
    private lateinit var tvLocation: TextView
    private lateinit var tvDistance: TextView

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(attrs: AttributeSet?) {
        inflate(context, R.layout.view_seller_location, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        tvLocation = findViewById<TextView>(R.id.tvLocation)
        tvDistance = findViewById<TextView>(R.id.tvDistance)
    }

    fun initView(){
        val itemLocationLatLng = LatLng(location?.latPoint?.toDouble()
                ?: .0, location?.longPoint?.toDouble() ?: .0)
        tvDistance.text = DistanceUtils.calculateDistance(itemLocationLatLng, context!!)
        tvLocation.text = location?.text
    }
}