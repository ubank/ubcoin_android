package com.ubcoin.fragment.transactions

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.adapter.TransactionsAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.CryptoCurrency
import com.ubcoin.model.event.UpdateTransactionMessage
import com.ubcoin.model.response.MyBalance
import com.ubcoin.model.response.TopUp
import com.ubcoin.model.response.TransactionListResponse
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import com.ubcoin.utils.copyToClipBoard
import com.ubcoin.utils.moneyFormat
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by Yuriy Aizenberg
 */

private const val LIMIT = 30



class MyBalanceFragment : BaseFragment() {

    companion object {
        fun getBundle(currencyType: CryptoCurrency): Bundle {
            val bundle = Bundle()
            bundle.putSerializable("currency", currencyType)
            return bundle
        }
    }

    private lateinit var txtMyBalance: TextView

    private lateinit var llMyBalanceSend: View
    private lateinit var imgMyBalanceSend: ImageView
    private lateinit var txtMyBalanceSend: TextView

    private lateinit var txtTransactionsHeader: TextView
    private lateinit var rvTransactions: RecyclerView
    private lateinit var progressCenter: View
    private lateinit var progressBottom: View
    private lateinit var llNoTransactions: View

    private var isLoading = false
    private var isEndOfLoading = false
    private var transactionDisposable: Disposable? = null
    private var currentPage: Int = -1
    private lateinit var transactionsAdapter: TransactionsAdapter

    private lateinit var currencyType: CryptoCurrency

    override fun getLayoutResId() = R.layout.fragment_my_balance

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        view.findViewById<View>(R.id.llMyBalanceTopUp).setOnClickListener { onTopUpClick() }

        currencyType = if(arguments?.getSerializable("currency") == null) CryptoCurrency.UBC else arguments?.getSerializable("currency") as CryptoCurrency

        txtMyBalance = view.findViewById(R.id.txtMyBalance)

        llMyBalanceSend = view.findViewById(R.id.llMyBalanceSend)
        imgMyBalanceSend = view.findViewById(R.id.imgMyBalanceSend)
        txtMyBalanceSend = view.findViewById(R.id.txtMyBalanceSend)

        txtTransactionsHeader = view.findViewById(R.id.txtTransactionsHeader)
        rvTransactions = view.findViewById(R.id.rvTransactions)
        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)
        llNoTransactions = view.findViewById(R.id.llNoTransactions)

        view.findViewById<View>(R.id.llHeaderRight).setOnClickListener {
            resetData()
        }

        rvTransactions.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(activity)
        rvTransactions.layoutManager = linearLayoutManager
        rvTransactions.addOnScrollListener(object : EndlessRecyclerViewOnScrollListener(linearLayoutManager) {
            override fun onLoadMore() {
                loadTransactions(false)
            }
        })

        transactionsAdapter = TransactionsAdapter(activity!!)
        transactionsAdapter.setHasStableIds(true)

        rvTransactions.adapter = transactionsAdapter

        changeSendBackground(false)
        queryBalance()
        loadTransactions(false)

    }

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun isFooterShow() = false

    override fun onIconClick() {
        super.onIconClick()
        activity!!.onBackPressed()
    }

    private fun queryBalance() {
        hideProgressDialog()
        showProgressDialog(R.string.loading, R.string.wait_please_message)
        loadBalance()
    }

    private fun loadBalance() {
        DataProvider.balance(object : SilentConsumer<MyBalance> {
            override fun onConsume(t: MyBalance) {
                hideProgressDialog()

                var effectiveAmount = if(currencyType == CryptoCurrency.UBC) t.effectiveAmount else t.effectiveAmountETH

                if(currencyType == CryptoCurrency.UBC)
                    txtMyBalance.text = "${effectiveAmount.moneyFormat()} ${getString(R.string.ubc_postfix)}"
                else
                    txtMyBalance.text = "${effectiveAmount.moneyFormat()} ${getString(R.string.eth_postfix)}"

                if (effectiveAmount > 0f) {
                    changeSendBackground(true)
                } else {
                    changeSendBackground(false)
                }
            }

        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                hideProgressDialog()
                handleException(t)
            }

        })
    }

    private fun stopLoading() {
        hideViewsQuietly(progressBottom, progressCenter)
        isLoading = false
    }

/*    private fun formatPriceWithTwoDigits(currentPrice: Float): String {
        return String.format("%.2f", currentPrice)
    }*/

    private fun resetData() {
        queryBalance()
        stopLoading()
        isEndOfLoading = false
        isLoading = false
        transactionDisposable?.dispose()
        currentPage = -1
        loadBalance()
        loadTransactions(true)
    }

    private fun loadTransactions(clear: Boolean) {
        if (isLoading || isEndOfLoading) return
        isLoading = true

        if (transactionsAdapter.isEmpty()) {
            progressCenter.visibility = View.VISIBLE
        } else {
            progressBottom.visibility = View.VISIBLE
        }

        currentPage++
        DataProvider.transactions(currencyType, LIMIT, currentPage, object : SilentConsumer<TransactionListResponse> {
            override fun onConsume(t: TransactionListResponse) {
                stopLoading()
                if (clear) {
                    transactionsAdapter.clear()
                }
                if (t.data.size < LIMIT) {
                    isEndOfLoading = true
                }
                transactionsAdapter.addData(t.data)
                if (transactionsAdapter.isEmpty()) {
                    hideTransactions()
                } else {
                    showTransactions()
                }
            }


        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                stopLoading()
                handleException(t)
            }

        })
    }

    private fun onTopUpClick() {
        getSwitcher()?.addTo(TopUpFragment::class.java, TopUpFragment.getBundle(currencyType), false)
        /*showProgressDialog(R.string.wait_please_title, R.string.loading)
        DataProvider.topUp(object : SilentConsumer<TopUp> {
            override fun onConsume(t: TopUp) {
                hideProgressDialog()
                onTopUpReady(t)
            }
        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                hideProgressDialog()
                handleException(t)
            }

        })*/
    }

    fun onTopUpReady(t: TopUp) {
        activity?.run {
            TopUpViewManager.show(this, t, object : TopUpViewManager.ITopupView {
                override fun onAction(action: TopUpViewManager.ITopupView.Action) {
                    when (action) {
                        TopUpViewManager.ITopupView.Action.COPY -> {
                            t.ubCoinAddress.copyToClipBoard(activity!!, "Copy to clipboard")
                            Toast.makeText(activity, R.string.address_copied_toast, Toast.LENGTH_SHORT).show()
                        }
                        TopUpViewManager.ITopupView.Action.FIRST_LINK -> {
                            closeDialogAndOpenExternalLink(getString(R.string.coss_io))
                        }
                        TopUpViewManager.ITopupView.Action.SECOND_LINK -> {
                            closeDialogAndOpenExternalLink(getString(R.string.coss_io))
                        }
                    }
                }

            })
        }
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onUpdateMessage(updateTransactionMessage: UpdateTransactionMessage) {
        resetData()
    }

    private fun closeDialogAndOpenExternalLink(link: String) {
        TopUpViewManager.dismiss()
        TheApplication.instance.openExternalLink(activity!!, link)
    }

    private fun onSendClick() {
        getSwitcher()?.addTo(SendFragment::class.java, SendFragment.getBundle(currencyType), false)
    }

    private fun changeSendBackground(isActive: Boolean) {
        if (isActive) {
            llMyBalanceSend.setOnClickListener { onSendClick() }
            imgMyBalanceSend.setImageResource(R.drawable.ic_send_white)
            llMyBalanceSend.setBackgroundResource(R.drawable.rounded_green_filled_button_smallr)
            txtMyBalanceSend.setTextColor(ContextCompat.getColor(activity!!, android.R.color.white))
        } else {
            llMyBalanceSend.setOnClickListener { }
            imgMyBalanceSend.setImageResource(R.drawable.ic_send_grey)
            llMyBalanceSend.setBackgroundResource(R.drawable.rounded_grey_filled_button_smallr)
            txtMyBalanceSend.setTextColor(ContextCompat.getColor(activity!!, R.color.myBalanceInactiveColor))
        }
    }

    private fun showTransactions() {
        rvTransactions.visibility = View.VISIBLE
        txtTransactionsHeader.visibility = View.VISIBLE
        llNoTransactions.visibility = View.GONE
    }

    private fun hideTransactions() {
        rvTransactions.visibility = View.GONE
        txtTransactionsHeader.visibility = View.GONE
        llNoTransactions.visibility = View.VISIBLE
    }

}