package com.ubcoin.model.ui

import com.ubcoin.model.response.Category
import com.ubcoin.model.ui.order.OrderBean

/**
 * Created by Yuriy Aizenberg
 */
class OrderFilterModel(orderBean: OrderBean) : FilterModel<OrderBean>(FilterType.SORT_BY, orderBean)