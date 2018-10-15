package com.ubcoin.fragment.transactions

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.model.response.TopUp

/**
 * Created by Yuriy Aizenberg
 */
@Deprecated("Use fragment instead of this")
object TopUpViewManager {

    @SuppressLint("StaticFieldLeak")
    private var dialog: MaterialDialog? = null

    fun show(context: Context, topUp: TopUp, listener: ITopupView?) {

        dismiss()

        dialog = MaterialDialog.Builder(context)
                .customView(R.layout.dialog_top_up, false)
                .build()

        dialog?.run {
            val txtHash = findViewById(R.id.txtTopUpHash) as TextView
            txtHash.text = topUp.ubCoinAddress

            val imgQrCode = findViewById(R.id.imgTopUpQrCode) as ImageView
            GlideApp.with(context).load(topUp.qrURL).into(imgQrCode)

            findViewById(R.id.imgTopUpCopy).setOnClickListener {
                listener?.onAction(ITopupView.Action.COPY)
            }

            findViewById(R.id.llLinkCossIO).setOnClickListener {
                listener?.onAction(ITopupView.Action.FIRST_LINK)
            }

            findViewById(R.id.llLinkERC).setOnClickListener {
                listener?.onAction(ITopupView.Action.FIRST_LINK)
            }

            show()
        }

    }

    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }

    @Deprecated("Use fragment instead of this")
    interface ITopupView {

        enum class Action {
            COPY, FIRST_LINK, SECOND_LINK
        }

        fun onAction(action: Action)
    }

}