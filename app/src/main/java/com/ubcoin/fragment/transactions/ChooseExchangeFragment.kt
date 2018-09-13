package com.ubcoin.fragment.transactions

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.adapter.ExchangeMarketAdapter
import com.ubcoin.adapter.GridItemOffsetDecoration
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.ExchangeMarket
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer

/**
 * Created by Yuriy Aizenberg
 */
class ChooseExchangeFragment : BaseFragment() {


    private lateinit var exchangeMarketAdapter : ExchangeMarketAdapter
    private lateinit var progressCenter: View

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvExchanges)
        progressCenter = view.findViewById(R.id.progressCenter)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity!!)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))

        exchangeMarketAdapter = ExchangeMarketAdapter(activity!!)
        exchangeMarketAdapter.setHasStableIds(true)
        recyclerView.adapter = exchangeMarketAdapter

        exchangeMarketAdapter.recyclerTouchListener = object : IRecyclerTouchListener<ExchangeMarket> {
            override fun onItemClick(data: ExchangeMarket, position: Int) {
                TheApplication.instance.openExternalLink(activity!!, data.url)
            }
        }

        progressCenter.visibility = View.VISIBLE
        DataProvider.getExchangeMarkets(object : SilentConsumer<List<ExchangeMarket>> {
            override fun onConsume(t: List<ExchangeMarket>) {
                stopLoading()
                exchangeMarketAdapter.addData(t)
            }

        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                handleException(t)
            }

        })
    }

    override fun handleException(t: Throwable) {
        stopLoading()
        super.handleException(t)
    }

    private fun stopLoading() {
        progressCenter.visibility = View.GONE
    }

    override fun getLayoutResId() = R.layout.fragment_choose_exchange

    override fun getHeaderText() = R.string.choose_the_exchange_header

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

}