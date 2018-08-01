package com.ubcoin.view.menu

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.ubcoin.R

@Suppress("MemberVisibilityCanBePrivate", "unused")
/**
 * Created by Yuriy Aizenberg
 */

class MenuSingleView : RelativeLayout {

    private val index = Indexer.getIndex()

    var txtMenuItem: TextView? = null
    var imgMenuItem: ImageView? = null
    var isActive: Boolean = false

    var activeDrawable: Drawable? = null
    var inactiveDrawable: Drawable? = null

    var activeTextColor: Int? = null
    var inactiveTextColor: Int? = null

    var singleMenuCallback: ISingleMenuViewCallback? = null

    constructor(context: Context?) : super(context) {
        initialize(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        inflate(context, R.layout.view_menu_single_view, this)

        if (attrs == null) return
        imgMenuItem = findViewById(R.id.imgMenuItem)
        txtMenuItem = findViewById(R.id.txtMenuItem)
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.MenuItemStyle, 0, 0)

        val text = attributes.getText(R.styleable.MenuItemStyle_MenuItemText)

        activeDrawable = attributes.getDrawable(R.styleable.MenuItemStyle_MenuItemActiveIcon)
        inactiveDrawable = attributes.getDrawable(R.styleable.MenuItemStyle_MenuItemInactiveIcon)

        activeTextColor = attributes.getColor(R.styleable.MenuItemStyle_MenuItemActiveTextColor, Color.BLACK)
        inactiveTextColor = attributes.getColor(R.styleable.MenuItemStyle_MenuItemInactiveTextColor, Color.BLACK)

        txtMenuItem?.run {
            this.text = text
            setTextColor(inactiveTextColor!!)
        }

        imgMenuItem?.setImageDrawable(inactiveDrawable)

        setOnClickListener {
            fireCallback()
        }

        attributes.recycle()
    }

    private fun fireCallback() {
        singleMenuCallback?.onSingleItemClickCallback(this, isActive)
    }

    fun activate() {
        activate(false)
    }

    fun deactivate() {
        deactivate(false)
    }


    fun activate(fireCallback: Boolean) {
        isActive = true
        txtMenuItem?.setTextColor(activeTextColor!!)
        imgMenuItem?.setImageDrawable(activeDrawable)
        if (fireCallback) fireCallback()
    }


    fun deactivate(fireCallback: Boolean) {
        isActive = false
        txtMenuItem?.setTextColor(inactiveTextColor!!)
        imgMenuItem?.setImageDrawable(inactiveDrawable)
        if (fireCallback) fireCallback()
    }



    fun toggle() {
        if (isActive) activate() else deactivate()
    }

    fun changeLabel(@StringRes str: Int) {
        txtMenuItem?.setText(str)
    }

    fun changeIcon(@DrawableRes icon: Int) {
        imgMenuItem?.setImageResource(icon)
    }

    fun show() {
        toggleVisibility(true)
    }

    fun hide() {
        toggleVisibility(false)
    }

    private fun toggleVisibility(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MenuSingleView

        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        return 31 * index
    }


}