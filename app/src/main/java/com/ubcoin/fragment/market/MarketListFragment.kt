package com.ubcoin.fragment.market

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import com.ubcoin.R
import com.ubcoin.adapter.GridItemOffsetDecoration
import com.ubcoin.adapter.MarketListAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.network.DataProvider
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import jp.wasabeef.recyclerview.animators.FadeInAnimator

/**
 * Created by Yuriy Aizenberg
 */

class MarketListFragment : BaseFragment() {

    private var recyclerView: RecyclerView? = null
    private var progressCenter: View? = null
    private var progressBottom: View? = null
    private var marketListAdapter: MarketListAdapter? = null
    private var currentDisposableLoader : Disposable?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_market_list, container, false)
        recyclerView = view.findViewById(R.id.rvMarketList)

        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)

        marketListAdapter = MarketListAdapter(activity!!)
        marketListAdapter?.setHasStableIds(true)

        val gridLayoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)


        recyclerView?.run {
            setHasFixedSize(true)
            addItemDecoration(GridItemOffsetDecoration(activity!!, R.dimen.decoration_grid_offset))
            adapter = marketListAdapter
            layoutManager = gridLayoutManager
            val fadeInAnimator = FadeInAnimator(OvershootInterpolator(1f))
            fadeInAnimator.addDuration = 300
            itemAnimator = fadeInAnimator
        }

        progressCenter?.visibility = View.VISIBLE
        cancelCurrentLoading()
        currentDisposableLoader = DataProvider.getMarketList(30, 0,
                Consumer {
                    hideViewQuitelly(progressCenter, progressBottom)
                    marketListAdapter?.addData(it.data)
                },
                Consumer {
                    handleException(it)
                })

        return view
    }

    private fun cancelCurrentLoading() = currentDisposableLoader?.dispose()

    override fun handleException(t: Throwable) {
        hideViewQuitelly(progressCenter, progressBottom)
        super.handleException(t)
    }

    override fun getHeaderText(): Int {
        return R.string.app_name
    }


}