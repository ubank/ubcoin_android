package com.ubcoin.model.ui.order

/**
 * Created by Yuriy Aizenberg
 */
class OrderBean(
        val orderType: OrderType,
        val orderDirection: OrderDirection) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderBean

        if (orderType != other.orderType) return false
        if (orderDirection != other.orderDirection) return false

        return true
    }

    override fun hashCode(): Int {
        var result = orderType.hashCode()
        result = 31 * result + orderDirection.hashCode()
        return result
    }
}

