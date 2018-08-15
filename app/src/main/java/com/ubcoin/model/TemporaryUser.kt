package com.ubcoin.model

import com.mukesh.countrypicker.Country
import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
data class TemporaryUser(var country: Country?,
                         var language: String?,
                         var localAvatarUrl: String?) : Serializable