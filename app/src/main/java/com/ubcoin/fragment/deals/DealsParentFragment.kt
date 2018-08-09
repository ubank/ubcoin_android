package com.ubcoin.fragment.deals

import android.support.v4.view.ViewPager
import android.view.View
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.ubcoin.R
import com.ubcoin.fragment.FirstLineFragment

/**
 * Created by Yuriy Aizenberg
 */
class DealsParentFragment : FirstLineFragment() {

    override fun getLayoutResId() = R.layout.fragment_deals

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        val viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        val smartTabLayout = view.findViewById<SmartTabLayout>(R.id.viewpagertab)

        val fragmentPagerItemAdapter = FragmentPagerItemAdapter(
                childFragmentManager,
                FragmentPagerItems.with(activity)
                        .add("To sell", DealsSellFragment::class.java)
                        .add("To buy", DealsBuyFragment::class.java)
                        .create()
        )
        viewPager.adapter = fragmentPagerItemAdapter
        smartTabLayout.setViewPager(viewPager)
    }

    override fun getHeaderText() = R.string.deals_header

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

}