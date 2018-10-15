package com.ubcoin.model.ui

import com.ubcoin.model.ui.condition.ConditionType

/**
 * Created by Yuriy Aizenberg
 */
class ConditionFilterModel(conditionType: ConditionType) : FilterModel<ConditionType>(FilterType.CONDITION, conditionType)