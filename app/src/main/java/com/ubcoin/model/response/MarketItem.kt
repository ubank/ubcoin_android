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
        var favorite: Boolean,
        var status: MarketItemStatus?,
        val title: String?,
        val price: Double?,
        val shareUrl: String?,
        val priceInCurrency: Double?,
        val rate: Double?,
        val priceETH: Double?,
        val rateETH: Double?,
        val currency: String?,
        val purchases: List<Purchase>,
        val condition: String?,
        val fileUrl: String?,
        val statusDescription: String?
) : MarketItemMarker {

    fun isOwner() : Boolean {
        return ProfileHolder.isAuthorized() && ProfileHolder.user!!.id.equals(user?.id)
    }

    fun isPriceInCurrencyPresented() : Boolean {
        return priceInCurrency != null && currency != null
    }

    override fun toString(): String {
        return "MarketItem(id='$id', title=$title)"
    }


}