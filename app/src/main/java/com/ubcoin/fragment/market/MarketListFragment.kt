package com.ubcoin.fragment.market

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.OvershootInterpolator
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.adapter.GridItemOffsetDecoration
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.MarketListAdapter
import com.ubcoin.fragment.FirstLineFragment
import com.ubcoin.model.response.MarketItem
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import com.ubcoin.utils.ProfileHolder
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import retrofit2.Response

/**
 * Created by Yuriy Aizenberg
 */
private const val LIMIT = 30

class MarketListFragment : FirstLineFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressCenter: View
    private lateinit var progressBottom: View
    private var marketListAdapter: MarketListAdapter? = null
    private var currentDisposableLoader: Disposable? = null

    private var currentPage = 0
    private var isLoading = false
    private var isEndOfLoading = false

    override fun getLayoutResId() = R.layout.fragment_market_list

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        recyclerView = view.findViewById(R.id.rvMarketList)

        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)

        marketListAdapter = MarketListAdapter(activity!!)
        marketListAdapter?.setHasStableIds(true)
        marketListAdapter?.favoriteListener = object : MarketListAdapter.IFavoriteListener {
            override fun onFavoriteTouch(data: MarketItem, position: Int) {
                if (ProfileHolder.isAuthorized()) {
                    processFavorite(data, position, !data.favorite)
                } else {
                    showNeedToRegistration()
                }
            }
        }

        val gridLayoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)

        recyclerView.addOnScrollListener(object : EndlessRecyclerViewOnScrollListener(gridLayoutManager) {
            override fun onLoadMore() {
                loadData()
            }

        })

        recyclerView.run {
            setHasFixedSize(true)
            addItemDecoration(GridItemOffsetDecoration(activity!!, R.dimen.decoration_grid_offset))
            adapter = marketListAdapter
            layoutManager = gridLayoutManager
            val fadeInAnimator = FadeInAnimator(OvershootInterpolator(1f))
            fadeInAnimator.addDuration = 300
            itemAnimator = fadeInAnimator
        }

        loadData()

        marketListAdapter?.recyclerTouchListener = object : IRecyclerTouchListener<MarketItem> {
            override fun onItemClick(data: MarketItem, position: Int) {
                getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(data), true)
            }

        }
    }

    private fun loadData() {
        if (isLoading || isEndOfLoading) return

        if (marketListAdapter!!.isEmpty()) {
            progressCenter.visibility = View.VISIBLE
        } else {
            progressBottom.visibility = View.VISIBLE
        }

        cancelCurrentLoading()
        isLoading = true
        currentDisposableLoader = DataProvider.getMarketList(LIMIT, currentPage,
                Consumer {
                    if (it.data.size < LIMIT) {
                        isEndOfLoading = true
                    }
                    currentPage++
                    isLoading = false
                    hideViewsQuietly(progressCenter, progressBottom)
                    marketListAdapter?.addData(it.data)
                },
                Consumer {
                    handleException(it)
                })
    }

    override fun onResume() {
        super.onResume()
        callItemRemoved()
    }

    private fun callItemRemoved() {
        val favoriteIdForRemove = TheApplication.instance.favoriteIdForRemove
        if (marketListAdapter == null || favoriteIdForRemove == null) return
        val data = marketListAdapter!!.data
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
                data[index].favorite = false
                marketListAdapter?.run {
                    if (index == 0) {
                        notifyDataSetChanged()
                    } else {
                        try {
                            notifyItemChanged(index)
                        } catch (e: Exception) {
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    private fun processFavorite(marketItem: MarketItem, position: Int, markAsFavorite: Boolean) {
        if (markAsFavorite) {
            DataProvider.favorite(marketItem.id, successConsumer(marketItem, position), Consumer { handleException(it) })
        } else {
            DataProvider.unfavorite(marketItem.id, successConsumer(marketItem, position), Consumer { handleException(it) })
        }
    }

    private fun successConsumer(marketItem: MarketItem, position: Int): SilentConsumer<Response<Unit>> {
        return object : SilentConsumer<Response<Unit>> {
            override fun onConsume(t: Response<Unit>) {
                marketItem.favorite = !marketItem.favorite
                try {
                    marketListAdapter?.notifyItemChanged(position)
                } catch (e: Exception) {
                    marketListAdapter?.notifyDataSetChanged()
                }
            }

        }
    }

    private fun cancelCurrentLoading() = currentDisposableLoader?.dispose()

    override fun handleException(t: Throwable) {
        hideViewsQuietly(progressCenter, progressBottom)
        isLoading = false
        super.handleException(t)
    }

    override fun getHeaderText() = R.string.app_name
}