package com.ubcoin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.model.ChatItem
import com.ubcoin.model.response.DealItemWrapper
import com.ubcoin.utils.ProfileHolder

class ChatListAdapter(context: Context) : BaseRecyclerAdapter<ChatItem, ChatListAdapter.ViewHolder>(context) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ChatListAdapter.ViewHolder {
        return ChatListAdapter.ViewHolder(inflate(R.layout.item_chat, p0))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val chatItem: ChatItem = getItem(p1)
        p0.tvName.text = chatItem.item!!.title
        p0.tvDescription.text = chatItem.item?.title

        GlideApp.with(context).load(chatItem.item.images?.get(0))
                .centerCrop()
                .placeholder(R.drawable.img_profile_default)
                .error(R.drawable.img_profile_default)
                .into(p0.ivItem)

        var user = chatItem.user

        p0.tvName.text = user.name

        bindTouchListener(p0.itemView, p0.adapterPosition, chatItem)
    }

    class ViewHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {

        val ivItem: ImageView = findView(R.id.ivItem)
        val tvName: TextView = findView(R.id.tvName)
        val tvDescription: TextView = findView(R.id.tvDescription)
        val tvMessage: TextView = findView(R.id.tvMessage)
        val tvDate: TextView = findView(R.id.tvDate)

    }
}