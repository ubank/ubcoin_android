package com.ubcoin.fragment.sell

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ubcoin.R
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.SelectConditionAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.Category
import com.ubcoin.model.ui.condition.ConditionType
import com.ubcoin.utils.SellCreateDataHolder

private const val BUNDLE_KEY = "bundle_key_id"

class SelectConditionFragment : BaseFragment() {

    private var selectConditionAdapter: SelectConditionAdapter? = null
    private var selectedCondition: ConditionType? = null

    companion object {
        fun createBundle(id: Int?): Bundle {
            val args = Bundle()
            if(id == null)
                args.putInt(BUNDLE_KEY, ConditionType.NEW.ordinal)
            else
                args.putInt(BUNDLE_KEY, id)
            return args
        }
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)

        val selectedId = arguments?.getInt(BUNDLE_KEY, 0)

        selectConditionAdapter = SelectConditionAdapter(activity!!)
        selectConditionAdapter?.selectedCategoryId = selectedId
        selectConditionAdapter?.setHasStableIds(true)

        val rvCategories = view.findViewById<RecyclerView>(R.id.rvCategories)
        rvCategories.setHasFixedSize(true)
        rvCategories?.layoutManager = LinearLayoutManager(activity)
        rvCategories.adapter = selectConditionAdapter

        view.findViewById<View>(R.id.llHeaderRight).setOnClickListener {
            if (selectedCondition != null) {
                SellCreateDataHolder.condition = selectedCondition
            }
            activity?.onBackPressed()
        }

        var conditions = ArrayList<ConditionType>()
        conditions.add(ConditionType.NEW)
        conditions.add(ConditionType.USED)
        selectConditionAdapter!!.addData(conditions)
        selectConditionAdapter!!.notifyDataSetChanged()

        selectConditionAdapter?.recyclerTouchListener = object : IRecyclerTouchListener<ConditionType> {
            override fun onItemClick(data: ConditionType, position: Int) {
                selectedCondition = data
                selectConditionAdapter?.clearPrevious()
                selectConditionAdapter?.selectedCategoryId = data.ordinal
                selectConditionAdapter?.invalidateByPosition(position)
            }

        }
    }


    override fun getLayoutResId() = R.layout.fragment_select_categories

    override fun getHeaderText() = R.string.text_select_condition

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

    override fun isFooterShow() = false


}