package com.ubcoin.fragment.favorite

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ubcoin.R
import com.ubcoin.adapter.FavoriteListAdapter
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.fragment.FirstLineFragment
import com.ubcoin.fragment.market.MarketDetailsFragment
import com.ubcoin.model.response.MarketItem
import com.ubcoin.network.DataProvider
import com.ubcoin.utils.CollectionExtensions
import io.reactivex.functions.Consumer

/**
 * Created by Yuriy Aizenberg
 */
class FavoriteListFragment : FirstLineFragment() {

    var llNoFavoriteItems: View? = null
    var progressCenter: View? = null
    var rvMarketList: RecyclerView? = null
    var favoriteListAdapter: FavoriteListAdapter? = null

    override fun getLayoutResId() = R.layout.fragment_favorites

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun getHeaderText() = R.string.header_favorites

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        llNoFavoriteItems = view.findViewById(R.id.llNoFavoriteItems)
        rvMarketList = view.findViewById(R.id.rvMarketList)
        progressCenter = view.findViewById(R.id.progressCenter)

        favoriteListAdapter = FavoriteListAdapter(activity!!)
        favoriteListAdapter?.setHasStableIds(true)
        favoriteListAdapter?.recyclerTouchListener = object : IRecyclerTouchListener<MarketItem> {
            override fun onItemClick(data: MarketItem, position: Int) {
                getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(data), false)
            }

        }

        rvMarketList?.run {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = favoriteListAdapter
        }

        progressCenter?.visibility = View.VISIBLE
        DataProvider.getFavoriteList(2, 0,
                Consumer {
                    hideViewsQuietly(progressCenter)
                    if (CollectionExtensions.nullOrEmpty(it.data)) {
                        rvMarketList?.visibility = View.GONE
                        llNoFavoriteItems?.visibility = View.VISIBLE
                    } else {
                        rvMarketList?.visibility = View.VISIBLE
                        llNoFavoriteItems?.visibility = View.GONE
                        favoriteListAdapter?.addData(it.data)
                    }
                }, Consumer { handleException(it) })

    }

    override fun handleException(t: Throwable) {
        hideViewsQuietly(progressCenter)
        super.handleException(t)
    }


}