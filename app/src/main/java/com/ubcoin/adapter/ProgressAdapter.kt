package com.ubcoin.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.model.Progress
import com.ubcoin.model.response.DealItemWrapper
import com.ubcoin.utils.ProfileHolder

class ProgressAdapter(_selected: Int, context: Context) : BaseRecyclerAdapter<Progress, ProgressAdapter.ViewHolder>(context) {

    var selected = 0
    init {
        selected = _selected
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ProgressAdapter.ViewHolder {
        return ProgressAdapter.ViewHolder(inflate(R.layout.item_deal_status, p0))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val progress: Progress = getItem(p1)
        p0.tvDescription.text = progress.description
        p0.tvText.text = progress.title

        if(p1 == 0)
            p0.rlTop.visibility = View.GONE
        else
            p0.rlTop.visibility = View.VISIBLE

        if(p1 == itemCount -1)
            p0.rlBottom.visibility = View.GONE
        else
            p0.rlBottom.visibility = View.VISIBLE

        if(p1 <= selected) {
            p0.rlTop.setBackgroundColor(context.resources.getColor(R.color.underlineColor))
            p0.rlCenter.background = context.resources.getDrawable(R.drawable.green_circle)
            p0.tvText.setTextColor(Color.parseColor("#403d45"))
            p0.tvDescription.setTextColor(Color.parseColor("#919191"))
        }
        else {
            p0.rlTop.setBackgroundColor(Color.parseColor("#18202022"))
            p0.rlCenter.background = context.resources.getDrawable(R.drawable.gray_circle)
            p0.tvText.setTextColor(Color.parseColor("#4c403d45"))
            p0.tvDescription.setTextColor(Color.parseColor("#4c919191"))
        }

        if(p1 + 1 <= selected)
            p0.rlBottom.setBackgroundColor(context.resources.getColor(R.color.underlineColor))
        else
            p0.rlBottom.setBackgroundColor(Color.parseColor("#18202022"))

        bindTouchListener(p0.itemView, p0.adapterPosition, progress)
    }

    class ViewHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {

        val tvText: TextView = findView(R.id.tvText)
        val tvDescription: TextView = findView(R.id.tvDescription)
        val rlCenter: TextView = findView(R.id.rlCenter)
        val rlTop: TextView = findView(R.id.rlTop)
        val rlBottom: ImageView = findView(R.id.rlBottom)
    }
}