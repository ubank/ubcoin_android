package com.ubcoin.view

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R

/**
 * Created by Yuriy Aizenberg
 */
class RefreshableEditText : ConstraintLayout {


    private lateinit var materialEditText: MaterialEditText
    private lateinit var imgRefresh: View
    private var inputHint: String? = ""

    var refreshListener: IRefreshListener? = null

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(attrs: AttributeSet?) {
        inflate(context, R.layout.view_refreshable_edittext, this)
        if (attrs == null) return

        materialEditText = findViewById(R.id.edtSellPrice)
        findViewById<View>(R.id.touchContainer).setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                refreshListener?.onViewClick()
            }
            true
        }
        imgRefresh = findViewById(R.id.imgRefresh)

        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.RefreshableEditText, 0, 0)

        inputHint = attributes.getString(R.styleable.RefreshableEditText_RefreshViewHint)
        materialEditText.hint = inputHint ?: context.resources.getString(R.string.empty)
        setRefreshState(RefreshState.IDLE)
        attributes.recycle()
    }

    fun setRefreshState(refreshState: RefreshState) {
        when (refreshState) {
            RefreshableEditText.RefreshState.IDLE -> {
                restoreColor()
                hideRefreshButton()
                materialEditText.text = null
                materialEditText.hint = inputHint
            }
            RefreshableEditText.RefreshState.REFRESH_IN_PROGRESS -> {
                setRefreshColor()
                hideRefreshButton()
                materialEditText.setText(R.string.hint_convertation_in_progress)
            }
            RefreshableEditText.RefreshState.REFRESH_FAILURE -> {
                setRefreshColor()
                showRefreshButton()
                materialEditText.text = null
            }
        }
    }

    fun stopRefreshAndSetValue(value: String) {
        setRefreshState(RefreshState.IDLE)
        materialEditText.setText(value)
    }

    private fun restoreColor() {
        materialEditText.setMetTextColor(ContextCompat.getColor(context, R.color.inputTextColor))
    }

    private fun setRefreshColor() {
        materialEditText.setMetTextColor(ContextCompat.getColor(context, R.color.haveAccountColor))
    }

    private fun hideRefreshButton() {
        imgRefresh.visibility = View.GONE
        imgRefresh.setOnClickListener { }
    }

    private fun showRefreshButton() {
        imgRefresh.visibility = View.VISIBLE
        imgRefresh.setOnClickListener {
            refreshListener?.onRefreshClick()
        }
    }

    enum class RefreshState {
        IDLE,
        REFRESH_IN_PROGRESS,
        REFRESH_FAILURE,
    }

    interface IRefreshListener {
        fun onRefreshClick()

        fun onViewClick()

    }

}