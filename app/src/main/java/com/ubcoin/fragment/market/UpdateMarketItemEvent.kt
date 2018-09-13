package com.ubcoin.fragment.market

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */

data class UpdateMarketItemEvent(val position: Int, val isFavorite: Boolean) : Serializable