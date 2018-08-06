package com.ubcoin.fragment.favorite

import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment

/**
 * Created by Yuriy Aizenberg
 */
class FavoriteListFragment : BaseFragment() {

    override fun getLayoutResId() = R.layout.fragment_favorites

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun getHeaderText() = R.string.header_favorites

}