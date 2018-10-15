package com.ubcoin.model.ui

import com.ubcoin.model.response.Category

/**
 * Created by Yuriy Aizenberg
 */
class PriceFilterModel(price: Double) : FilterModel<Double>(FilterType.MAX_PRICE, price)