package com.ubcoin.services

import com.onesignal.OSNotificationReceivedResult
import com.onesignal.NotificationExtenderService


class NotificationListener : NotificationExtenderService() {
    override fun onNotificationProcessing(receivedResult: OSNotificationReceivedResult): Boolean {
        // Read properties from result.

        // Return true to stop the notification from displaying.

        return false
    }
}