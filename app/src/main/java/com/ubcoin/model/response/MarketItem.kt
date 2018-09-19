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
        val currency: String?,
        val purchases: List<Purchase>
) : MarketItemMarker {

    fun isOwner() : Boolean {
        return ProfileHolder.isAuthorized() && ProfileHolder.user!!.id.equals(user?.id)
    }

    fun isPriceInCurrencyPresented() : Boolean {
        return priceInCurrency != null && currency != null
    }

}