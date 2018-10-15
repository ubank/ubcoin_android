package com.ubcoin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.model.ui.*
import com.ubcoin.utils.WordUtils
import com.ubcoin.utils.moneyFormat

/**
 * Created by Yuriy Aizenberg
 */
class FilterItemsAdapter(context: Context) : BaseRecyclerAdapter<FilterModel<*>, FilterItemsAdapter.FilterVHolder>(context) {

    var onFilterClear: ((model: FilterModel<*>, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FilterVHolder {
        return FilterVHolder(inflate(R.layout.item_filter_bubble, p0))
    }

    override fun onBindViewHolder(p0: FilterVHolder, p1: Int) {
        val filterModel = getItem(p1)
        when (filterModel) {
            is CategoryFilterModel -> onBindViewHolder(p0, p1, filterModel)
            is ConditionFilterModel -> onBindViewHolder(p0, p1, filterModel)
            is DistanceFilterModel -> onBindViewHolder(p0, p1, filterModel)
            is PriceFilterModel -> onBindViewHolder(p0, p1, filterModel)
            is OrderFilterModel -> onBindViewHolder(p0, p1, filterModel)
            else -> TODO("Type ${p0::class.java.name} not supported")
        }
    }

    private fun onBindViewHolder(holder: FilterVHolder, position: Int, categoryFilterModel: CategoryFilterModel) {
        holder.txtFilterValue.text = categoryFilterModel.model.name
        holder.llFilterRemove.setOnClickListener { onFilterClear?.invoke(categoryFilterModel, position) }
    }

    private fun onBindViewHolder(holder: FilterVHolder, position: Int, conditionFilterModel: ConditionFilterModel) {
        holder.txtFilterValue.text = context.getString(conditionFilterModel.model.getResId())
        holder.llFilterRemove.setOnClickListener { onFilterClear?.invoke(conditionFilterModel, position) }
    }


    @SuppressLint("SetTextI18n")
    private fun onBindViewHolder(holder: FilterVHolder, position: Int, distanceFilterModel: DistanceFilterModel) {
        holder.txtFilterValue.text = """${distanceFilterModel.model} ${context.getString(R.string.distance_km)}"""
        holder.llFilterRemove.setOnClickListener { onFilterClear?.invoke(distanceFilterModel, position) }
    }

    private fun onBindViewHolder(holder: FilterVHolder, position: Int, priceFilterModel: PriceFilterModel) {
        holder.txtFilterValue.text = context.getString(R.string.balance_placeholder, priceFilterModel.model.moneyFormat())
        holder.llFilterRemove.setOnClickListener { onFilterClear?.invoke(priceFilterModel, position) }
    }


    private fun onBindViewHolder(holder: FilterVHolder, position: Int, orderFilterModel: OrderFilterModel) {
        holder.txtFilterValue.text = context.getString(orderFilterModel.model.orderType.getResId())
        holder.llFilterRemove.setOnClickListener { onFilterClear?.invoke(orderFilterModel, position) }
    }


    class FilterVHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
        val txtFilterValue = findView<TextView>(R.id.txtFilterValue)
        val llFilterRemove = findView<View>(R.id.llFilterRemove)
    }
}