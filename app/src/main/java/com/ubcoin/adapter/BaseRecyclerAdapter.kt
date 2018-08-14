package com.ubcoin.adapter

import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Yuriy Aizenberg
 */
abstract class BaseRecyclerAdapter<T, VH : BaseRecyclerAdapter.VHolder>(protected val context: Context) : RecyclerView.Adapter<VH>() {

    var recyclerTouchListener: IRecyclerTouchListener<T>? = null
    var data: MutableList<T> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun addData(data: List<T>) {
        val position = this.data.size
        this.data.addAll(data)
        if (position == 0) notifyDataSetChanged() else notifyItemRangeInserted(position, data.size)
    }

    fun addData(data: List<T>, position: Int) {
        if (this.data.isEmpty()) {
            addData(data)
        } else {
            this.data.addAll(position, data)
            notifyItemRangeInserted(position, data.size)
        }
    }

    fun isEmpty(): Boolean = data.isEmpty()

    fun getItem(position: Int): T = data[position]

    protected fun inflate(@LayoutRes resId: Int, viewGroup: ViewGroup): View {
        return (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(resId, viewGroup, false)
    }

    protected fun bindTouchListener(view: View, position: Int, data: T) {
        view.setOnClickListener {
            recyclerTouchListener?.onItemClick(data, position)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long = position.toLong()

    abstract class VHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun <T : View> findView(@IdRes id: Int) = itemView.findViewById<T>(id)

    }
}