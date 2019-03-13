package com.ubcoin.fragment.market

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.Currency
import com.ubcoin.model.ItemPurchaseDto
import com.ubcoin.network.DataProvider
import com.ubcoin.view.deal_description.*
import io.reactivex.functions.Consumer
import android.content.Intent
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.ubcoin.TheApplication
import com.ubcoin.activity.MainActivity
import com.ubcoin.fragment.messages.ChatFragment
import com.ubcoin.fragment.profile.SellerProfileFragment
import com.ubcoin.model.Purchase
import com.ubcoin.model.event.MarketItemBoughtEvent
import com.ubcoin.model.response.*
import com.ubcoin.utils.ProfileHolder
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus


class DealPurchaseFragment : BaseFragment() {
    private var marketItem: MarketItem? = null
    private var user: User? = null
    private var purchaseId: String? = null
    private var purchaseStatus: PurchaseItemStatus? = null
    private var category: String = ""
    private var statusDescriptions: List<StatusDescription> = ArrayList()
    private var isDelivery: Boolean = false
    private var purchase: Purchase? = null

    override fun getLayoutResId() = R.layout.fragment_deal_purchase
    override fun getHeaderIcon() = R.drawable.ic_back
    override fun getHeaderText() = R.string.text_purchase

    private lateinit var itemDescription: ItemDescriptionView
    private lateinit var progressDescription: ProgressDescriptionView
    private lateinit var confirmDeliveryPrice: ConfirmDeliveryPriceView
    private lateinit var sellerLocation: SellerLocationView
    private lateinit var deliveryType: DeliveryTypeView
    private lateinit var userProfile: UserProfileView
    private lateinit var btnChat: Button
    private lateinit var progressView: ProgressView
    private lateinit var btnReport: Button
    private lateinit var btnCancelDeal: Button
    private lateinit var purchaseMain: PurchaseMainView
    private lateinit var llDigitalPurchaseDescription: LinearLayout
    private lateinit var progressCenter: GoogleProgressBar
    private lateinit var needDelivery: NeedDeliveryView

    companion object {
        fun getBundle(marketItem: MarketItem): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(MarketItem::class.java.simpleName, marketItem)
            return bundle
        }

        fun getBundle(purchaseId: String): Bundle {
            val bundle = Bundle()
            bundle.putString("purchaseId", purchaseId)
            return bundle
        }
    }

    fun isDigital(): Boolean{
        return category.equals("dc602e1f-80d2-af0d-9588-de6f1956f4ef")
    }

    override fun isFooterShow() = false

    override fun onIconClick() {
        super.onIconClick()
        activity!!.onBackPressed()
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        marketItem = arguments?.getSerializable(MarketItem::class.java.simpleName) as MarketItem?
        purchaseId = arguments?.getString("purchaseId")

        needDelivery = view.findViewById(R.id.needDelivery)
        needDelivery.buttonClickListener = object: NeedDeliveryView.OnButtonClickListener{
            override fun onNeedDelivery() {
                progressCenter.visibility = View.VISIBLE
                DataProvider.withDelivery(purchaseId!!, Consumer {
                    activity?.onBackPressed()
                }, Consumer {
                    handleException(it)
                    progressCenter.visibility = View.GONE
                })
            }
        }
        confirmDeliveryPrice = view.findViewById(R.id.confirmDeliveryPrice)
        confirmDeliveryPrice.buttonClickListener = object: ConfirmDeliveryPriceView.OnButtonClickListener{
            override fun onConfirm(price: Double) {
                progressCenter.visibility = View.VISIBLE

                var map = HashMap<String,Double>()
                map.put("amount", price)

                DataProvider.confirmDeliveryPrice(purchaseId!!,map,  Consumer {
                    activity?.onBackPressed()
                }, Consumer {
                    handleException(it)
                    progressCenter.visibility = View.GONE
                })
            }
        }
        itemDescription = view.findViewById(R.id.itemDescription)
        itemDescription.setOnClickListener {
            getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(marketItem!!), true)
        }
        progressDescription = view.findViewById(R.id.progressDescription)
        progressDescription.setClickListener(object: ProgressDescriptionView.OnButtonClickListener{
            override fun onConfirmDeliveryStart() {
                //not implemented here
            }

            override fun onConfirmFileClicked() {
                progressCenter.visibility = View.VISIBLE
                DataProvider.confirmPurchase(purchaseId!!, Consumer {
                    activity?.onBackPressed()
                }, Consumer {
                    handleException(it)
                    progressCenter.visibility = View.GONE
                })
            }

            override fun onConfirmNewDeliveryPrice(price: Double) {
                //not implemented here
            }

            override fun onReceivedItemClicked() {
                progressCenter.visibility = View.VISIBLE
                DataProvider.confirmPurchase(purchaseId!!, Consumer {
                    activity?.onBackPressed()
                }, Consumer {
                    handleException(it)
                    progressCenter.visibility = View.GONE
                })
            }
        })
        sellerLocation = view.findViewById(R.id.sellerLocation)
        sellerLocation.setOnClickListener {
            val location = marketItem!!.location
            if (location != null) {
                val latLng = LatLng(
                        location.latPoint?.toDouble() ?: .0,
                        location.longPoint?.toDouble() ?: .0)
                TheApplication.instance.openGeoMap(latLng.latitude, latLng.longitude, location.text)
            }
        }
        purchaseMain = view.findViewById(R.id.purchaseMain)
        purchaseMain.activity = activity
        deliveryType = view.findViewById(R.id.deliveryType)
        userProfile = view.findViewById(R.id.userProfile)
        userProfile.setOnClickListener {
            getSwitcher()?.addTo(SellerProfileFragment::class.java, SellerProfileFragment.getBundle(user!!), false)
        }
        btnChat = view.findViewById(R.id.btnChat)
        btnChat.setOnClickListener {
            if(purchase != null) {
                getSwitcher()?.addTo(ChatFragment::class.java, ChatFragment.getBundle(marketItem!!.id, purchase!!.seller), true)
            }
        }
        progressView = view.findViewById(R.id.progressView)
        btnReport = view.findViewById(R.id.btnReport)
        btnReport.setOnClickListener{
            val testIntent = Intent(Intent.ACTION_VIEW)
            val data = Uri.parse("mailto:?subject=" + "" + "&body=" + "" + "&to=" + "support@ubcoin.io")
            testIntent.data = data
            startActivity(testIntent)
        }
        btnCancelDeal = view.findViewById(R.id.btnCancelDeal)
        btnCancelDeal.setOnClickListener {
            progressCenter.visibility = View.VISIBLE
            DataProvider.cancelPurchase(purchaseId!!, Consumer {
                activity?.onBackPressed()
            }, Consumer {
                handleException(it)
                progressCenter.visibility = View.GONE
            })
        }
        llDigitalPurchaseDescription = view.findViewById(R.id.llDigitalPurchaseDescription)
        progressCenter = view.findViewById(R.id.progressCenter)

        if(marketItem != null) {
            user = marketItem!!.user
            category = marketItem?.category!!.id
            initView()
        }
        else
            loadPurchase()
    }

    fun initView(){
        (activity as MainActivity).menuBottomView.setNeedsUpdate()
        if(marketItem == null)
            return
        enableItemDescription()

        when(purchaseStatus) {
            null -> {
                if (!isDigital()) {
                    enableSellerLocation()
                    enableDeliveryType()
                    enablePurchaseMain()
                    purchaseMain.IsAddressInputEnabled(true)
                } else {
                    enableUserProfile()
                    enableChatButton()
                    enablePurchaseMain()
                    llDigitalPurchaseDescription.visibility = View.VISIBLE
                }
            }

            PurchaseItemStatus.ACTIVE -> {
                if(isDigital())
                    btnReport.visibility = View.VISIBLE
                else {
                    btnCancelDeal.visibility = View.VISIBLE
                    if(!isDelivery)
                    {enableNeedDelivery()}
                }
                enableUserProfile()
                enableChatButton()
            }

            PurchaseItemStatus.DELIVERY_PRICE_DEFINED -> {
                btnCancelDeal.visibility = View.VISIBLE
                enableConfirmDeliveryPrice()
                enableUserProfile()
                enableChatButton()
            }

            PurchaseItemStatus.DELIVERY_PRICE_CONFIRMED -> {
                btnCancelDeal.visibility = View.VISIBLE
                enableUserProfile()
                enableChatButton()
            }

            PurchaseItemStatus.DELIVERY -> {
                btnReport.visibility = View.VISIBLE
                enableUserProfile()
                enableChatButton()
            }

            PurchaseItemStatus.CONFIRMED -> {
                btnReport.visibility = View.VISIBLE
                enableUserProfile()
                enableChatButton()
            }
        }

        enableProgressDescription()
        enableProgressView()
    }

    fun enableNeedDelivery(){
        needDelivery.visibility = View.VISIBLE
    }

    fun enableConfirmDeliveryPrice(){
        confirmDeliveryPrice.item = purchase
        confirmDeliveryPrice.visibility = View.VISIBLE
    }

    fun enableItemDescription(){
        itemDescription.marketItem = marketItem
        itemDescription.currency = purchase?.currencyType
        itemDescription.visibility = View.VISIBLE
    }

    fun enableSellerLocation(){
        sellerLocation.location = marketItem!!.location
        sellerLocation.visibility = View.VISIBLE
    }

    fun enableUserProfile(){
        userProfile.user = user
        userProfile.visibility = View.VISIBLE
    }

    fun enableChatButton(){
        btnChat.visibility = View.VISIBLE
    }

    fun enablePurchaseMain(){
        purchaseMain.marketItem = marketItem
        purchaseMain.setCreatePurchaseListener(object: PurchaseMainView.OnCreatePurchase{
            override fun purchase(currency: Currency, comment: String) {
                var withDelivery = false
                if(!isDigital())
                    withDelivery = deliveryType.type.equals(DeliveryTypeView.DeliveryType.Delivery)
                var purchase = ItemPurchaseDto(comment, currency.toString(), marketItem!!.id, "", withDelivery)
                DataProvider.buyItem(purchase, Consumer {
                    if(marketItem != null)
                        EventBus.getDefault().post(MarketItemBoughtEvent(marketItem!!.id))
                    activity?.onBackPressed()
                    activity?.onBackPressed()
                    var purchaseId = it.id
                    getSwitcher()?.addTo(DealPurchaseFragment::class.java, DealPurchaseFragment.getBundle(purchaseId), false)
                },
                Consumer {
                    handleException(it)
                })
            }
        })
        purchaseMain.visibility = View.VISIBLE
    }

    fun enableProgressDescription(){
        if(purchaseStatus != null) {
            progressDescription.item = purchase
            progressDescription.status = purchaseStatus
            progressDescription.isDigital = isDigital()
            progressDescription.isSeller = false
            progressDescription.isDelivery = isDelivery
            progressDescription.visibility = View.VISIBLE
        }
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

    fun enableProgressView(){
        if(statusDescriptions.size > 0) {
            progressView.setProgressList(statusDescriptions)
            progressView.visibility = View.VISIBLE
        }
    }

    fun loadPurchase(){
        progressCenter.visibility = View.VISIBLE
        DataProvider.getPurchaseStatus(purchaseId!!, Consumer {
            purchase = it
            marketItem = it.item
            user = it.seller
            statusDescriptions = it.statusDescriptions
            category = marketItem?.category?.id!!
            purchaseStatus = it.status
            if(purchaseStatus == PurchaseItemStatus.CANCELLED)
                activity?.onBackPressed()
            else {
                isDelivery = it.withDelivery
                initView()
            }
            progressCenter.visibility = View.GONE
        }, Consumer {
            handleException(it)
            progressCenter.visibility = View.GONE
        })
    }


    fun hideViews(){
        itemDescription.visibility = View.GONE
        progressDescription.visibility = View.GONE
        userProfile.visibility = View.GONE
        btnChat.visibility = View.GONE
        progressView.visibility = View.GONE
        btnReport.visibility = View.GONE
        btnCancelDeal.visibility = View.GONE
        progressCenter.visibility = View.GONE

        confirmDeliveryPrice.visibility = View.GONE
        sellerLocation.visibility = View.GONE
        deliveryType.visibility = View.GONE
        llDigitalPurchaseDescription.visibility = View.GONE
        needDelivery.visibility = View.GONE
    }

    override fun subscribeOnDealUpdate(id: String) {
        super.subscribeOnDealUpdate(id)
        activity?.runOnUiThread {
            if (id.equals(purchaseId)) {
                hideViews()
                loadPurchase()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).menuBottomView.setNeedsUpdate()
    }
}