package com.ubcoin.fragment.market

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.MarketItem
import com.ubcoin.view.deal_description.*

class DealPurchaseFragment : BaseFragment() {
    private lateinit var marketItem: MarketItem

    override fun getLayoutResId() = R.layout.fragment_deal_purchase
    override fun getHeaderIcon() = R.drawable.ic_back
    override fun getHeaderText() = R.string.text_purchase

    private lateinit var itemDescription: ItemDescriptionView
    private lateinit var progressDescription: ProgressDescriptionView
    private lateinit var sellerLocation: SellerLocationView
    private lateinit var deliveryType: DeliveryTypeView
    private lateinit var userProfile: UserProfileView
    private lateinit var btnChat: Button
    private lateinit var progressView: ProgressView
    private lateinit var btnReport: Button
    private lateinit var btnCancelDeal: Button
    private lateinit var purchaseMain: PurchaseMainView
    private lateinit var llDigitalPurchaseDescription: LinearLayout

    companion object {
        fun getBundle(marketItem: MarketItem): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(MarketItem::class.java.simpleName, marketItem)
            return bundle
        }
    }

    override fun isFooterShow() = false

    override fun onIconClick() {
        super.onIconClick()
        activity!!.onBackPressed()
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        marketItem = arguments?.getSerializable(MarketItem::class.java.simpleName) as MarketItem

        itemDescription = view.findViewById(R.id.itemDescription)
        progressDescription = view.findViewById(R.id.progressDescription)
        sellerLocation = view.findViewById(R.id.sellerLocation)
        purchaseMain = view.findViewById(R.id.purchaseMain)
        deliveryType = view.findViewById(R.id.deliveryType)
        userProfile = view.findViewById(R.id.userProfile)
        btnChat = view.findViewById(R.id.btnChat)
        progressView = view.findViewById(R.id.progressView)
        btnReport = view.findViewById(R.id.btnReport)
        btnCancelDeal = view.findViewById(R.id.btnCancelDeal)
        llDigitalPurchaseDescription = view.findViewById(R.id.llDigitalPurchaseDescription)

        initView()
    }

    fun initView(){
        enableItemDescription()

        if(!marketItem.category!!.id.equals("dc602e1f-80d2-af0d-9588-de6f1956f4ef")) {
            enableSellerLocation()
            enableDeliveryType()
            enablePurchaseMain()
            purchaseMain.IsAddressInputEnabled(true)
        }
        else{
            enableUserProfile()
            enableChatButton()
            enablePurchaseMain()
            llDigitalPurchaseDescription.visibility = View.VISIBLE
        }
    }

    fun enableItemDescription(){
        itemDescription.marketItem = marketItem
        itemDescription.visibility = View.VISIBLE
    }

    fun enableSellerLocation(){
        sellerLocation.location = marketItem.location
        sellerLocation.visibility = View.VISIBLE
    }

    fun enableUserProfile(){
        userProfile.user = marketItem.user
        userProfile.visibility = View.VISIBLE
    }

    fun enableChatButton(){
        btnChat.setOnClickListener{}
        btnChat.visibility = View.VISIBLE
    }

    fun enablePurchaseMain(){
        purchaseMain.marketItem = marketItem
        purchaseMain.visibility = View.VISIBLE
    }

    fun enableDeliveryType(){
        deliveryType.setOnTypeChangedListener(object : DeliveryTypeView.OnTypeChanged{
            override fun onChanged(type: DeliveryTypeView.DeliveryType) {
                if(type.equals(DeliveryTypeView.DeliveryType.Delivery))
                    purchaseMain.IsAddressInputEnabled(true)
                else
                    purchaseMain.IsAddressInputEnabled(false)
            }
        })
        deliveryType.visibility = View.VISIBLE
    }
}