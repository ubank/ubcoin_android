package com.ubcoin.utils.filters

import com.ubcoin.model.ui.condition.ConditionType
import com.ubcoin.model.ui.order.OrderBean

/**
 * Created by Yuriy Aizenberg
 */
class FilterBean {

    var maxPrice: Double?= null
    var maxDistance: Int?= null
    var conditionType: ConditionType = ConditionType.NONE
    var orderBean: OrderBean?= null

    fun applyFrom(another: FilterBean) {
        maxPrice = another.maxPrice
        maxDistance = another.maxDistance
        conditionType = another.conditionType
        orderBean = another.orderBean
    }

    fun reset() {
        maxPrice = null
        maxDistance = null
        conditionType = ConditionType.NONE
        orderBean = null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FilterBean

        if (maxPrice != other.maxPrice) return false
        if (maxDistance != other.maxDistance) return false
        if (conditionType != other.conditionType) return false
        if (orderBean != other.orderBean) return false

        return true
    }

    override fun hashCode(): Int {
        var result = maxPrice?.hashCode() ?: 0
        result = 31 * result + (maxDistance ?: 0)
        result = 31 * result + conditionType.hashCode()
        result = 31 * result + (orderBean?.hashCode() ?: 0)
        return result
    }


}