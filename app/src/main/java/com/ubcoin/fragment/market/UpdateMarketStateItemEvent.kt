package com.ubcoin.fragment.market

import com.ubcoin.model.response.MarketItem
import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class UpdateMarketStateItemEvent(val marketItem: MarketItem) : Serializable