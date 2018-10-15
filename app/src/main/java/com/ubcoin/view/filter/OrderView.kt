package com.ubcoin.view.filter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.model.ui.order.OrderDirection
import com.ubcoin.model.ui.order.OrderType
import com.ubcoin.utils.invisible
import com.ubcoin.utils.visible

/**
 * Created by Yuriy Aizenberg
 */
class OrderView : ConstraintLayout {

    private lateinit var txtFilter: TextView
    private lateinit var imgFilter: ImageView
    private var currentOrderDirection: OrderDirection = OrderDirection.NONE
    private var currentOrderType: OrderType? = null

    private var ascDrawable : Drawable ?= null
    private var descDrawable: Drawable?= null
    private var activeTextColor: Int ?= null
    private var inactiveTextColor: Int ?= null

    var onChangeState: ((orderType: OrderType, orderDirection: OrderDirection) -> Unit)? = null

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    fun setOrderType(orderType: OrderType) {
        this.currentOrderType = orderType
    }

    private fun init(attrs: AttributeSet?) {
        inflate(context, R.layout.view_order, this)
        imgFilter = findViewById(R.id.imgFilter)
        txtFilter = findViewById(R.id.txtFilter)

        setOnClickListener {
            if (currentOrderType != null && onChangeState != null) {
                changeDirection()
                onChangeState!!.invoke(currentOrderType!!, currentOrderDirection)
            }
        }
        if (attrs != null) {
            val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.OrderView, 0, 0)

            ascDrawable = attributes.getDrawable(R.styleable.OrderView_ascIcon)
            descDrawable = attributes.getDrawable(R.styleable.OrderView_descIcon)

            activeTextColor = attributes.getColor(R.styleable.OrderView_selectedColor, Color.BLACK)
            inactiveTextColor = attributes.getColor(R.styleable.OrderView_unselectedColor, Color.BLACK)

            val text = attributes.getString(R.styleable.OrderView_orderText)
            txtFilter.text = text

            currentOrderDirection = OrderDirection.values()[attributes.getInteger(R.styleable.OrderView_orderDirection, 0)]

            attributes.recycle()
            setDirectionInternal(OrderDirection.NONE, false)
        }
    }

    fun setDirectionVisual(orderDirection: OrderDirection) {
        setDirectionInternal(orderDirection, false)
    }

    private fun invokeListener() {
        onChangeState!!.invoke(currentOrderType!!, currentOrderDirection)
    }

    private fun changeDirection() {
        val newDirection = when(currentOrderDirection) {
            OrderDirection.NONE -> OrderDirection.ASC
            OrderDirection.ASC -> OrderDirection.DESC
            else -> OrderDirection.NONE
        }
        setDirectionInternal(newDirection, true)
    }

    private fun setDirectionInternal(orderDirection: OrderDirection, invokeListener: Boolean) {
        this.currentOrderDirection = orderDirection
        when (orderDirection) {
            OrderDirection.NONE -> {
                imgFilter.invisible()
                txtFilter.setTextColor(inactiveTextColor!!)
            }
            OrderDirection.ASC, OrderDirection.DESC  -> {
                imgFilter.visible()
                imgFilter.setImageDrawable(if (orderDirection == OrderDirection.ASC) ascDrawable else descDrawable)
                txtFilter.setTextColor(activeTextColor!!)
            }
        }
        if (invokeListener) invokeListener()
    }

}