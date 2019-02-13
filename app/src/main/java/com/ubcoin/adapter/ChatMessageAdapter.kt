package com.ubcoin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.model.ChatMessage
import com.ubcoin.model.ChatMessageType
import java.text.SimpleDateFormat
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri


class ChatMessageAdapter(context: Context) : BaseRecyclerAdapter<ChatMessage, ChatMessageAdapter.ViewHolder>(context) {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ChatMessageAdapter.ViewHolder {
        when(p1) {
            ChatMessageType.Date.ordinal -> return ChatMessageAdapter.ViewHolder(inflate(R.layout.item_chat_date, p0))
            ChatMessageType.MyImage.ordinal -> return ChatMessageAdapter.ViewHolder(inflate(R.layout.item_chat_my_image, p0))
            ChatMessageType.OpponentImage.ordinal -> return ChatMessageAdapter.ViewHolder(inflate(R.layout.item_chat_opponent_image, p0))
            ChatMessageType.MyMessage.ordinal -> return ChatMessageAdapter.ViewHolder(inflate(R.layout.item_chat_my_message, p0))
            ChatMessageType.OpponentMessage.ordinal -> return ChatMessageAdapter.ViewHolder(inflate(R.layout.item_chat_opponent_message, p0))
        }
        return ChatMessageAdapter.ViewHolder(inflate(R.layout.item_chat_date, p0))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val message: ChatMessage = getItem(p1)

        var sdf = SimpleDateFormat("HH:mm")

        when(message.type) {
            ChatMessageType.Date -> {
                sdf = SimpleDateFormat("dd MMM")
            }
            ChatMessageType.MyImage -> {
                if(p0.ivImage != null)
                GlideApp.with(context).load(message.data)
                    .centerCrop()
                    .placeholder(R.drawable.img_profile_default)
                    .error(R.drawable.img_profile_default)
                    .into(p0.ivImage)

                p0.ivImage?.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(message.data))
                    context.startActivity(browserIntent)
                }
            }
            ChatMessageType.OpponentImage -> {
                if(p0.ivImage != null)
                GlideApp.with(context).load(message.data)
                    .centerCrop()
                    .placeholder(R.drawable.img_profile_default)
                    .error(R.drawable.img_profile_default)
                    .into(p0.ivImage)

                p0.ivImage?.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(message.data))
                    context.startActivity(browserIntent)
                }
            }
            ChatMessageType.MyMessage -> {p0.tvText?.text = message.data}
            ChatMessageType.OpponentMessage -> {p0.tvText?.text = message.data}
        }


        p0.tvDate?.text = sdf.format(message.date)

    }

    class ViewHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
        val tvText: TextView? = findView(R.id.tvText)
        val ivImage: ImageView? = findView(R.id.ivImage)
        val tvDate: TextView? = findView(R.id.tvDate)
    }
}