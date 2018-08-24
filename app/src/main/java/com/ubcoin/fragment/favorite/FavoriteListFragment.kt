package com.ubcoin.fragment.favorite

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.adapter.FavoriteListAdapter
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.fragment.FirstLineFragment
import com.ubcoin.fragment.market.MarketDetailsFragment
import com.ubcoin.model.response.MarketItem
import com.ubcoin.network.DataProvider
import com.ubcoin.utils.CollectionExtensions
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import com.ubcoin.utils.ProfileHolder
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * Created by Yuriy Aizenberg
 */

private const val LIMIT = 30

class FavoriteListFragment : FirstLineFragment() {

    private lateinit var llNoFavoriteItems: View
    private lateinit var progressCenter: View
    private lateinit var progressBottom: View
    private lateinit var rvMarketList: RecyclerView
    private var favoriteListAdapter: FavoriteListAdapter? = null

    private var isLoading = false
    private var isEndOfLoading = false
    private var currentDisposable: Disposable? = null
    private var currentPage = 0

    override fun getLayoutResId() = R.layout.fragment_favorites

    override fun getHeaderText() = R.string.header_favorites

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        llNoFavoriteItems = view.findViewById(R.id.llNoFavoriteItems)
        rvMarketList = view.findViewById(R.id.rvMarketList)
        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)

        favoriteListAdapter = FavoriteListAdapter(activity!!)
        favoriteListAdapter?.setHasStableIds(true)
        favoriteListAdapter?.recyclerTouchListener = object : IRecyclerTouchListener<MarketItem> {
            override fun onItemClick(data: MarketItem, position: Int) {
                getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(data), false)
            }

        }

        rvMarketList.run {
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(object : EndlessRecyclerViewOnScrollListener(layoutManager!!) {
                override fun onLoadMore() {
                    loadData()
                }

            })
            setHasFixedSize(true)
            adapter = favoriteListAdapter
        }

        if (!ProfileHolder.isAuthorized()) {
            rvMarketList.visibility = View.GONE
            llNoFavoriteItems.visibility = View.VISIBLE
        } else {
            rvMarketList.visibility = View.VISIBLE
            llNoFavoriteItems.visibility = View.GONE
            loadData()
        }

        view.findViewById<View>(R.id.imgHeaderLeft).visibility = View.INVISIBLE
        view.findViewById<View>(R.id.llHeaderLeft).setOnClickListener { }

    }

    private fun loadData() {
        if (isLoading || isEndOfLoading) return

        isLoading = true

        cancelCurrentLoading()

        if (favoriteListAdapter!!.isEmpty()) {
            progressCenter.visibility = View.VISIBLE
        } else {
            progressBottom.visibility = View.VISIBLE
        }

        currentDisposable = DataProvider.getFavoriteList(LIMIT, currentPage,
                Consumer {
                    isLoading = false
                    currentPage++
                    if (it.data.size < LIMIT) {
                        isEndOfLoading = true
                    }
                    hideViewsQuietly(progressCenter, progressBottom)
                    if (CollectionExtensions.nullOrEmpty(it.data)) {
                        rvMarketList.visibility = View.GONE
                        llNoFavoriteItems.visibility = View.VISIBLE
                    } else {
                        rvMarketList.visibility = View.VISIBLE
                        llNoFavoriteItems.visibility = View.GONE
                        favoriteListAdapter?.addData(it.data)
                    }
                }, Consumer { handleException(it) })
    }

    private fun cancelCurrentLoading() {
        currentDisposable?.dispose()
    }

    override fun handleException(t: Throwable) {
        hideViewsQuietly(progressCenter, progressBottom)
        isLoading = false
        super.handleException(t)
    }

    override fun onResume() {
        super.onResume()
        callItemRemoved()
        if (favoriteListAdapter != null && favoriteListAdapter?.isEmpty() == false) {
            if (!rvMarketList.canScrollVertically(1)) {
                rvMarketList.postDelayed({ rvMarketList.scrollToPosition(favoriteListAdapter!!.itemCount - 1) }, 300L)
            }
        }
    }


    private fun callItemRemoved() {
        val favoriteIdForRemove = TheApplication.instance.favoriteIdForRemove
        if (favoriteListAdapter == null || favoriteIdForRemove == null) return
        val data = favoriteListAdapter!!.data
        if (data.isEmpty()) return

        synchronized(data) {
            var index = -1
            for ((i, it) in data.withIndex()) {
                if (it.id == favoriteIdForRemove) {
                    index = i
                    break
                }
            }
            if (index != -1) {
                data.removeAt(index)
                favoriteListAdapter?.run {
                    if (index == 0) {
                        notifyDataSetChanged()
                    } else {
                        try {
                            notifyItemRemoved(index)
                        } catch (e: Exception) {
                            notifyDataSetChanged()
                        }
                    }
                    if (CollectionExtensions.nullOrEmpty(favoriteListAdapter?.data)) {
                        rvMarketList.visibility = View.GONE
                        llNoFavoriteItems.visibility = View.VISIBLE
                    } else {
                        rvMarketList.visibility = View.VISIBLE
                        llNoFavoriteItems.visibility = View.GONE
                    }
                }
            }
        }
    }


}