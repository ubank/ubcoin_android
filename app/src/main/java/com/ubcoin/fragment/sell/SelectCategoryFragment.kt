package com.ubcoin.fragment.sell

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ubcoin.R
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.SelectCategoryAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.Category
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.SellCreateDataHolder

/**
 * Created by Yuriy Aizenberg
 */

private const val BUNDLE_KEY = "bundle_key_id"

class SelectCategoryFragment : BaseFragment() {

    private lateinit var progressCenter: View
    private var selectCategoryAdapter: SelectCategoryAdapter? = null
    private var selectedCategory: Category? = null

    companion object {
        fun createBundle(id: String?): Bundle {
            val args = Bundle()
            args.putString(BUNDLE_KEY, id)
            return args
        }
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)

        val selectedId = arguments?.getString(BUNDLE_KEY, null)

        selectCategoryAdapter = SelectCategoryAdapter(activity!!)
        selectCategoryAdapter?.selectedCategoryId = selectedId
        selectCategoryAdapter?.setHasStableIds(true)

        val rvCategories = view.findViewById<RecyclerView>(R.id.rvCategories)
        rvCategories.setHasFixedSize(true)
        rvCategories?.layoutManager = LinearLayoutManager(activity)
        rvCategories.adapter = selectCategoryAdapter


        progressCenter = view.findViewById(R.id.progressCenter)

        view.findViewById<View>(R.id.llHeaderRight).setOnClickListener {
            if (selectedCategory != null) {
                SellCreateDataHolder.category = selectedCategory
            }
            activity?.onBackPressed()
        }

        if (SellCreateDataHolder.isCategoriesLoaded()) {
            selectCategoryAdapter?.data = SellCreateDataHolder.categories
        } else {
            loadCategories()
        }
        selectCategoryAdapter?.recyclerTouchListener = object : IRecyclerTouchListener<Category> {
            override fun onItemClick(data: Category, position: Int) {
                selectedCategory = data
                selectCategoryAdapter?.clearPrevious()
                selectCategoryAdapter?.selectedCategoryId = data.id
                selectCategoryAdapter?.invalidateByPosition(position)
            }

        }
    }

    private fun loadCategories() {
        progressCenter.visibility = View.VISIBLE
        DataProvider.getCategories(object : SilentConsumer<List<Category>> {
            override fun onConsume(t: List<Category>) {
                progressCenter.visibility = View.GONE
                SellCreateDataHolder.categories = ArrayList(t)
                selectCategoryAdapter?.addData(t)
            }
        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                progressCenter.visibility = View.GONE
                handleException(t)
            }
        })
    }


    override fun getLayoutResId() = R.layout.fragment_select_categories

    override fun getHeaderText() = R.string.select_category

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

    override fun isFooterShow() = false


}