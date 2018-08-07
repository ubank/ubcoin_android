package com.ubcoin.fragment.favorite

import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.FirstLineFragment

/**
 * Created by Yuriy Aizenberg
 */
class FavoriteListFragment : FirstLineFragment() {

    override fun getLayoutResId() = R.layout.fragment_favorites

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun getHeaderText() = R.string.header_favorites

}