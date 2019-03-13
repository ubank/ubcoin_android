package com.ubcoin.fragment.deals

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ubcoin.R
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.DealsListAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.market.DealPurchaseFragment
import com.ubcoin.fragment.market.DealSellFragment
import com.ubcoin.fragment.market.MarketDetailsFragment
import com.ubcoin.fragment.market.UpdateMarketStateItemEvent
import com.ubcoin.fragment.sell.MarketUpdateEvent
import com.ubcoin.model.response.*
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.ProfileHolder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by Yuriy Aizenberg
 */

class SellDealsChildFragment : BaseFragment() {
    private lateinit var llNoDeals: View
    private lateinit var dealsListAdapter: DealsListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var srLayout: SwipeRefreshLayout
    private var progressCenter: View? = null
    private var progressBottom: View? = null

    private var isLoading = false
    private var initialized = false

    override fun getLayoutResId() = R.layout.fragment_deals_child
    override fun isFooterShow() = false
    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        srLayout = view.findViewById(R.id.srLayout)
        srLayout.setOnRefreshListener {
            loadData()
        }
        llNoDeals = view.findViewById<RecyclerView>(R.id.llNoDeals)
        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)
        recyclerView = view.findViewById(R.id.rvDeals)

        dealsListAdapter = DealsListAdapter(activity!!)
        dealsListAdapter.setHasStableIds(true)

        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = dealsListAdapter

        if (ProfileHolder.isAuthorized()) {
            llNoDeals.visibility = View.GONE
            srLayout.visibility = View.VISIBLE
            loadData()
        } else {
            llNoDeals.visibility = View.VISIBLE
            srLayout.visibility = View.GONE
        }

        dealsListAdapter.recyclerTouchListener = object : IRecyclerTouchListener<MarketItemMarker> {
            override fun onItemClick(data: MarketItemMarker, position: Int) {
                if((data as MarketItem).purchaseDetailsCanBeOpened())
                    getSwitcher()?.addTo(DealSellFragment::class.java, DealSellFragment.getBundle(data.activePurchase!!.id), false)
                else
                    getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(data), false)
            }
        }
    }

    fun update() {
        if(initialized) {
            loadData()
        }
    }

    fun loadData() {
        if(isLoading)
            return

        showProgress(dealsListAdapter.isEmpty())

        val onSuccess = object : SilentConsumer<NewSellResponse> {
            override fun onConsume(t: NewSellResponse) {

                dealsListAdapter.clear()

                if(t.active.isNotEmpty()) {
                    dealsListAdapter.addData(MarketItemHeader(getString(R.string.str_item_status_active)))
                    for (entry in t.active)
                        dealsListAdapter.addData(entry)
                }

                if(t.waste.isNotEmpty()) {
                    dealsListAdapter.addData(MarketItemHeader(getString(R.string.str_not_active)))
                    for (entry in t.waste)
                        dealsListAdapter.addData(entry)
                }

                if (dealsListAdapter.isEmpty()) {
                    llNoDeals.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    llNoDeals.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
                hideProgress()
                srLayout.isRefreshing = false
                initialized = true
            }
        }
        val onError = object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                handleException(t)
                srLayout.isRefreshing = false
                initialized = true
            }

        }

        DataProvider.getItemsToSell(onSuccess, onError)
    }

    private fun showProgress(center: Boolean) {
        isLoading = true
        if (center) {
            progressCenter?.visibility = View.VISIBLE
        } else {
            progressBottom?.visibility = View.VISIBLE
        }

        hideViewsQuietly(progressCenter, progressBottom)
    }

    private fun hideProgress() {
        hideViewsQuietly(progressCenter, progressBottom)
        isLoading = false
    }

    override fun handleException(t: Throwable) {
        hideProgress()
        super.handleException(t)
        initialized = true
    }

    override fun subscribeOnDealUpdate(id: String) {
        activity?.runOnUiThread {
            loadData()
        }
    }
}