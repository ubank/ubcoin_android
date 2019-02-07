package com.ubcoin.model.response

import com.google.gson.annotations.SerializedName
import com.ubcoin.R

/**
 * Created by Yuriy Aizenberg
 */
const val NO_RESOURCE = -1

enum class MarketItemStatus(val stringResourceId: Int, val description: Int, val groupKey: Int) {

    @SerializedName("ACTIVE")
    ACTIVE(R.string.str_item_status_active, NO_RESOURCE, 1),

    @SerializedName("CHECK")
    CHECK(R.string.str_item_status_check, R.string.str_status_check, 2),

    @SerializedName("CHECKING")
    CHECKING(R.string.str_item_status_check, R.string.str_status_check, 2),

    @SerializedName("BLOCKED")
    BLOCKED(R.string.str_item_status_blocked, R.string.empty, 2),

    @SerializedName("DEACTIVATED")
    DEACTIVATED(R.string.str_item_status_deactivated, R.string.str_status_deactivated, 2),

    @SerializedName("RESERVED")
    RESERVED(R.string.str_item_status_reserved, NO_RESOURCE, 1),

    @SerializedName("SOLD")
    SOLD(R.string.str_item_status_sold, NO_RESOURCE, 2),

    @SerializedName("DELIVERY")
    DELIVERY(R.string.str_item_status_delivery, NO_RESOURCE, 1),

    @SerializedName("DELIVERY_PRICE_DEFINED")
    DELIVERY_PRICE_DEFINED(R.string.str_item_status_delivery_price_defined, NO_RESOURCE, 1),

    @SerializedName("DELIVERY_PRICE_CONFIRMED")
    DELIVERY_PRICE_CONFIRMED(R.string.str_item_status_delivery_price_confirmed, NO_RESOURCE, 1),

    @SerializedName("CANCELLED")
    CANCELLED(R.string.str_item_status_delivery_price_confirmed, NO_RESOURCE, 1);

    companion object {
        fun bySplitKey(key: Int) : Int {
            if(key == 2)
                return R.string.str_not_active
            return ACTIVE.stringResourceId
        }
    }

    fun whatCanOwnerDo() : DoActions {
        return when (this) {
            MarketItemStatus.ACTIVE -> DoActions.EDIT_OR_DEACTIVATE
            MarketItemStatus.CHECK , MarketItemStatus.BLOCKED -> DoActions.EDIT_ONLY
            MarketItemStatus.DEACTIVATED -> DoActions.EDIT_OR_ACTIVATE
            MarketItemStatus.CHECKING, CANCELLED, MarketItemStatus.RESERVED,  MarketItemStatus.SOLD, MarketItemStatus.DELIVERY, MarketItemStatus.DELIVERY_PRICE_CONFIRMED, MarketItemStatus.DELIVERY_PRICE_DEFINED -> DoActions.NOTHING
        }
    }

    enum class DoActions {
        NOTHING,
        EDIT_OR_DEACTIVATE,
        EDIT_OR_ACTIVATE,
        EDIT_ONLY
    }



}