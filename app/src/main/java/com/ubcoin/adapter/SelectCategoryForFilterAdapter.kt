package com.ubcoin.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.model.response.Category
import com.ubcoin.model.response.ICategory
import com.ubcoin.utils.filters.FiltersHolder

/**
 * Created by Yuriy Aizenberg
 */
class SelectCategoryForFilterAdapter(context: Context) : BaseRecyclerAdapter<ICategory, SelectCategoryForFilterAdapter.VHolder>(context) {

    var onPositionCallback: ((category: ICategory, position: Int) -> Unit)? = null

    override fun onBindViewHolder(vh: VHolder, position: Int) {
        val item = getItem(position)
        if (item is Category) {
            vh.txtItemCategory.text = item.name
            if (FiltersHolder.hasCategoryInFilter(item.id)) {
                vh.imgItemCategory.setImageResource(R.drawable.ic_check_green)
                vh.txtItemCategory.setTextColor(ContextCompat.getColor(context, R.color.greenMainColor))
                onPositionCallback?.invoke(item, vh.adapterPosition)
            } else {
                vh.txtItemCategory.setTextColor(ContextCompat.getColor(context, R.color.headerTextColor))
                vh.imgItemCategory.setImageDrawable(null)
            }
        } else {
            vh.txtItemCategory.setText(R.string.all_categories)
            if (FiltersHolder.hasAnyCategoryInFilter()) {
                vh.txtItemCategory.setTextColor(ContextCompat.getColor(context, R.color.headerTextColor))
                vh.imgItemCategory.setImageDrawable(null)
            } else {
                vh.imgItemCategory.setImageResource(R.drawable.ic_check_green)
                vh.txtItemCategory.setTextColor(ContextCompat.getColor(context, R.color.greenMainColor))
            }
        }
        bindTouchListener(vh.itemView, vh.adapterPosition, item)

    }

    fun invalidateByPosition(position: Int) {
        if (position == 0) {
            notifyDataSetChanged()
        } else {
            try {
                notifyItemChanged(position)
            } catch (e: Exception) {
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VHolder {
        return VHolder(inflate(R.layout.item_select_category, p0))
    }


    class VHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
        val txtItemCategory: TextView = findView(R.id.txtItemCategory)
        val imgItemCategory: ImageView = findView(R.id.imgItemCategory)
    }

}