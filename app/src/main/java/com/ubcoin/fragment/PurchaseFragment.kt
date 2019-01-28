package com.ubcoin.fragment

import android.support.v4.view.ViewPager
import android.view.View
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.ubcoin.R
import com.ubcoin.fragment.deals.BuyDealsChildFragment
import com.ubcoin.fragment.deals.SellDealsChildFragment

class PurchaseFragment : BaseFragment() {

    override fun getLayoutResId() = R.layout.fragment_purchase
    override fun getHeaderIcon() = R.drawable.ic_back
    override fun getHeaderText() = R.string.text_purchase

    override fun isFooterShow() = false

    override fun onIconClick() {
        super.onIconClick()
        activity!!.onBackPressed()
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
    }
}