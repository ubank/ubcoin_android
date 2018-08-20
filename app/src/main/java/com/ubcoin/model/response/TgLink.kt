package com.ubcoin.model.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Yuriy Aizenberg
 */
data class TgLink(val url: String?, @SerializedName("app_url") var appUrl: String?)
