package com.ubcoin.network.request

import com.ubcoin.model.response.Location

/**
 * Created by Yuriy Aizenberg
 */
data class UpdateProductRequest(
        val id: String,
        val categoryId: String?,
        val title: String,
        val description: String?,
        val price: Double,
        val location: Location?,
        val disableNotifyToEmail: Boolean,
        val agreement: Boolean,
        var images: List<String>
) {

    companion object {
        fun fromCreateRequest(createProductRequest: CreateProductRequest, id: String) : UpdateProductRequest {
            return UpdateProductRequest(
                    id,
                    createProductRequest.categoryId,
                    createProductRequest.title,
                    createProductRequest.description,
                    createProductRequest.price,
                    createProductRequest.location,
                    createProductRequest.disableNotifyToEmail,
                    createProductRequest.agreement,
                    createProductRequest.images
            )
        }
    }

}