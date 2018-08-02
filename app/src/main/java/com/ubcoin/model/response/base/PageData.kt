package com.ubcoin.model.response.base

/**
 * Created by Yuriy Aizenberg
 */
data class PageData(
        val totalElements: Int,
        val totalPages: Int,
        val page: Int,
        val size: Int
)