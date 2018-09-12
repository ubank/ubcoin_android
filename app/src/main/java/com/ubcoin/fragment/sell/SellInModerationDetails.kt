package com.ubcoin.fragment.sell

import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment

/**
 * Created by Yuriy Aizenberg
 */
class SellInModerationDetails : BaseFragment() {
    override fun getLayoutResId() = R.layout.fragment_sell_completed

    override fun getHeaderIcon() = R.drawable.ic_close

    override fun getHeaderText() = R.string.menu_label_sell

    override fun isFooterShow() = false

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

}