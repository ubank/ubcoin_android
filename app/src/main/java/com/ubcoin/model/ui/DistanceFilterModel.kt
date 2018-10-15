package com.ubcoin.model.ui

import com.ubcoin.model.response.Category

/**
 * Created by Yuriy Aizenberg
 */
class DistanceFilterModel(distance: Int) : FilterModel<Int>(FilterType.MAX_DISTANCE, distance)