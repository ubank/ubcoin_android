package com.ubcoin.fragment.deals

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ubcoin.R
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.SellsListAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.market.DealPurchaseFragment
import com.ubcoin.fragment.market.MarketDetailsFragment
import com.ubcoin.model.response.*
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.network.request.BuyerPurchaseLinkRequest
import com.ubcoin.utils.ProfileHolder

/**
 * Created by Yuriy Aizenberg
 */
private const val LIMIT = 30

class BuyDealsChildFragment : BaseFragment() {

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
                if((data as MarketItem).status != MarketItemStatus.ACTIVE && (data as MarketItem).purchases.size > 0)
                    getSwitcher()?.addTo(DealPurchaseFragment::class.java, DealPurchaseFragment.getBundle((data as MarketItem).purchases.get(0).id), false)
                else
                    getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(data as MarketItem), false)
            }
        }
    }

    fun requestUrlAndOpenApp(data: DealItemWrapper) {
        showProgressDialog(R.string.wait_please_title, R.string.wait_please_message)
        DataProvider.discussFromBuyer(BuyerPurchaseLinkRequest(data.item.id), object : SilentConsumer<TgLink> {
            override fun onConsume(t: TgLink) {
                hideProgressDialog()
                val fullUrl = t.url
                if (fullUrl.isNotBlank()) {
                    //TheApplication.instance.openTelegramIntent(fullUrl, t.appUrl, this@BuyDealsChildFragment, 18888)
                }
            }

        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                handleException(t)
            }

        })
    }

    fun loadData() {
        if (sellsListAdapter.isEmpty()) {
            progressCenter?.visibility = View.VISIBLE
        } else {
            progressBottom?.visibility = View.VISIBLE
        }

        val onSuccess = object : SilentConsumer<NewSellResponse> {
            override fun onConsume(t: NewSellResponse) {

                sellsListAdapter.clear()

                if(t.active != null) {
                    sellsListAdapter.addData(MarketItemHeader(getString(R.string.str_item_status_active)))
                    for (entry in t.active)
                        sellsListAdapter.addData(entry)
                }

                if(t.waste != null) {
                    sellsListAdapter.addData(MarketItemHeader(getString(R.string.str_not_active)))
                    for (entry in t.waste)
                        sellsListAdapter.addData(entry)
                }

                if (sellsListAdapter.isEmpty()) {
                    llNoDeals.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    llNoDeals.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
                hideProgress()
            }
        }
        val onError = object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                handleException(t)
            }

        }

        DataProvider.getItemsToBuy(onSuccess, onError)
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