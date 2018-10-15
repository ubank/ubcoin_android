package com.ubcoin.utils.filters

import com.ubcoin.model.response.Category
import com.ubcoin.model.ui.FilterType
import com.ubcoin.model.ui.condition.ConditionType
import com.ubcoin.model.ui.order.OrderDirection
import com.ubcoin.model.ui.order.OrderType
import com.ubcoin.utils.hashCodeKeys
import java.util.*

/**
 * Created by Yuriy Aizenberg
 */
object FiltersHolder {

    private val filtersFromFiltersBean = FilterBean()
    private val selectedBean = FilterBean()
    private var hasChanges = false
    private var hasCategoriesChanges = false

    private val categoriesId = HashMap<String, Int>()
    private val filteredIds = HashMap<String, Int>()
    private val dataSource = LinkedHashMap<String, Category>()


    fun addFromFilter(id: String, position: Int) {
        filteredIds.put(id, position)
    }

    fun removeFromFilter(id: String) {
        filteredIds.remove(id)
    }

    fun removeDirectly(id: String) {
        filteredIds.remove(id)
        categoriesId.remove(id)
    }

    fun onDataLoaded(categories: List<Category>) {
        dataSource.clear()
        categories.forEach {
            dataSource.put(it.id, it)
        }
    }

    fun isPopulated() = dataSource.isNotEmpty()

    fun getData() = ArrayList(dataSource.values)

    fun clearCategories() {
        filteredIds.clear()
    }

    fun iterateByCategories() = filteredIds.entries.iterator()

    private fun applyCategoriesChanged() {
        categoriesId.clear()
        categoriesId.putAll(filteredIds)
    }

    private fun cancelCategoriesChanged() {
        filteredIds.clear()
        filteredIds.putAll(categoriesId)
    }

    fun getFiltersCount() = filteredIds.count()

    fun isCategoriesChanged() = categoriesId.hashCodeKeys() != filteredIds.hashCodeKeys()

    fun hasCategoryInFilter(id: String) = filteredIds.contains(id)

    fun hasAnyCategoryInFilter() = filteredIds.isNotEmpty()

    fun resolveWithOrdering(): List<Category> {
        val list = ArrayList<Category>()
        if (categoriesId.isEmpty()) return list

        var negativeIndex = -100
        val resultRevertedMap = HashMap<Int, String>()
        categoriesId.entries.forEach {
            val key = it.key
            var value = it.value
            if (value == -1) {
                value = negativeIndex
                negativeIndex--
            }
            resultRevertedMap.put(value, key)
        }
        val treeMap = TreeMap<Int, String>(Comparator<Int> { o1, o2 -> o1!!.compareTo(o2!!) })
        treeMap.putAll(resultRevertedMap)
        treeMap.entries.forEach {
            list.add(dataSource.get(it.value)!!)
        }
        return list
    }


    fun resetFilters() {
        filtersFromFiltersBean.reset()
        filteredIds.clear()
    }

    private fun calcChanges(): Boolean {
        hasChanges = filtersFromFiltersBean != selectedBean
        hasCategoriesChanges = isCategoriesChanged()
        return hasChanges || hasCategoriesChanges
    }

    fun getChangesAndDrop(): Boolean {
        val changes = calcChanges()
        hasChanges = false
        hasCategoriesChanges = false
        applyCategoriesChanged()
        selectedBean.applyFrom(filtersFromFiltersBean)
        return changes
    }

    fun cancelChanges() {
        hasChanges = false
        hasCategoriesChanges = false
        cancelCategoriesChanged()
        filtersFromFiltersBean.applyFrom(selectedBean)
    }

    fun onDestroy() {
        hasChanges = false
        hasCategoriesChanges = false
        filtersFromFiltersBean.reset()
        selectedBean.reset()
        categoriesId.clear()
        dataSource.clear()
        filteredIds.clear()
    }

    fun getFilterObjectForFilters() = filtersFromFiltersBean

    fun getFilterObjectForList() = selectedBean

    fun removeFilterDirectly(filterType: FilterType) {
        when (filterType) {
            FilterType.MAX_PRICE -> {
                filtersFromFiltersBean.maxPrice = null
                selectedBean.maxPrice = null
            }
            FilterType.MAX_DISTANCE -> {
                filtersFromFiltersBean.maxDistance = null
                selectedBean.maxDistance = null
            }
            FilterType.CONDITION -> {
                filtersFromFiltersBean.conditionType = ConditionType.NONE
                selectedBean.conditionType = ConditionType.NONE
            }
            FilterType.SORT_BY -> {
                filtersFromFiltersBean.orderBean = null
                selectedBean.orderBean = null
            }
            else -> {
                /*pass*/
            }
        }
    }

    fun getOrderByDate(): String? {
        return getOrderInternal(OrderType.DATE)
    }

    fun getOrderByPrice(): String? {
        return getOrderInternal(OrderType.PRICE)
    }

    fun getOrderByDistance(): String? {
        return getOrderInternal(OrderType.DISTANCE)
    }

    private fun getOrderInternal(requestOrderType: OrderType): String? {
        val orderBean = selectedBean.orderBean
        if (orderBean == null || orderBean.orderDirection == OrderDirection.NONE || orderBean.orderType != requestOrderType) return null
        return orderBean.orderDirection.getQueryRepresentation()
    }

}