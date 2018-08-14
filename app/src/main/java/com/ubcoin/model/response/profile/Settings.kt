package com.ubcoin.model.response.profile

import com.ubcoin.model.response.Location
import java.io.Serializable


/**
 * Created by Yuriy Aizenberg
 */
data class Settings(
        val additionalCurrencies: List<String>?,
        val location: Location?,
        val twoFactorAuth: TwoFactorAuth?
) : Serializable