package com.ubcoin.view.filter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.ubcoin.R

/**
 * Created by Yuriy Aizenberg
 */
class SelectableView : ConstraintLayout {

    private var itemSelected: Boolean = false
    var index: Int = -1
    private lateinit var txtSelection: TextView
    private lateinit var llSelection: View

    private var selectedDrawable: Drawable? = null
    private var unselectedDrawable: Drawable? = null

    private var selectedColor = Color.BLACK
    private var unselectedColor = Color.BLACK

    lateinit var selectCallbackParam: String

    var selectionCallback: ((selectableView: SelectableView, arg: String, index: Int, isSelected: Boolean) -> Unit)? = null


    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        inflate(context, R.layout.view_selectable_item, this)
        txtSelection = findViewById(R.id.txtSelection)
        llSelection = findViewById(R.id.llUnderlineContainer)

        if (attrs != null) {
            val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SelectableView, 0, 0)
            val text = attributes.getString(R.styleable.SelectableView_selectableText)
            txtSelection.text = text

            selectedColor = attributes.getColor(R.styleable.SelectableView_selectableActiveColor, selectedColor)
            unselectedColor = attributes.getColor(R.styleable.SelectableView_selectableInactiveColor, unselectedColor)

            selectedDrawable = attributes.getDrawable(R.styleable.SelectableView_selectableActiveUnderline)
            unselectedDrawable = attributes.getDrawable(R.styleable.SelectableView_selectableInactiveUnderline)
            index = attributes.getInt(R.styleable.SelectableView_selectableIndex, index)
            selectCallbackParam = attributes.getString(R.styleable.SelectableView_selectableCallbackParam)!!

            setOnClickListener {
                changeSelection(!itemSelected, true)
            }
            changeSelectionVisual(false)
        }

    }


    fun changeSelectionVisual(isSelected: Boolean) {
        changeSelection(isSelected, false)
    }

    private fun changeSelection(isSelected: Boolean, invokeListener: Boolean) {
        this.itemSelected = isSelected
        if (!isSelected) {
            txtSelection.setTextColor(unselectedColor)
            llSelection.background = unselectedDrawable
        } else {
            txtSelection.setTextColor(selectedColor)
            llSelection.background = selectedDrawable
        }
        if (invokeListener) {
            selectionCallback?.invoke(this, selectCallbackParam, index, isSelected)
        }
    }


}