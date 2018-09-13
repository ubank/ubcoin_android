package com.ubcoin.fragment.deals

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.ThePreferences
import com.ubcoin.adapter.BuysListAdapter
import com.ubcoin.adapter.SellsListAdapter
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.*
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.network.request.BuyerPurchaseLinkRequest
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.view.OpenTelegramDialogManager

/**
 * Created by Yuriy Aizenberg
 */
private const val LIMIT = 30

class BuyDealsChildFragment : BaseFragment() {

    private lateinit var llNoDeals: View
    private lateinit var sellsListAdapter: BuysListAdapter
    private lateinit var recyclerView: RecyclerView
    private var progressCenter: View? = null
    private var progressBottom: View? = null

    private var isLoading = false
    private var isEndOfLoading = false
    private var currentPage = -1

    override fun getLayoutResId() = R.layout.fragment_deals_child

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        llNoDeals = view.findViewById<RecyclerView>(R.id.llNoDeals)
        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)
        recyclerView = view.findViewById(R.id.rvDeals)

        sellsListAdapter = BuysListAdapter(activity!!)
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


        sellsListAdapter.recyclerTouchListener = object : IRecyclerTouchListener<DealItemWrapper> {
            override fun onItemClick(data: DealItemWrapper, position: Int) {
                val thePreferences = ThePreferences()
                if (thePreferences.shouldShowThDialog()) {
                    OpenTelegramDialogManager.showDialog(activity!!, object : OpenTelegramDialogManager.ITelegramDialogCallback {
                        override fun onPositiveClick(materialDialog: MaterialDialog) {
                            materialDialog.dismiss()
                            thePreferences.disableTgDialog()
                            requestUrlAndOpenApp(data)
                        }
                    })
                } else {
                    requestUrlAndOpenApp(data)
                }
            }
        }

        recyclerView.addOnScrollListener(object : EndlessRecyclerViewOnScrollListener(linearLayoutManager) {
            override fun onLoadMore() {
                loadData()
            }

        })
    }

    fun requestUrlAndOpenApp(data: DealItemWrapper) {
        showProgressDialog(R.string.wait_please_title, R.string.wait_please_message)
        DataProvider.discuss(BuyerPurchaseLinkRequest(data.dealItem.id), object : SilentConsumer<TgLink> {
            override fun onConsume(t: TgLink) {
                hideProgressDialog()
                val fullUrl = t.url
                if (fullUrl.isNotBlank()) {
                    TheApplication.instance.openTelegramIntent(fullUrl, t.appUrl, this@BuyDealsChildFragment, 18888)
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

        if (sellsListAdapter.isEmpty()) {
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
                sellsListAdapter.addData(data)
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
        DataProvider.getBuyersItems(LIMIT, currentPage, onSuccess, onError)
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