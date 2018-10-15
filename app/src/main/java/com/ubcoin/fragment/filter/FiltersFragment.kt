package com.ubcoin.fragment.filter

import android.annotation.SuppressLint
import android.support.annotation.IdRes
import android.view.View
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.ui.FilterType
import com.ubcoin.model.ui.UpdateFilterEvent
import com.ubcoin.model.ui.condition.ConditionType
import com.ubcoin.model.ui.order.OrderBean
import com.ubcoin.model.ui.order.OrderDirection
import com.ubcoin.model.ui.order.OrderType
import com.ubcoin.utils.MaxValueInputFilter
import com.ubcoin.utils.filters.FiltersHolder
import com.ubcoin.utils.moneyFormat
import com.ubcoin.view.filter.OrderView
import com.ubcoin.view.filter.SelectableView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat

/**
 * Created by Yuriy Aizenberg
 */
class FiltersFragment : BaseFragment() {

    private lateinit var llHeaderRight: View
    private lateinit var edtCategory: MaterialEditText
    private lateinit var touchCategory: View
    private lateinit var edtMaxPrice: MaterialEditText
    private lateinit var touchMaxPrice: View

    private lateinit var selectDistance1: SelectableView
    private lateinit var selectDistance5: SelectableView
    private lateinit var selectDistance10: SelectableView
    private lateinit var selectDistance100: SelectableView
    private val distanceViews = ArrayList<SelectableView>()

    private lateinit var selectConditionNew: SelectableView
    private lateinit var selectConditionUsed: SelectableView

    private lateinit var orderViewPlacementData: OrderView
    private lateinit var orderViewItemPrice: OrderView
    private lateinit var orderViewDistance: OrderView
    private lateinit var btnShowItems: View


    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        llHeaderRight = byId(view, R.id.llHeaderRight)
        edtCategory = byId(view, R.id.edtCategory)
        touchCategory = byId(view, R.id.touchCategory)
        edtMaxPrice = byId(view, R.id.edtMaxPrice)
        touchMaxPrice = byId(view, R.id.touchMaxPrice)
        selectDistance1 = byId(view, R.id.selectDistance1)
        selectDistance5 = byId(view, R.id.selectDistance5)
        selectDistance10 = byId(view, R.id.selectDistance10)
        selectDistance100 = byId(view, R.id.selectDistance100)

        selectConditionNew = byId(view, R.id.selectConditionNew)
        selectConditionUsed = byId(view, R.id.selectConditionUsed)

        orderViewDistance = byId(view, R.id.orderViewDistance)
        orderViewPlacementData = byId(view, R.id.orderViewPlacementData)
        orderViewItemPrice = byId(view, R.id.orderViewItemPrice)
        btnShowItems = byId(view, R.id.btnShowItems)


        llHeaderRight.setOnClickListener {
            FiltersHolder.resetFilters()
            setupData()
        }

        orderViewDistance.setOrderType(OrderType.DISTANCE)
        orderViewItemPrice.setOrderType(OrderType.PRICE)
        orderViewPlacementData.setOrderType(OrderType.DATE)


        val function = { _: SelectableView, arg: String, index: Int, isSelected: Boolean ->
            if (isSelected) {
                distanceViews.forEach { if (it.index != index) it.changeSelectionVisual(false) } //Cancel all other selections
                FiltersHolder.getFilterObjectForFilters().maxDistance = arg.toIntOrNull()
            } else {
                FiltersHolder.getFilterObjectForFilters().maxDistance = null
            }
        }
        selectDistance1.selectionCallback = function
        selectDistance5.selectionCallback = function
        selectDistance10.selectionCallback = function
        selectDistance100.selectionCallback = function
        distanceViews.addAll(arrayOf(selectDistance1, selectDistance10, selectDistance5, selectDistance100))


        setupConditionCallback(ConditionType.NEW, selectConditionNew, selectConditionUsed)
        setupConditionCallback(ConditionType.USED, selectConditionUsed, selectConditionNew)

        //TODO refactor this to indexers
        setupOrderCallback(orderViewPlacementData, orderViewItemPrice, orderViewDistance)
        setupOrderCallback(orderViewItemPrice, orderViewPlacementData, orderViewDistance)
        setupOrderCallback(orderViewDistance, orderViewPlacementData, orderViewItemPrice)

        setupData()

        touchCategory.setOnClickListener {
            getSwitcher()?.addTo(SelectCategoryFilterFragment::class.java)
        }

        touchMaxPrice.setOnClickListener {
            openPriceDialog()
        }

        btnShowItems.setOnClickListener {
            val changes = FiltersHolder.getChangesAndDrop()
            if (changes) {
                EventBus.getDefault().post(UpdateFilterEvent(FilterType.MAX_PRICE)) //TODO create general type for update all filters
            }
            activity?.onBackPressed()
        }
    }

    private fun setupOrderCallback(orderView: OrderView, vararg others: OrderView) {
        orderView.onChangeState = { orderType: OrderType, orderDirection: OrderDirection ->
            if (orderDirection == OrderDirection.NONE) {
                FiltersHolder.getFilterObjectForFilters().orderBean = null
            } else {
                FiltersHolder.getFilterObjectForFilters().orderBean = OrderBean(orderType, orderDirection)
                others.forEach { it.setDirectionVisual(OrderDirection.NONE) }
            }
        }
    }

    private fun setupConditionCallback(conditionType: ConditionType, selectableView: SelectableView, anotherView: SelectableView) {
        selectableView.selectionCallback = { _, _, _, isSelected ->
            if (isSelected) {
                anotherView.changeSelectionVisual(false)
                FiltersHolder.getFilterObjectForFilters().conditionType = conditionType
            } else {
                FiltersHolder.getFilterObjectForFilters().conditionType = ConditionType.NONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onPause() {
        EventBus.getDefault().unregister(this)
        super.onPause()
    }

    @Subscribe
    fun onCategoriesEvent(filterEvent: UpdateFilterEvent) {
        if (filterEvent.type == FilterType.CATEGORY) {
            setSelectedCategories()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupData() {
        val filterObjectForFilters = FiltersHolder.getFilterObjectForFilters()
        distanceViews.forEach {
            if (filterObjectForFilters.maxDistance == null) {
                it.changeSelectionVisual(false)
            } else {
                it.changeSelectionVisual(it.selectCallbackParam.toInt() == filterObjectForFilters.maxDistance)
            }
        }

        setFormattedPrice()
        setSelectedCategories()

        when (filterObjectForFilters.conditionType) {
            ConditionType.NEW -> {
                selectConditionUsed.changeSelectionVisual(true)
                selectConditionNew.changeSelectionVisual(false)
            }
            ConditionType.USED -> {
                selectConditionUsed.changeSelectionVisual(false)
                selectConditionNew.changeSelectionVisual(true)
            }
            ConditionType.NONE -> {
                selectConditionUsed.changeSelectionVisual(false)
                selectConditionNew.changeSelectionVisual(false)
            }
        }

        orderViewItemPrice.setDirectionVisual(OrderDirection.NONE)
        orderViewPlacementData.setDirectionVisual(OrderDirection.NONE)
        orderViewDistance.setDirectionVisual(OrderDirection.NONE)


        if (filterObjectForFilters.orderBean != null) {
            when (filterObjectForFilters.orderBean!!.orderType) {
                OrderType.DATE -> {
                    orderViewPlacementData.setDirectionVisual(filterObjectForFilters.orderBean!!.orderDirection)
                }
                OrderType.PRICE -> {
                    orderViewItemPrice.setDirectionVisual(filterObjectForFilters.orderBean!!.orderDirection)
                }
                OrderType.DISTANCE -> {
                    orderViewDistance.setDirectionVisual(filterObjectForFilters.orderBean!!.orderDirection)
                }
            }
        }
    }

    private fun setSelectedCategories() {
        if (FiltersHolder.hasAnyCategoryInFilter()) {
            val categoriesCount = FiltersHolder.getFiltersCount()
            edtCategory.setText(resources.getQuantityString(R.plurals.categories_count, categoriesCount, categoriesCount))
        } else {
            edtCategory.setText(R.string.all_categories)
        }
    }

    override fun getLayoutResId() = R.layout.fragment_filters

    override fun getHeaderText() = R.string.header_filters

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun isFooterShow() = false

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        FiltersHolder.cancelChanges()
        return super.onBackPressed()
    }

    private fun <T : View> byId(view: View, @IdRes id: Int): T {
        return view.findViewById(id)
    }

    private fun openPriceDialog() {
        val materialDialog = MaterialDialog.Builder(activity!!)
                .customView(R.layout.fragment_content_select_price, false)
                .build()
        val edtPrice: MaterialEditText = materialDialog.findViewById(R.id.edtPrice) as MaterialEditText

        materialDialog.findViewById(R.id.btnDialogCancel).setOnClickListener {
            materialDialog.dismiss()
        }

        materialDialog.findViewById(R.id.btnDialogDone).setOnClickListener {
            materialDialog.dismiss()
            val value = edtPrice.text.toString().toDoubleOrNull()
            FiltersHolder.getFilterObjectForFilters().maxPrice = value
            setFormattedPrice()
        }
        val decimalFormat = DecimalFormat()
        decimalFormat.maximumFractionDigits = 4
        var format = ""
        val maxPrice = FiltersHolder.getFilterObjectForFilters().maxPrice
        if (maxPrice != null && maxPrice > 0) {
            format = decimalFormat.format(maxPrice)
        }
        format = java.lang.String(format).replaceAll("\\s+", "").replace(",", ".")
        edtPrice.setText(format)
        edtPrice.hint = getString(R.string.hint_price_in_ubc)
        edtPrice.filters += MaxValueInputFilter()
        materialDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        materialDialog.show()
    }


    private fun setFormattedPrice() {
        val maxPrice = FiltersHolder.getFilterObjectForFilters().maxPrice
        if (maxPrice == null) {
            edtMaxPrice.setText(getString(R.string.ubc_postfix))
        } else {
            edtMaxPrice.setText(getString(R.string.balance_placeholder, maxPrice.moneyFormat()))
        }
    }
}