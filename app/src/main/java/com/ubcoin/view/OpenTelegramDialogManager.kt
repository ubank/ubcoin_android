package com.ubcoin.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.ubcoin.R

/**
 * Created by Yuriy Aizenberg
 */

object OpenTelegramDialogManager {

    fun showDialog(context: Context, callback: ITelegramDialogCallback) {
        val materialDialog = MaterialDialog.Builder(context)
                .content(R.string.open_telegram_text)
                .positiveText(R.string.open)
                .positiveColor(ContextCompat.getColor(context, R.color.greenMainColor))
                .onPositive { dialog, _ -> callback.onPositiveClick(dialog) }
                .build()
        materialDialog!!.show()
    }


    interface ITelegramDialogCallback {

        fun onPositiveClick(materialDialog: MaterialDialog)

    }

}