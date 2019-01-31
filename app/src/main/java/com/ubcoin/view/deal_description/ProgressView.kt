package com.ubcoin.view.deal_description

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.LinearLayout
import com.ubcoin.R
import com.ubcoin.adapter.ProgressAdapter
import com.ubcoin.model.Progress

class ProgressView : LinearLayout {
    private var progressList: List<Progress> = ArrayList()
    private var selected = -1
    constructor(context: Context?) : super(context) {
        init(null)
    }

    public fun setSelectedProgress(selected: Int){
        this.selected = selected
    }

    public fun setProgressList(progressList: List<Progress>){
        this.progressList  = progressList
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(attrs: AttributeSet?) {
        inflate(context, R.layout.view_progress, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        var rvProgress = findViewById<RecyclerView>(R.id.rvProgress)

        var progressAdapter = ProgressAdapter(0, context!!)

        var manager = LinearLayoutManager(context)
        rvProgress.layoutManager = manager
        rvProgress.adapter = progressAdapter

        progressAdapter.addData(progressList, selected)
    }
}