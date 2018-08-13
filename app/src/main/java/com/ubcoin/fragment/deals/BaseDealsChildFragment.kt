package com.ubcoin.fragment.deals

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ubcoin.R
import com.ubcoin.adapter.DealsListAdapter
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.market.MarketDetailsFragment
import com.ubcoin.model.response.MarketItem
import com.ubcoin.model.response.base.MarketListResponse
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_deals_child.*

/**
 * Created by Yuriy Aizenberg
 */
abstract class BaseDealsChildFragment : BaseFragment() {

    private lateinit var llNoDeals: View
    private lateinit var dealsListAdapter: DealsListAdapter
    private var progressCenter: View? = null

    enum class Type {
        SELL,
        BUY
    }

    abstract fun getFragmentType(): Type

    override fun getLayoutResId() = R.layout.fragment_deals_child

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        llNoDeals = view.findViewById<RecyclerView>(R.id.llNoDeals)
        progressCenter = view.findViewById(R.id.progressCenter)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvDeals)

        dealsListAdapter = DealsListAdapter(activity!!)
        dealsListAdapter.setHasStableIds(true)

        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = dealsListAdapter

        progressCenter?.visibility = View.VISIBLE
        DataProvider.getDeals(2, 0, getFragmentType(), object : SilentConsumer<MarketListResponse> {
            override fun onConsume(t: MarketListResponse) {
                hideProgress()
                dealsListAdapter.run {
                    addData(t.data)
                    if (isEmpty()) {
                        llNoDeals.visibility = View.VISIBLE
                        rvDeals?.visibility = View.GONE
                    } else {
                        llNoDeals.visibility = View.GONE
                        rvDeals?.visibility = View.VISIBLE
                    }
                }
            }

        }, Consumer {
            handleException(it)
        })

        dealsListAdapter.recyclerTouchListener = object : IRecyclerTouchListener<MarketItem> {
            override fun onItemClick(data: MarketItem, position: Int) {
                getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(data), true)
            }

        }
    }

    private fun hideProgress() {
        hideViewsQuietly(progressCenter)
    }

    override fun handleException(t: Throwable) {
        hideProgress()
        super.handleException(t)
    }

}