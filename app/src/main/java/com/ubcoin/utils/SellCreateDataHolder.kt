package com.ubcoin.utils

import com.ubcoin.model.response.Category
import com.ubcoin.model.response.Location
import com.ubcoin.model.ui.condition.ConditionType

/**
 * Created by Yuriy Aizenberg
 */
object SellCreateDataHolder {

    var category: Category? = null
        set(value) {
            hasChanges = true
            field = value
        }

    var condition: ConditionType? = null
        set(value) {
            hasChanges = true
            field = value
        }

    var location: Location? = null
        set(value) {
            hasChanges = true
            field = value
        }

    var hasChanges = false

    var categories = ArrayList<Category>()

    fun reset() {
        category = null
        location = null
        hasChanges = false
        condition = null
    }

    fun isCategoriesLoaded() = !categories.isEmpty()


}