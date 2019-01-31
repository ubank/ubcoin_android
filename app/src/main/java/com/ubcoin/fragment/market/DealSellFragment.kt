package com.ubcoin.fragment.market

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.ubcoin.R
import com.ubcoin.adapter.ProgressAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.Progress

class DealSellFragment : BaseFragment() {

    override fun getLayoutResId() = R.layout.fragment_deal_sell
    override fun getHeaderIcon() = R.drawable.ic_back
    override fun getHeaderText() = R.string.menu_label_sell


    override fun isFooterShow() = false

    override fun onIconClick() {
        super.onIconClick()
        activity!!.onBackPressed()
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)

        initProgress()
    }

    fun initProgress(){

    }
}