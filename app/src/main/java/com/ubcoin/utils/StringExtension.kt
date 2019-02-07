package com.ubcoin.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import com.nulabinc.zxcvbn.Zxcvbn
import com.ubcoin.model.response.User
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


/**
 * Created by Yuriy Aizenberg
 */

fun String.checkAsPassword(): Int = if (isEmpty()) -1 else Zxcvbn().measure(this).score


fun String.toDate(): Date? {
    return try {
            SimpleDateFormat("yyyyMMdd'T'HHmmssZ", Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}

fun Date.toTransactionDate(): String {
    return SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(this)
}

fun String.copyToClipBoard(context: Context, label: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, this)
    clipboardManager.primaryClip = clipData
}

fun Double.moneyRoundedFormat() : String {
    val formatSymbols = DecimalFormatSymbols(Locale.getDefault())
    formatSymbols.groupingSeparator = ' '
    formatSymbols.decimalSeparator = '.'
    val formatter = DecimalFormat("###,###.##", formatSymbols)
    return formatter.format(this)
}

fun Double.moneyRoundedFormatETH() : String {
    val formatSymbols = DecimalFormatSymbols(Locale.getDefault())
    formatSymbols.groupingSeparator = ' '
    formatSymbols.decimalSeparator = '.'
    val formatter = DecimalFormat("###,###.####", formatSymbols)
    formatter.minimumFractionDigits = 4
    return formatter.format(this)
}


fun Double.moneyFormatETH(): String {
    val formatSymbols = DecimalFormatSymbols(Locale.getDefault())
    formatSymbols.decimalSeparator = '.'
    formatSymbols.groupingSeparator = ' '
    val formatter = DecimalFormat("###,###.####", formatSymbols)
    formatter.minimumFractionDigits = 4
    var format = formatter.format(this)
    return format
}

fun Double.rateRoundedFormat() : String {
    val formatSymbols = DecimalFormatSymbols(Locale.getDefault())
    formatSymbols.groupingSeparator = ' '
    formatSymbols.decimalSeparator = '.'
    val formatter = DecimalFormat("#.####", formatSymbols)
    return formatter.format(this)
}

fun Double.rateRoundedFormatETH() : String {
    val formatSymbols = DecimalFormatSymbols(Locale.getDefault())
    formatSymbols.groupingSeparator = ' '
    formatSymbols.decimalSeparator = '.'
    val formatter = DecimalFormat("#.######", formatSymbols)
    formatter.minimumFractionDigits = 6
    return formatter.format(this)
}

fun Float.moneyFormat(): String {
    return this.toDouble().moneyFormat()
}

fun Double.moneyFormat(): String {
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

fun String.moneyFormat(): String {
    return this.toDouble().moneyFormat()
}

fun Double.bigMoneyFormat(): String {
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

fun User.getNickName() : String {
    if (TextUtils.isEmpty(name)) return ""
    return WordUtils.capitalizeFully(name).replace(" ", "")
}

fun User.getCapitalizedName() : String {
    if (TextUtils.isEmpty(name)) return ""
    return WordUtils.capitalizeFully(name)
}

fun Long.toTimeLaps() : String {
    if (this <= 0L) return "00:00:00"
    /*//return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(this))
    val timeInMilliSeconds = this
    val seconds = timeInMilliSeconds / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    return "${hours%24}:${minutes%60}:${seconds%60}"*/
    val millis = this
    return String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
}
fun String.verifyUrl() : Boolean {

    val p = Pattern.compile("^(?:(?:https?|ftp):\\/\\/)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})))(?::\\d{2,5})?(?:\\/\\S*)?\$");
    val m = p.matcher(this)
    return m.matches()
}