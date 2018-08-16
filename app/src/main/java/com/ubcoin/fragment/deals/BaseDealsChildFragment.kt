package com.ubcoin.fragment.deals

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ubcoin.R
import com.ubcoin.adapter.DealsListAdapter
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.market.MarketDetailsFragment
import com.ubcoin.model.response.DealItemWrapper
import com.ubcoin.model.response.DealsListResponse
import com.ubcoin.model.response.MarketItem
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import kotlinx.android.synthetic.main.fragment_deals_child.*

/**
 * Created by Yuriy Aizenberg
 */
private const val LIMIT = 30

abstract class BaseDealsChildFragment : BaseFragment() {

    private lateinit var llNoDeals: View
    private lateinit var dealsListAdapter: DealsListAdapter
    private lateinit var recyclerView: RecyclerView
    private var progressCenter: View? = null
    private var progressBottom: View? = null

    enum class Type {
        SELL,
        BUY
    }

    private var isLoading = false
    private var isEndOfLoading = false
    private var currentPage = -1

    abstract fun getFragmentType(): Type

    override fun getLayoutResId() = R.layout.fragment_deals_child

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
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

        loadData()


        dealsListAdapter.recyclerTouchListener = object : IRecyclerTouchListener<DealItemWrapper> {
            override fun onItemClick(data: DealItemWrapper, position: Int) {
                showProgressDialog("Wait please", "Wait please")
                DataProvider.getMarketItemById(data.dealItem.id, object : SilentConsumer<MarketItem> {
                    override fun onConsume(t: MarketItem) {
                        hideProgress()
                        getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(t), true)
                    }
                }, object : SilentConsumer<Throwable> {
                    override fun onConsume(t: Throwable) {
                        handleException(t)
                    }

                })
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

        if (dealsListAdapter.isEmpty()) {
            progressCenter?.visibility = View.VISIBLE
        } else {
            progressBottom?.visibility = View.VISIBLE
        }

        val onSuccess = object : SilentConsumer<DealsListResponse> {
            override fun onConsume(t: DealsListResponse) {
                val data = t.data
                hideProgress()
                if (data.size < LIMIT) {
                    isEndOfLoading = true
                }
                dealsListAdapter.addData(data)
                if (dealsListAdapter.isEmpty()) {
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
        if (getFragmentType() == Type.BUY) {
            DataProvider.getBuyersItems(LIMIT, currentPage, onSuccess, onError)
        } else {
            DataProvider.getSellerItems(LIMIT, currentPage, onSuccess, onError)
        }
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