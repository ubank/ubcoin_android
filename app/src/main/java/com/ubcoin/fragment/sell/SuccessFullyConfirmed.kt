package com.ubcoin.fragment.sell

import android.view.View
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment

/**
 * Created by Yuriy Aizenberg
 */
class SuccessFullyConfirmed : BaseFragment() {
    override fun getLayoutResId() = R.layout.fragment_product_updated

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        view.findViewById<View>(R.id.llClose).setOnClickListener { close() }
    }

    override fun getHeaderIcon() = R.drawable.ic_close

    override fun onIconClick() {
        super.onIconClick()
        close()
    }

    private fun close() {
        activity?.onBackPressed()
    }

}