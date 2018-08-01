package com.ubcoin.utils

import com.nulabinc.zxcvbn.Zxcvbn

/**
 * Created by Yuriy Aizenberg
 */

object PasswordChecker {

    private val zxcvbn: Zxcvbn = Zxcvbn()

    /**
     * Mapping
     * # Integer from 0-4 (useful for implementing a strength bar)
     * # 0 Weak        （guesses < ^ 3 10）
     * # 1 Fair        （guesses <^ 6 10）
     * # 2 Good        （guesses <^ 8 10）
     * # 3 Strong      （guesses < 10 ^ 10）
     * # 4 Very strong （guesses >= 10 ^ 10）
     *
     */

    fun check(password: String): Int {
        if (password.isEmpty()) return -1
        return zxcvbn.measure(password).score
    }

}