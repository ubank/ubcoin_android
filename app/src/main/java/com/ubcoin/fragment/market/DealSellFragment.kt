package com.ubcoin.fragment.market

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar
import com.ubcoin.R
import com.ubcoin.adapter.ProgressAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.messages.ChatFragment
import com.ubcoin.fragment.profile.SellerProfileFragment
import com.ubcoin.model.Progress
import com.ubcoin.model.Purchase
import com.ubcoin.model.response.*
import com.ubcoin.network.DataProvider
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.view.deal_description.*
import io.reactivex.functions.Consumer

class DealSellFragment : BaseFragment() {
    private var marketItem: MarketItem? = null
    private var user: User? = null
    private var purchaseId: String? = null
    private var purchaseStatus: PurchaseItemStatus? = null
    private var category: String = ""
    private var statusDescriptions: List<StatusDescription> = ArrayList()
    private var isDelivery: Boolean = false
    private var purchase: Purchase? = null

    override fun getLayoutResId() = R.layout.fragment_deal_sell
    override fun getHeaderIcon() = R.drawable.ic_back
    override fun getHeaderText() = R.string.menu_label_sell

    private lateinit var itemDescription: ItemDescriptionView
    private lateinit var progressDescription: ProgressDescriptionView
    private lateinit var userProfile: UserProfileView
    private lateinit var btnChat: Button
    private lateinit var progressView: ProgressView
    private lateinit var btnReport: Button
    private lateinit var btnCancelDeal: Button
    private lateinit var progressCenter: GoogleProgressBar
    private lateinit var organizeDelivery: OrganizeDeliveryView
    private lateinit var sellerSetPrice: SellerSetPriceView

    companion object {
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

        purchaseId = arguments?.getString("purchaseId")

        organizeDelivery = view.findViewById(R.id.organizeDelivery)
        organizeDelivery.buttonClickListener = object: OrganizeDeliveryView.OnButtonClickListener{
            override fun onOrganizeDelivery() {
                progressCenter.visibility = View.VISIBLE
                DataProvider.withDelivery(purchaseId!!, Consumer {
                    activity?.onBackPressed()
                }, Consumer {
                    handleException(it)
                    progressCenter.visibility = View.GONE
                })
            }
        }
        sellerSetPrice = view.findViewById(R.id.sellerSetPrice)
        sellerSetPrice.activity = activity
        sellerSetPrice.setClickListener(object: SellerSetPriceView.OnButtonClickListener{
            override fun onConfirmDeliveryPrice(price: Double) {
                progressCenter.visibility = View.VISIBLE

                var map = HashMap<String,Double>()
                map.put("amount", price)

                DataProvider.setDeliveryPrice(purchaseId!!, map, Consumer {
                    activity?.onBackPressed()
                }, Consumer {
                    handleException(it)
                    progressCenter.visibility = View.GONE
                })
            }
        })
        itemDescription = view.findViewById(R.id.itemDescription)
        itemDescription.setOnClickListener {
            getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(marketItem!!), true)
        }
        progressDescription = view.findViewById(R.id.progressDescription)
        progressDescription.activity = activity
        progressDescription.setClickListener(object: ProgressDescriptionView.OnButtonClickListener{
            override fun onConfirmDeliveryStart() {
                progressCenter.visibility = View.VISIBLE

                DataProvider.startDelivery(purchaseId!!, Consumer {
                    activity?.onBackPressed()
                }, Consumer {
                    handleException(it)
                    progressCenter.visibility = View.GONE
                })
            }

            override fun onConfirmFileClicked() {
            }

            override fun onConfirmNewDeliveryPrice(price: Double) {
                progressCenter.visibility = View.VISIBLE

                var map = HashMap<String,Double>()
                map.put("amount", price)

                DataProvider.setDeliveryPrice(purchaseId!!, map, Consumer {
                    activity?.onBackPressed()
                }, Consumer {
                    handleException(it)
                    progressCenter.visibility = View.GONE
                })
            }

            override fun onReceivedItemClicked() {
            }
        })
        userProfile = view.findViewById(R.id.userProfile)
        userProfile.setOnClickListener {
            getSwitcher()?.addTo(SellerProfileFragment::class.java, SellerProfileFragment.getBundle(user!!), false)
        }
        btnChat = view.findViewById(R.id.btnChat)
        btnChat.setOnClickListener {
            if(purchase != null) {
                getSwitcher()?.addTo(ChatFragment::class.java, ChatFragment.getBundle(marketItem!!.id, purchase!!.buyer), true)
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
        progressCenter = view.findViewById(R.id.progressCenter)

        loadPurchase()
    }

    fun initView(){
        if(marketItem == null)
            return
        enableItemDescription()

        when(purchaseStatus) {
            null -> {
                if (!isDigital()) {
                } else {
                    enableUserProfile()
                    enableChatButton()
                }
            }

            PurchaseItemStatus.ACTIVE -> {
                if(isDigital())
                    btnReport.visibility = View.VISIBLE
                else {
                    btnCancelDeal.visibility = View.VISIBLE
                    if(!isDelivery)
                    {
                        enableOrganizeDelivery()
                    }
                    else{
                        enableSellerSetPrice()
                    }
                }
                enableProgressDescription()
                enableUserProfile()
                enableChatButton()
            }

            PurchaseItemStatus.DELIVERY_PRICE_DEFINED -> {
                btnCancelDeal.visibility = View.VISIBLE
                progressDescription.item = purchase
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

    fun enableItemDescription(){
        itemDescription.marketItem = marketItem
        itemDescription.currency = purchase?.currencyType
        itemDescription.visibility = View.VISIBLE
    }

    fun enableOrganizeDelivery(){
        organizeDelivery.visibility = View.VISIBLE
    }

    fun enableProgressView(){
        if(statusDescriptions.size > 0) {
            progressView.setProgressList(statusDescriptions)
            progressView.visibility = View.VISIBLE
        }
    }

    fun enableSellerSetPrice(){
        sellerSetPrice.item = purchase
        sellerSetPrice.visibility = View.VISIBLE
    }

    fun enableProgressDescription(){
        if(purchaseStatus != null) {
            progressDescription.status = purchaseStatus
            progressDescription.isDigital = isDigital()
            progressDescription.isSeller = true
            progressDescription.isDelivery = isDelivery
            progressDescription.visibility = View.VISIBLE
        }
    }

    fun enableUserProfile(){
        userProfile.user = user
        userProfile.visibility = View.VISIBLE
    }

    fun enableChatButton(){
        btnChat.visibility = View.VISIBLE
    }

    fun loadPurchase(){
        progressCenter.visibility = View.VISIBLE
        DataProvider.getPurchaseStatus(purchaseId!!, Consumer {
            purchase = it
            marketItem = it.item
            user = it.buyer
            statusDescriptions = it.statusDescriptions
            category = marketItem?.categoryId!!
            purchaseStatus = it.status
            isDelivery = it.withDelivery
            initView()
            progressCenter.visibility = View.GONE
        }, Consumer {
            handleException(it)
            progressCenter.visibility = View.GONE
        })
    }

}