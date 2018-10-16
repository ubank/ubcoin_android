package com.ubcoin.fragment.filter

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ubcoin.R
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.SelectCategoryForFilterAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.Category
import com.ubcoin.model.response.CategoryAll
import com.ubcoin.model.response.ICategory
import com.ubcoin.model.ui.FilterType
import com.ubcoin.model.ui.UpdateFilterEvent
import com.ubcoin.network.DataProvider
import com.ubcoin.utils.filters.FiltersHolder
import io.reactivex.functions.Consumer
import org.greenrobot.eventbus.EventBus

/**
 * Created by Yuriy Aizenberg
 */
const val BUNDLE_FROM_MAIN_SCREEN = "BFMSScreen"
class SelectCategoryFilterFragment : BaseFragment(), Consumer<List<Category>> {

    lateinit var rvCategories: RecyclerView
    lateinit var progressCenter: View
    lateinit var progressBottom: View
    private lateinit var adapter: SelectCategoryForFilterAdapter
    private var isDirectly = false

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        rvCategories = view.findViewById(R.id.rvCategories)
        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)
        setupList()
        if (FiltersHolder.isPopulated()) {
            Handler().postDelayed({
                accept(FiltersHolder.getData())
            }, 200)
        } else {
            queryCategories()
        }
        isDirectly = arguments?.getBoolean(BUNDLE_FROM_MAIN_SCREEN, false)?:false
    }

    private fun queryCategories() {
        progressCenter.visibility = View.VISIBLE
        DataProvider.getCategories(Consumer {
            progressCenter.visibility = View.GONE
            FiltersHolder.onDataLoaded(it)
            accept(it)
        }, Consumer {
            progressCenter.visibility = View.GONE
            handleException(it)
        })
    }

    private fun setupList() {
        adapter = SelectCategoryForFilterAdapter(activity!!)
        adapter.setHasStableIds(true)
        rvCategories.layoutManager = LinearLayoutManager(activity!!)
        rvCategories.setHasFixedSize(true)
        rvCategories.adapter = adapter

        adapter.recyclerTouchListener = object : IRecyclerTouchListener<ICategory> {
            override fun onItemClick(data: ICategory, position: Int) {
                if (data is CategoryAll) {
                    onAllPressed()
                } else {
                    onSingleCategoryPressed(data as Category, position)
                }
            }
        }
        adapter.onPositionCallback = { iCategory: ICategory, i: Int ->
            if (iCategory is Category) {
                FiltersHolder.addFromFilter(iCategory.id, i)
            }
        }
    }


    private fun onSingleCategoryPressed(category: Category, position: Int) {
        val id = category.id
        //Remove from filter
        if (FiltersHolder.hasCategoryInFilter(id)) {
            FiltersHolder.removeFromFilter(id)
            adapter.invalidateByPosition(position)
            if (!FiltersHolder.hasAnyCategoryInFilter()) {
                adapter.invalidateByPosition(0) //Should select 'All categories'
            }
        } else { //Add to filter
            FiltersHolder.addFromFilter(id, position)
            adapter.invalidateByPosition(position)
            adapter.invalidateByPosition(0)
        }
    }

    private fun onAllPressed() {
        if (FiltersHolder.hasAnyCategoryInFilter()) {
            FiltersHolder.clearCategories()
            adapter.notifyDataSetChanged()
        }
    }

    override fun accept(t: List<Category>?) {
        if (isResumed && t != null) {
            val list = ArrayList<ICategory>()
            list.add(CategoryAll())
            t.forEach { list.add(it) }
            adapter.data = list
        }
    }

    override fun getHeaderText(): Int {
        return R.string.category
    }

    override fun getLayoutResId() = R.layout.fragment_filter_categories

    override fun isFooterShow() = false

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        if (FiltersHolder.isCategoriesChanged()) {
            if (isDirectly) {
                FiltersHolder.getChangesAndDropOnlyCategories()
            }
            EventBus.getDefault().post(UpdateFilterEvent(FilterType.CATEGORY, isDirectly))
        }
        return super.onBackPressed()
    }

    companion object {
        fun getBundle(fromMainScreen: Boolean) : Bundle {
            val args = Bundle()
            args.putBoolean(BUNDLE_FROM_MAIN_SCREEN, fromMainScreen)
            return args
        }
    }
}