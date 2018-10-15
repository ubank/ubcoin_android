package com.ubcoin.model.ui

import com.ubcoin.model.response.Category

/**
 * Created by Yuriy Aizenberg
 */
class CategoryFilterModel(model: Category) : FilterModel<Category>(FilterType.CATEGORY, model)