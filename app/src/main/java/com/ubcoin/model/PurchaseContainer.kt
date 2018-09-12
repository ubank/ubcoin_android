package com.ubcoin.model

/**
 * Created by Yuriy Aizenberg
 */
class PurchaseContainer {

    val activePurchases =  ArrayList<Purchase>()
    val otherPurchases = ArrayList<Purchase>()

    fun shouldDivideByBlocks() = !activePurchases.isEmpty() && !otherPurchases.isEmpty()


}