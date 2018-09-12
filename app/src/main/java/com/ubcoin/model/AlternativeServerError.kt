package com.ubcoin.model

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
//    {"timestamp":1535392715011,"status":500,"error":"Internal Server Error","message":"No message available","path":"/api/wallet/transactions"}//
data class AlternativeServerError(val timestamp: Long,
                                  val status: Int,
                                  val error: String,
                                  val message: String,
                                  val path: String) : Serializable