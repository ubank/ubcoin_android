package com.ubcoin.fragment.deals

import android.support.v4.view.ViewPager
import android.view.View
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.FirstLineFragment

/**
 * Created by Yuriy Aizenberg
 */
class DealsParentFragment : BaseFragment() {

    override fun getLayoutResId() = R.layout.fragment_deals
    override fun getHeaderIcon() = R.drawable.ic_back

    override fun isFooterShow() = false

    override fun onIconClick() {
        super.onIconClick()
        activity!!.onBackPressed()
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        val viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        val smartTabLayout = view.findViewById<SmartTabLayout>(R.id.viewpagertab)

        val fragmentPagerItemAdapter = FragmentPagerItemAdapter(
                childFragmentManager,
                FragmentPagerItems.with(activity)
                        .add(getString(R.string.to_buy), BuyDealsChildFragment::class.java)
                        .add(getString(R.string.to_sell), SellDealsChildFragment::class.java)
                        .create()
        )
        viewPager.adapter = fragmentPagerItemAdapter
        smartTabLayout.setViewPager(viewPager)
    }
}