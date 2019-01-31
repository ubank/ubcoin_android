package com.ubcoin.view.deal_description

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.ubcoin.R
import kotlinx.android.synthetic.main.view_delivery_type.view.*

class DeliveryTypeView : LinearLayout {

    enum class DeliveryType{
        Delivery,
        Meeting
    }

    var type = DeliveryType.Delivery
    var onTypeChanged: OnTypeChanged? = null

    fun setOnTypeChangedListener(listener: OnTypeChanged){
        onTypeChanged = listener
    }

    fun getDeliveryType() : DeliveryType {
        return type
    }

    private lateinit var clDelivery: ConstraintLayout
    private lateinit var clMeeting: ConstraintLayout
    private lateinit var rlDelivery: RelativeLayout
    private lateinit var rlMeeting: RelativeLayout
    private lateinit var viDelivery: ImageView
    private lateinit var ivMeeting: ImageView

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
        inflate(context, R.layout.view_delivery_type, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        clDelivery = findViewById<ConstraintLayout>(R.id.clDelivery)
        clMeeting = findViewById<ConstraintLayout>(R.id.clMeeting)
        rlDelivery = findViewById<RelativeLayout>(R.id.rlDelivery)
        rlMeeting = findViewById<RelativeLayout>(R.id.rlMeeting)
        viDelivery = findViewById<ImageView>(R.id.ivDelivery)
        ivMeeting = findViewById<ImageView>(R.id.ivMeeting)

        initType()

        clDelivery.setOnClickListener{
            if(type == DeliveryType.Delivery)
                return@setOnClickListener

            type = DeliveryType.Delivery
            initType()
            notifyTypeChanged()
        }

        clMeeting.setOnClickListener{
            if(type == DeliveryType.Meeting)
                return@setOnClickListener

            type = DeliveryType.Meeting
            initType()
            notifyTypeChanged()
        }
    }

    fun initType(){
        if(type == DeliveryType.Meeting)
        {
            clDelivery.background = context.resources.getDrawable(R.drawable.gray_border)
            clMeeting.background = context.resources.getDrawable(R.drawable.green_border)

            rlDelivery.background = context.resources.getDrawable(R.drawable.gray_circle)
            rlMeeting.background = context.resources.getDrawable(R.drawable.green_circle)
            ivDelivery.visibility = View.GONE
            ivMeeting.visibility = View.VISIBLE
        }
        else
        {
            clDelivery.background = context.resources.getDrawable(R.drawable.green_border)
            clMeeting.background = context.resources.getDrawable(R.drawable.gray_border)
            rlDelivery.background = context.resources.getDrawable(R.drawable.green_circle)
            rlMeeting.background = context.resources.getDrawable(R.drawable.gray_circle)
            ivDelivery.visibility = View.VISIBLE
            ivMeeting.visibility = View.GONE
        }
    }

    fun notifyTypeChanged(){
        if(onTypeChanged != null)
            onTypeChanged!!.onChanged(type)
    }

    interface OnTypeChanged
    {
        fun onChanged(type: DeliveryType)
    }
}