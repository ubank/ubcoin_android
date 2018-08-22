package com.ubcoin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.model.response.SingleTransaction
import com.ubcoin.utils.moneyFormat
import com.ubcoin.utils.toDate
import com.ubcoin.utils.toTransactionDate
import java.util.*

/**
 * Created by Yuriy Aizenberg
 */
class TransactionsAdapter(context: Context) : BaseRecyclerAdapter<SingleTransaction, TransactionsAdapter.VHolder>(context) {

    private val ubcPostfix: String = context.getString(R.string.ubc_postfix)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VHolder {
        return VHolder(inflate(R.layout.item_transaction, p0))
    }

    override fun onBindViewHolder(vHolder: VHolder, p1: Int) {
        val transaction = getItem(p1)
        var date = transaction.createdDate.toDate()
        if (date == null) {
            date = Date()
        }
        vHolder.txtItemTransactionDate.text = date.toTransactionDate()
        vHolder.txtItemTransactionAmount.text = """${if (transaction.amountUBC > .0) "+ " else ""}${transaction.amountUBC.moneyFormat()} $ubcPostfix"""
    }

/*
    private fun formatPriceWithTwoDigits(currentPrice: Float): String {
        return String.format("%.2f", currentPrice)
    }
*/

    class VHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
        val txtItemTransactionDate = findView(R.id.txtItemTransactionDate) as TextView
        val txtItemTransactionAmount = findView(R.id.txtItemTransactionAmount) as TextView
    }


}