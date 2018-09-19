package com.ubcoin.fragment.transactions

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.event.UpdateTransactionMessage
import com.ubcoin.model.response.WithdrawResponse
import com.ubcoin.network.DataProvider
import com.ubcoin.network.HttpRequestException
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.bigMoneyFormat
import org.greenrobot.eventbus.EventBus
import java.net.HttpURLConnection

/**
 * Created by Yuriy Aizenberg
 */
private const val AMOUNT = "amount"
private const val COMMISSION = "commision"
private const val CONVERSION = "conversion"
private const val ADDRESS = "address"

class InfoFragment : BaseFragment() {

    companion object {
        fun createBundle(amount: Double, commission: Double, conversion: Double, address: String): Bundle {
            val args = Bundle()
            args.putString(ADDRESS, address)
            args.putDouble(AMOUNT, amount)
            args.putDouble(COMMISSION, commission)
            args.putDouble(CONVERSION, conversion)
            return args
        }
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        if (arguments == null) {
            activity?.onBackPressed()
            return
        }
        val address = arguments!!.getString(ADDRESS) ?: ""
        view.findViewById<TextView>(R.id.txtInfoAddress).text = address
        val amount = arguments!!.getDouble(AMOUNT)
        val commission = arguments!!.getDouble(COMMISSION)

        view.findViewById<TextView>(R.id.txtInfoAmountUBC).text = getString(R.string.balance_placeholder_prefix, (amount - commission).bigMoneyFormat())
        view.findViewById<TextView>(R.id.txtInfoAmountUSD).text = getString(R.string.transaction_amount_with_pats, arguments!!.getDouble(CONVERSION).bigMoneyFormat())
        view.findViewById<TextView>(R.id.txtInfoTransactionCommission).text = getString(R.string.balance_placeholder_prefix, commission.bigMoneyFormat())
        view.findViewById<TextView>(R.id.txtInfoTotalAmount).text = getString(R.string.balance_placeholder_prefix, amount.bigMoneyFormat())

        view.findViewById<View>(R.id.btnSend).setOnClickListener {
            performSend(address, amount)
        }
    }

    private fun performSend(address: String, amount: Double) {
        showProgressDialog(R.string.wait_please_title, R.string.withdraw_progress)
        DataProvider.withdraw(amount, address, object : SilentConsumer<WithdrawResponse> {
            override fun onConsume(t: WithdrawResponse) {
                hideProgressDialog()
                if (!t.isSuccess()) {
                    showSweetAlertDialog(getString(R.string.error), t.message)
                } else {
                    activity?.run {
                        Toast.makeText(this, R.string.withdraw_sucess, Toast.LENGTH_SHORT).show()
                        EventBus.getDefault().post(UpdateTransactionMessage())
                        onBackPressed()
                        onBackPressed()
                    }

                }
            }

        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                handleException(t)
            }
        })
    }

    override fun handleException(t: Throwable) {
        hideProgressDialog()
        super.handleException(t)
    }

    override fun handleByChild(httpRequestException: HttpRequestException): Boolean {
        if (httpRequestException.isServerError() && httpRequestException.errorCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            Toast.makeText(activity, R.string.withdraw_sucess, Toast.LENGTH_SHORT).show()
            EventBus.getDefault().post(UpdateTransactionMessage())
            activity?.onBackPressed()
            activity?.onBackPressed()
            return true
        }
        return super.handleByChild(httpRequestException)
    }

    override fun isFooterShow() = false

    override fun getLayoutResId() = R.layout.fragment_send_commit

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun getHeaderText() = R.string.info_label

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

}