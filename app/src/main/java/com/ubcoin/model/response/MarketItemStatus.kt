package com.ubcoin.model.response

import com.ubcoin.R

/**
 * Created by Yuriy Aizenberg
 */
const val NO_RESOURCE = -1

enum class MarketItemStatus(val stringResourceId: Int, val description: Int, val inListPositionIndex: Int) {

    ACTIVE(R.string.str_item_status_active, NO_RESOURCE, 0),
    CHECK(R.string.str_item_status_check, R.string.str_status_check, 1),
    CHECKING(R.string.str_item_status_check, R.string.str_status_check, 2),
    BLOCKED(R.string.str_item_status_blocked, R.string.empty, 6),
    DEACTIVATED(R.string.str_item_status_deactivated, NO_RESOURCE, 3),
    RESERVED(R.string.str_item_status_reserved, NO_RESOURCE, 5),
    SOLD(R.string.str_item_status_sold, NO_RESOURCE, 4);

}