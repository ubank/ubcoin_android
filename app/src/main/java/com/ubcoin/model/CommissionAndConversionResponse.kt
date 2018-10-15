package com.ubcoin.model

import com.ubcoin.model.response.Commission
import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class CommissionAndConversionResponse(val commission: Commission, val conversionResponse: ConversionResponse) : Serializable