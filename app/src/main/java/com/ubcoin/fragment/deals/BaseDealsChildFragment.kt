package com.ubcoin.fragment.deals

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.adapter.DealsListAdapter
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.DealItemWrapper
import com.ubcoin.model.response.DealsListResponse
import com.ubcoin.model.response.TgLink
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import com.ubcoin.utils.ProfileHolder

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

        if (ProfileHolder.isAuthorized()) {
            llNoDeals.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            loadData()
        } else {
            llNoDeals.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }


        dealsListAdapter.recyclerTouchListener = object : IRecyclerTouchListener<DealItemWrapper> {
            override fun onItemClick(data: DealItemWrapper, position: Int) {
                requestUrlAndOpenApp(data)
            }
        }

        recyclerView.addOnScrollListener(object : EndlessRecyclerViewOnScrollListener(linearLayoutManager) {
            override fun onLoadMore() {
                loadData()
            }

        })
    }

    fun requestUrlAndOpenApp(data: DealItemWrapper) {
        showProgressDialog("Wait please", "Wait please")
        DataProvider.discuss(data.dealItem.id, object : SilentConsumer<TgLink> {
            override fun onConsume(t: TgLink) {
                hideProgressDialog()
                val fullUrl = t.url
                if (fullUrl.isNotBlank()) {
                    TheApplication.instance.openTelegramIntent(fullUrl, t.appUrl, this@BaseDealsChildFragment, 18888)
                }
            }

        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                handleException(t)
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