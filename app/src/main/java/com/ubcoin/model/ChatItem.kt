package com.ubcoin.model

import com.ubcoin.model.response.MarketItem
import com.ubcoin.model.response.User
import com.ubcoin.utils.getDateWithServerTimeStamp
import java.util.*

class ChatItem {
    var item: MarketItem? = null
    var user: User? = null
    var unreadCount: Int = 0
    var lastMessage:SystemMessageItem? = null
}

class SystemMessageItem {
    var id:String? = null
    var userName:String? = null
    var date:String? = null
    var msg:MessageItem? = null

    fun getDate() : Date {
        return date!!.getDateWithServerTimeStamp()
    }

}

class MessageItem {
    var type:String = ""
    var content:String = ""
    var publisher:String = ""
}