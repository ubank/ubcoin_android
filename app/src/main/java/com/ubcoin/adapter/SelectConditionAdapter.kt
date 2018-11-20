package com.ubcoin.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.model.ui.condition.ConditionType

class SelectConditionAdapter(context: Context) : BaseRecyclerAdapter<ConditionType, SelectConditionAdapter.VHolder>(context) {

    private var selectedItemPosition = -1
    var selectedCategoryId: Int? = null

    override fun onBindViewHolder(vh: VHolder, position: Int) {
        val item = getItem(position)
        vh.txtItemCategory.text = context.getString(item.getResId())
        if (selectedCategoryId != null && selectedCategoryId == item.ordinal) {
            vh.imgItemCategory.setImageResource(R.drawable.ic_check_green)
            vh.txtItemCategory.setTextColor(ContextCompat.getColor(context, R.color.greenMainColor))
            selectedItemPosition = vh.adapterPosition
        } else {
            vh.txtItemCategory.setTextColor(ContextCompat.getColor(context, R.color.headerTextColor))
            vh.imgItemCategory.setImageDrawable(null)
        }
        bindTouchListener(vh.itemView, vh.adapterPosition, item)
    }

    fun clearPrevious() {
        if (selectedItemPosition != -1) {
            invalidateByPosition(selectedItemPosition)
        }

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