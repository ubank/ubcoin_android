package com.ubcoin.fragment.deals

import android.support.v4.view.ViewPager
import android.view.View
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment

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

    var fragmentPagerItemAdapter: FragmentStatePagerItemAdapter? = null

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        val viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        val smartTabLayout = view.findViewById<SmartTabLayout>(R.id.viewpagertab)

        fragmentPagerItemAdapter = FragmentStatePagerItemAdapter(
                childFragmentManager,
                FragmentPagerItems.with(activity)
                        .add(getString(R.string.to_buy), BuysDealsChildFragment::class.java)
                        .add(getString(R.string.to_sell), SellDealsChildFragment::class.java)
                        .create()
        )
        viewPager.adapter = fragmentPagerItemAdapter
        smartTabLayout.setViewPager(viewPager)
    }

    override fun onResume() {
        super.onResume()
        for(i in 0 .. fragmentPagerItemAdapter?.count!!-1) {
            var fragment = fragmentPagerItemAdapter?.getPage(i)
            if(fragment is SellDealsChildFragment)
                fragment.update()
            if(fragment is BuysDealsChildFragment)
                fragment.update()
        }
    }


    override fun subscribeOnDealUpdate(id: String) {
        super.subscribeOnDealUpdate(id)
        for(i in 0 .. fragmentPagerItemAdapter?.count!!-1) {
            var fragment = fragmentPagerItemAdapter?.getPage(i)
            if(fragment is SellDealsChildFragment)
                fragment.subscribeOnDealUpdate(id)
            if(fragment is BuysDealsChildFragment)
                fragment.subscribeOnDealUpdate(id)
        }
    }
}