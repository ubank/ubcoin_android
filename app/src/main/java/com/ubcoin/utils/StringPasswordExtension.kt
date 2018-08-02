package com.ubcoin.utils

import com.nulabinc.zxcvbn.Zxcvbn

/**
 * Created by Yuriy Aizenberg
 */

fun String.checkAsPassword(): Int = if (isEmpty()) -1 else Zxcvbn().measure(this).score
