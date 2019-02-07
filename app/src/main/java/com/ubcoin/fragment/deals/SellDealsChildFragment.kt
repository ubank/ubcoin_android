package com.ubcoin.fragment.deals

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.google.android.gms.common.util.CollectionUtils
import com.ubcoin.R
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.SellsListAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.market.DealSellFragment
import com.ubcoin.fragment.market.MarketDetailsFragment
import com.ubcoin.fragment.market.UpdateMarketStateItemEvent
import com.ubcoin.fragment.sell.MarketUpdateEvent
import com.ubcoin.model.response.*
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import com.ubcoin.utils.MarketItemsSorterBySections
import com.ubcoin.utils.ProfileHolder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.function.BiConsumer

/**
 * Created by Yuriy Aizenberg
 */
private const val LIMIT = 30

class SellDealsChildFragment : BaseFragment() {

    private lateinit var llNoDeals: View
    private lateinit var sellsListAdapter: SellsListAdapter
    private lateinit var recyclerView: RecyclerView
    private var progressCenter: View? = null
    private var progressBottom: View? = null


    private var isLoading = false
    private var isEndOfLoading = false
    private var currentPage = -1

    override fun getLayoutResId() = R.layout.fragment_deals_child
    override fun isFooterShow() = false
    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        llNoDeals = view.findViewById<RecyclerView>(R.id.llNoDeals)
        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)
        recyclerView = view.findViewById(R.id.rvDeals)

        sellsListAdapter = SellsListAdapter(activity!!)
        sellsListAdapter.setHasStableIds(true)

        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = sellsListAdapter

        if (ProfileHolder.isAuthorized()) {
            llNoDeals.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            loadData()
        } else {
            llNoDeals.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }


        sellsListAdapter.recyclerTouchListener = object : IRecyclerTouchListener<MarketItemMarker> {
            override fun onItemClick(data: MarketItemMarker, position: Int) {
                if((data as MarketItem).purchases != null && (data as MarketItem).purchases.size > 0)
                    getSwitcher()?.addTo(DealSellFragment::class.java, DealSellFragment.getBundle((data as MarketItem).purchases.get(0).id), false)
                else
                    getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(data as MarketItem), false)
            }
        }

        recyclerView.addOnScrollListener(object : EndlessRecyclerViewOnScrollListener(linearLayoutManager) {
            override fun onLoadMore() {
                loadData()
            }
        })
    }


    fun loadData() {
        if (isLoading || isEndOfLoading) return

        currentPage++

        if (sellsListAdapter.isEmpty()) {
            progressCenter?.visibility = View.VISIBLE
        } else {
            progressBottom?.visibility = View.VISIBLE
        }

        val onSuccess = object : SilentConsumer<MarketListResponse> {
            override fun onConsume(t: MarketListResponse) {
                val data = t.data
                hideProgress()
                if (data.size < LIMIT) {
                    isEndOfLoading = true
                }
                sellsListAdapter.addData(prepareData(t.data))
                if (sellsListAdapter.isEmpty()) {
                    llNoDeals.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    llNoDeals.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }

        }
        val onError = object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                handleException(t)
            }

        }

        DataProvider.getSellerItems(LIMIT, currentPage, onSuccess, onError)
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onPause() {
        EventBus.getDefault().unregister(this)
        super.onPause()
    }

    @Subscribe
    fun onMarketUpdate(marketUpdateEvent: MarketUpdateEvent) {
        val arrayList = ArrayList<MarketItem>()
        sellsListAdapter.data.forEach {
            if (it is MarketItem) {
                arrayList.add(it)
            }
        }
        for ((index, datum) in arrayList.withIndex()) {
            if (datum.id == marketUpdateEvent.marketItem.id) {
                arrayList[index] = marketUpdateEvent.marketItem
                break
            }
        }
        sellsListAdapter.clear()
        sellsListAdapter.addData(prepareData(arrayList))
    }

    @Subscribe
    fun onActivatedOrDeactivated(updateMarketStateItemEvent: UpdateMarketStateItemEvent) {
        val arrayList = ArrayList<MarketItem>()
        sellsListAdapter.data.forEach {
            if (it is MarketItem) {
                arrayList.add(it)
            }
        }
        for (datum in arrayList) {
            if (datum.id == updateMarketStateItemEvent.marketItem.id) {
                datum.status = updateMarketStateItemEvent.marketItem.status
                break
            }
        }
        sellsListAdapter.clear()
        sellsListAdapter.addData(prepareData(arrayList))
    }

    private fun prepareData(marketItems: List<MarketItem>): List<MarketItemMarker> {
        if (CollectionUtils.isEmpty(marketItems)) return ArrayList()

        val sortedList = MarketItemsSorterBySections.sort(marketItems)

        if (sellsListAdapter.isEmpty()) {
            val associatedMap = groupData(sortedList)
            val toReturn = ArrayList<MarketItemMarker>()
            for (entry in associatedMap) {
                toReturn.add(MarketItemHeader(getString(MarketItemStatus.bySplitKey(entry.key))))
                toReturn.addAll(entry.value)
            }
            return ArrayList(toReturn)
        } else {
            val lastItemInAdapter = sellsListAdapter.data[sellsListAdapter.data.size - 1]
            val toReturn : MutableList<MarketItemMarker> = ArrayList<MarketItemMarker>().toMutableList()

            val mutableList = sortedList.toMutableList()
            val iterator = mutableList.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if ((lastItemInAdapter as MarketItem).status == next.status) {
                    toReturn.add(next)
                    iterator.remove()
                } else {
                    break
                }
            }
            if (mutableList.isEmpty()) {
                return ArrayList(toReturn)
            }
            val associatedMap = groupData(mutableList)
            for (entry in associatedMap) {
                toReturn.add(MarketItemHeader(getString(MarketItemStatus.bySplitKey(entry.key))))
                toReturn.addAll(entry.value)
            }
            return ArrayList(toReturn)

        }
    }

    private fun groupData(sortedList: List<MarketItem>): LinkedHashMap<Int, ArrayList<MarketItem>> {
        val associatedMap = LinkedHashMap<Int, ArrayList<MarketItem>>()
        sortedList.forEach {
            val currentList: ArrayList<MarketItem>? =
                    if (!associatedMap.containsKey(it.status!!.groupKey)) {
                        associatedMap.put(it.status!!.groupKey, ArrayList())
                        associatedMap[it.status!!.groupKey]
                    } else {
                        associatedMap[it.status!!.groupKey]
                    }
            currentList!!.add(it)
        }
        return associatedMap
    }

    private fun hideProgress() {
        hideProgressDialog()
        hideViewsQuietly(progressCenter, progressBottom)
        isLoading = false
    }

    override fun handleException(t: Throwable) {
        hideProgress()
        super.handleException(t)
    }

}