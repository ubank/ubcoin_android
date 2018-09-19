package com.ubcoin.fragment.market

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.OvershootInterpolator
import com.google.android.gms.maps.model.LatLng
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.adapter.GridItemOffsetDecoration
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.MarketListAdapter
import com.ubcoin.fragment.FirstLineFragment
import com.ubcoin.fragment.sell.MarketUpdateEvent
import com.ubcoin.model.response.MarketItem
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import com.ubcoin.utils.ProfileHolder
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean

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

    private val isFavoriteProcessing = AtomicBoolean(false)

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
                getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(data, position), true)
            }
        }
        view.findViewById<View>(R.id.imgHeaderLeft).visibility = View.INVISIBLE
        view.findViewById<View>(R.id.llHeaderLeft).setOnClickListener { }

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
                TheApplication.instance.currentLatitude(), TheApplication.instance.currentLongitude(),
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
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onLatLngEvent(latLng: LatLng) {
        if (marketListAdapter != null) {
            marketListAdapter?.notifyDataSetChanged()
        }
    }

    @Subscribe
    fun onMarketUpdate(marketUpdateEvent: MarketUpdateEvent) {
        if (marketListAdapter != null) {
            for ((index, datum) in marketListAdapter!!.data.withIndex()) {
                if (datum.id == marketUpdateEvent.marketItem.id) {
                    marketListAdapter!!.data[index] = datum
                    if (index == 0) {
                        marketListAdapter!!.notifyDataSetChanged()
                    } else {
                        marketListAdapter!!.notifyItemChanged(index)
                    }
                    break
                }
            }
        }
    }

    @Subscribe
    fun onFavoriteEvent(event: UpdateMarketItemEvent?) {
        if (event != null && event.position != -1) {
            try {
                val item = marketListAdapter?.getItem(position = event.position) ?: return
                item.favorite = event.isFavorite
                if (event.position == 0) {
                    marketListAdapter?.notifyDataSetChanged()
                } else {
                    marketListAdapter?.notifyItemChanged(event.position)
                }
            } catch (e: Exception) {
            }

        }
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
        if (isFavoriteProcessing.get()) return
        isFavoriteProcessing.set(true)
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
                isFavoriteProcessing.set(false)
            }

        }
    }

    private fun cancelCurrentLoading() = currentDisposableLoader?.dispose()

    override fun handleException(t: Throwable) {
        hideViewsQuietly(progressCenter, progressBottom)
        isLoading = false
        isFavoriteProcessing.set(false)
        super.handleException(t)
    }

    override fun getHeaderText() = R.string.app_name
}