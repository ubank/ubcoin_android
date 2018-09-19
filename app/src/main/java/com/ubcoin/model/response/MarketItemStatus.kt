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
    BLOCKED(R.string.str_item_status_blocked, R.string.empty, 3),

    @SerializedName("DEACTIVATED")
    DEACTIVATED(R.string.str_item_status_deactivated, R.string.str_status_deactivated, 4),

    @SerializedName("RESERVED")
    RESERVED(R.string.str_item_status_reserved, NO_RESOURCE, 5),

    @SerializedName("SOLD")
    SOLD(R.string.str_item_status_sold, NO_RESOURCE, 6);

    companion object {
        fun bySplitKey(key: Int) : MarketItemStatus {
            values().forEach {
                if (it.groupKey == key) return it
            }
            return ACTIVE
        }
    }

    fun whatCanOwnerDo() : DoActions {
        return when (this) {
            MarketItemStatus.ACTIVE -> DoActions.EDIT_OR_DEACTIVATE
            MarketItemStatus.CHECK , MarketItemStatus.BLOCKED -> DoActions.EDIT_ONLY
            MarketItemStatus.DEACTIVATED -> DoActions.EDIT_OR_ACTIVATE
            MarketItemStatus.CHECKING, MarketItemStatus.RESERVED,  MarketItemStatus.SOLD -> DoActions.NOTHING
        }
    }

    enum class DoActions {
        NOTHING,
        EDIT_OR_DEACTIVATE,
        EDIT_OR_ACTIVATE,
        EDIT_ONLY
    }



}