package com.ubcoin.model.response

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class Location(val text: String, val latPoint: String?, val longPoint: String?) : Serializable