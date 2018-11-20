package com.ubcoin.fragment.market

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.OvershootInterpolator
import com.google.android.gms.maps.model.LatLng
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.adapter.FilterItemsAdapter
import com.ubcoin.adapter.GridItemOffsetDecoration
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.MarketListAdapter
import com.ubcoin.fragment.FirstLineFragment
import com.ubcoin.fragment.filter.FiltersFragment
import com.ubcoin.fragment.filter.SelectCategoryFilterFragment
import com.ubcoin.fragment.sell.MarketUpdateEvent
import com.ubcoin.model.response.MarketItem
import com.ubcoin.model.ui.*
import com.ubcoin.model.ui.condition.ConditionType
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.utils.filters.FiltersHolder
import com.ubcoin.utils.gone
import com.ubcoin.utils.visible
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
    private lateinit var rvFiltersInMarketList: RecyclerView
    private lateinit var filterContainer: View
    private lateinit var progressCenter: View
    private lateinit var progressBottom: View
    private lateinit var llHeaderRight: View
    private lateinit var llNoItems: View
    private var marketListAdapter: MarketListAdapter? = null
    private var filterAdapter: FilterItemsAdapter? = null
    private var currentDisposableLoader: Disposable? = null

    private var currentPage = 0
    private var isLoading = false
    private var isEndOfLoading = false
    private var fixedLocationLatLng: LatLng? = null

    private val isFavoriteProcessing = AtomicBoolean(false)

    override fun getLayoutResId() = R.layout.fragment_market_list

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        recyclerView = view.findViewById(R.id.rvMarketList)
        if (TheApplication.instance.currentLocation != null) {
            fixedLocationLatLng = TheApplication.instance.copyCurrentLocation()
        }

        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)
        llHeaderRight = view.findViewById(R.id.llHeaderRight)
        llHeaderRight.setOnClickListener { getSwitcher()?.addTo(FiltersFragment::class.java) }

        llNoItems = view.findViewById(R.id.llNoItems)
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
        filterContainer = view.findViewById(R.id.filterContainer)

        rvFiltersInMarketList = view.findViewById(R.id.rvFiltersInMarketList)
        rvFiltersInMarketList.setHasFixedSize(true)
        rvFiltersInMarketList.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)

        filterAdapter = FilterItemsAdapter(activity!!)
        filterAdapter!!.setHasStableIds(true)
        filterAdapter!!.onFilterClear = { filterModel: FilterModel<*>, i: Int ->
            filterAdapter!!.data.removeAt(i)
            if (i == 0) {
                filterAdapter!!.notifyDataSetChanged()
            } else {
                filterAdapter!!.notifyItemRemoved(i)
            }
            if (filterAdapter!!.isEmpty()) {
                filterContainer.gone()
            }
            when (filterModel) {
                is CategoryFilterModel -> FiltersHolder.removeDirectly(filterModel.model.id)
                is ConditionFilterModel -> FiltersHolder.removeFilterDirectly(FilterType.CONDITION)
                is DistanceFilterModel -> FiltersHolder.removeFilterDirectly(FilterType.MAX_DISTANCE)
                is PriceFilterModel -> FiltersHolder.removeFilterDirectly(FilterType.MAX_PRICE)
                is OrderFilterModel -> FiltersHolder.removeFilterDirectly(FilterType.SORT_BY)
            }
            resetLoading()
        }

        rvFiltersInMarketList.adapter = filterAdapter!!
        fetchFilters()
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
        llNoItems.gone()
        val categoriesIds: ArrayList<String>?
        if (FiltersHolder.hasAnyCategoryInFilter()) {
            categoriesIds = ArrayList()
            FiltersHolder.resolveWithOrdering().forEach {
                categoriesIds.add(it.id)
            }
        } else {
            categoriesIds = null
        }
        currentDisposableLoader = DataProvider.getMarketList(LIMIT, currentPage,
                fixedLocationLatLng?.latitude, fixedLocationLatLng?.longitude,
                categoriesIds,
                FiltersHolder.getFilterObjectForList().maxPrice,
                FiltersHolder.getFilterObjectForList().maxDistance,
                FiltersHolder.getOrderByDate(),
                FiltersHolder.getOrderByPrice(),
                FiltersHolder.getOrderByDistance(),
                FiltersHolder.getCondition(),
                Consumer {
                    if (it.data.size < LIMIT) {
                        isEndOfLoading = true
                    }
                    currentPage++
                    isLoading = false
                    hideViewsQuietly(progressCenter, progressBottom)
                    marketListAdapter?.addData(it.data)
                    if (marketListAdapter?.isEmpty() != false) {
                        llNoItems.visible()
                    } else {
                        llNoItems.gone()
                    }
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
    fun onFilterEvent(event: UpdateFilterEvent) {
        if (event.type != FilterType.CATEGORY || event.directly) {
            fetchFilters()
            resetLoading()
        }
    }

    private fun resetLoading() {
        isLoading = false
        isEndOfLoading = false
        currentPage = 0
        marketListAdapter?.clear()
        cancelCurrentLoading()
        //ReFix location
        if (TheApplication.instance.currentLocation != null) {
            fixedLocationLatLng = TheApplication.instance.copyCurrentLocation()
        }
        loadData()
    }

    private fun fetchFilters() {
        val arrayList = ArrayList<FilterModel<*>>()
        if (FiltersHolder.hasAnyCategoryInFilter()) {
            val ordering = FiltersHolder.resolveWithOrdering()
            ordering.forEach {
                arrayList.add(CategoryFilterModel(it))
            }
        }

        val filters = FiltersHolder.getFilterObjectForFilters()
        if (filters.maxDistance != null) {
            arrayList.add(DistanceFilterModel(filters.maxDistance!!))
        }
        if (filters.maxPrice != null) {
            arrayList.add(PriceFilterModel(filters.maxPrice!!))
        }
        if (filters.conditionType != ConditionType.NONE) {
            arrayList.add(ConditionFilterModel(filters.conditionType))
        }
        if (filters.orderBean != null) {
            arrayList.add(OrderFilterModel(filters.orderBean!!))
        }

        if (arrayList.isNotEmpty()) {
            filterAdapter!!.data = arrayList
            filterContainer.visible()
        } else {
            filterAdapter!!.clear()
            filterContainer.gone()
        }
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

    override fun onDestroyView() {
        FiltersHolder.onDestroy()
        super.onDestroyView()
    }

    override fun getHeaderIcon() = R.drawable.ic_category

    override fun onIconClick() {
        super.onIconClick()
        getSwitcher()?.addTo(SelectCategoryFilterFragment::class.java, SelectCategoryFilterFragment.getBundle(true), true)
    }
}