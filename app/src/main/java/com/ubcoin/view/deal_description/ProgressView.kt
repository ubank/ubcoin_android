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
import com.ubcoin.model.response.StatusDescription

class ProgressView : LinearLayout {
    private var progressList: List<StatusDescription> = ArrayList()
    private var selected = -1
    private lateinit var progressAdapter: ProgressAdapter
    constructor(context: Context?) : super(context) {
        init(null)
    }

    public fun setProgressList(progressList: List<StatusDescription>){
        this.progressList  = progressList
        initView()
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

        progressAdapter = ProgressAdapter(context!!)

        var manager = LinearLayoutManager(context)
        rvProgress.layoutManager = manager
        rvProgress.adapter = progressAdapter


    }

    fun initView(){
        progressAdapter.clear()
        progressAdapter.addData(progressList, selected)
    }
}