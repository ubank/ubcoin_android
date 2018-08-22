package com.ubcoin.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.nulabinc.zxcvbn.Zxcvbn
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Yuriy Aizenberg
 */

fun String.checkAsPassword(): Int = if (isEmpty()) -1 else Zxcvbn().measure(this).score


fun String.toDate() : Date? {
    return try {
        SimpleDateFormat("yyyyMMdd'T'HHmmssZ", Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}

fun Date.toTransactionDate() : String {
    return SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(this)
}

fun String.copyToClipBoard(context: Context, label: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, this)
    clipboardManager.primaryClip = clipData
}

fun Float.moneyFormat() : String {
    return this.toDouble().moneyFormat()
}

fun Double.moneyFormat() : String {
    val formatSymbols = DecimalFormatSymbols(Locale.getDefault())
    formatSymbols.decimalSeparator = '.'
    formatSymbols.groupingSeparator = ' '
    val formatter = DecimalFormat("###,###.##", formatSymbols)
    var format = formatter.format(this)
    if (!format.contains(".")) {
        format += ".00"
    } else {
        val afterSeparatorString = format.split(".")[1]
        if (afterSeparatorString.length == 1) {
            format += "0"
        }
    }
    return format
}

fun String.moneyFormat() : String {
    return this.toDouble().moneyFormat()
}

fun Double.bigMoneyFormat() : String {
    val formatSymbols = DecimalFormatSymbols(Locale.getDefault())
    formatSymbols.decimalSeparator = '.'
    formatSymbols.groupingSeparator = ' '
    val formatter = DecimalFormat("###,###.#####", formatSymbols)
    var format = formatter.format(this)
    if (!format.contains(".")) {
        format += ".00"
    } else {
        val afterSeparatorString = format.split(".")[1]
        if (afterSeparatorString.length == 1) {
            format += "0"
        }
    }
    return format
}