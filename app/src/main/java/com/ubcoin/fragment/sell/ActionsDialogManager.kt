package com.ubcoin.fragment.sell

import android.app.Dialog
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.ubcoin.R
import com.ubcoin.model.response.MarketItemStatus

/**
 * Created by Yuriy Aizenberg
 */

object ActionsDialogManager {

    @Suppress("NON_EXHAUSTIVE_WHEN")
    fun show(context: Context, doActions: MarketItemStatus.DoActions, callback: ((item: Items, dialog: Dialog) -> Unit)?) {
        if (doActions == MarketItemStatus.DoActions.NOTHING) return
        val materialDialog: MaterialDialog = MaterialDialog.Builder(context)
                .customView(R.layout.common_dialog_actions, false)
                .build()

        materialDialog.findViewById(R.id.llClose).setOnClickListener {
            materialDialog.dismiss()
        }

        val firstContainer = materialDialog.findViewById(R.id.llDialogFirstContainer)
        val secondContainer = materialDialog.findViewById(R.id.llDialogSecondContainer)

        val firstTextView = materialDialog.findViewById(R.id.txtDialogFirst) as TextView
        val secondTextView = materialDialog.findViewById(R.id.txtDialogSecond) as TextView


        firstTextView.setText(R.string.dialog_action_edit)
        firstContainer.setOnClickListener { callback?.invoke(Items.EDIT, materialDialog) }

        when (doActions) {
            MarketItemStatus.DoActions.EDIT_OR_DEACTIVATE -> {
                secondTextView.setText(R.string.dialog_action_deactivate)
                secondContainer.setOnClickListener { callback?.invoke(Items.DEACTIVATE, materialDialog) }
            }
            MarketItemStatus.DoActions.EDIT_OR_ACTIVATE -> {
                secondTextView.setText(R.string.dialog_action_activate)
                secondContainer.setOnClickListener { callback?.invoke(Items.ACTIVATE, materialDialog) }
            }
            MarketItemStatus.DoActions.EDIT_ONLY -> {
                secondContainer.visibility = View.GONE
            }
        }
        materialDialog.show()
    }

    enum class Items {
        EDIT,
        ACTIVATE,
        DEACTIVATE
    }

}