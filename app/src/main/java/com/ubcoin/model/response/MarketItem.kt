package com.ubcoin.model.response

import com.ubcoin.model.Purchase
import com.ubcoin.utils.ProfileHolder
import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class MarketItem(
        val id: String,
        val category: Category?,
        val categoryId: String?,
        val user: User?,
        val description: String?,
        val images: List<String>?,
        val location: Location?,
        val createdDate: String?,
        var favorite: Boolean?,
        var status: MarketItemStatus?,
        val title: String?,
        val price: Double?,
        val shareUrl: String?,
        val priceInCurrency: Double?,
        val rate: Double?,
        val priceETH: Double?,
        val rateETH: Double?,
        val currency: String?,
        private val purchases: List<Purchase>?,
        val condition: String?,
        val fileUrl: String?,
        val statusDescription: String?,
        val activePurchase: Purchase?
) : MarketItemMarker {

    fun isOwner() : Boolean {
        return ProfileHolder.isAuthorized() && ProfileHolder.getUserId().equals(user?.id)
    }

    fun isPriceInCurrencyPresented() : Boolean {
        return priceInCurrency != null && currency != null
    }

    override fun toString(): String {
        return "MarketItem(id='$id', title=$title)"
    }

    fun hasAction() : Boolean{
        if(activePurchase == null)
            return false
        else
            return activePurchase.needAction?:false
    }

    fun itemDetailsCanBeOpened(): Boolean{
        if(isOwner())
            return true
        else{
            if(status == MarketItemStatus.DEACTIVATED || status == MarketItemStatus.BLOCKED ||status == MarketItemStatus.CHECK || status == MarketItemStatus.CHECKING)
                return false

            if(status == MarketItemStatus.ACTIVE)
                return true
            else
            {
                if(activePurchase == null)
                    return false
                else
                    return(activePurchase.status != PurchaseItemStatus.CANCELLED)
            }
        }
    }

    fun purchaseDetailsCanBeOpened(): Boolean{
        if(activePurchase == null)
            return false
        else
            return(activePurchase.status != PurchaseItemStatus.CANCELLED)
    }
}