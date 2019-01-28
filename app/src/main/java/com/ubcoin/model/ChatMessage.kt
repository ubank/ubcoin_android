package com.ubcoin.model

import java.util.*

class ChatMessage {
    var type: ChatMessageType = ChatMessageType.MyMessage
    var data: String = ""
    var date: Date? = null
}

enum class ChatMessageType{
    MyMessage,
    OpponentMessage,
    MyImage,
    OpponentImage,
    Date
}