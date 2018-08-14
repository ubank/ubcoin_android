package com.ubcoin.model.response.profile

import com.ubcoin.model.response.User

/**
 * Created by Yuriy Aizenberg
 */

data class ProfileCompleteResponse(val user: User, val accessToken: String)