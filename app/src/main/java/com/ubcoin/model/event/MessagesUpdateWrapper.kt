package com.ubcoin.model.event

import com.onesignal.OSNotification
import com.ubcoin.model.ChatItem

data class MessagesUpdateWrapper(var chatItem: ChatItem?, var notification:OSNotification)