package com.ubcoin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.ubcoin.model.response.MarketItem

/**
 * Created by Yuriy Aizenberg
 */

class MarketListAdapter(context: Context) : BaseRecyclerAdapter<MarketItem, MarketListAdapter.ViewHolder>(context) {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    class ViewHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
}
}