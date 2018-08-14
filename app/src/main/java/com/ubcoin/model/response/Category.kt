package com.ubcoin.model.response

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class Category(
        val id: String,
        val name: String
) : Serializable