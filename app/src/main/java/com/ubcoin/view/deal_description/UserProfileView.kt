package com.ubcoin.view.deal_description

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.activity.BaseActivity
import com.ubcoin.fragment.profile.SellerProfileFragment
import com.ubcoin.model.response.MarketItem
import com.ubcoin.model.response.User
import com.ubcoin.switcher.FragmentSwitcher

class UserProfileView: LinearLayout {

    var user: User? = null
        set(value) {
            field = value
            initView()
        }
    private lateinit var tvName: TextView
    private lateinit var tvDealsCount: TextView
    private lateinit var ivImage: ImageView

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
        inflate(context, R.layout.view_user_profile, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        tvName = findViewById(R.id.tvName)
        tvDealsCount = findViewById(R.id.tvDealsCount)
        ivImage = findViewById(R.id.ivImage)
    }

    fun initView(){
        GlideApp.with(context!!).load(user?.avatarUrl)
                .override(R.dimen.detailsSubProfileHeight, R.dimen.detailsSubProfileHeight)
                .centerInside()
                .transform(RoundedCorners(context!!.resources.getDimensionPixelSize(R.dimen.detailsSubProfileHeight)))
                .placeholder(R.drawable.img_profile_default)
                .error(R.drawable.img_profile_default)
                .into(ivImage)
        tvName.setText(user?.name)

        val itemsCount = user?.itemsCount ?: 0
        tvDealsCount.text = resources.getQuantityString(R.plurals.txt_active_deals_count, itemsCount,itemsCount)
    }
}