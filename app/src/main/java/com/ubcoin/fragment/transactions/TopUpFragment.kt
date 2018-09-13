package com.ubcoin.fragment.transactions

import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.TopUp
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.copyToClipBoard
import com.ubcoin.utils.toTimeLaps
import io.reactivex.disposables.Disposable

/**
 * Created by Yuriy Aizenberg
 */
class TopUpFragment : BaseFragment() {

    private lateinit var txtTopUpTimer: TextView
    private lateinit var txtTopUpHash: TextView
    private lateinit var progressCenter: View
    private lateinit var llCopyCardContainer: CardView
    private lateinit var llChooseExchangeToBuyUBC: View
    private var topUp: TopUp? = null
    private var timeLapsManager: TimeLapsManager? = null
    private var disposable: Disposable? = null

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        txtTopUpHash = view.findViewById(R.id.txtTopUpHash)
        txtTopUpTimer = view.findViewById(R.id.txtTopUpTimer)
        progressCenter = view.findViewById(R.id.progressCenter)
        llCopyCardContainer = view.findViewById(R.id.llCopyCardContainer)
        llChooseExchangeToBuyUBC = view.findViewById(R.id.llChooseExchangeToBuyUBC)
        llChooseExchangeToBuyUBC.setOnClickListener {
            getSwitcher()?.addTo(ChooseExchangeFragment::class.java)
        }
        beginLoading()
        DataProvider.topUp(object : SilentConsumer<TopUp> {
            override fun onConsume(t: TopUp) {
                endLoading()
                successLoading()
                this@TopUpFragment.topUp = t
                setupData()
            }

        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                endLoading()
                handleException(t)
            }
        })
    }

    override fun onDestroyView() {
        timeLapsManager?.stop()
        disposable?.dispose()
        super.onDestroyView()
    }

    private fun setupData() {
        topUp?.let {
            txtTopUpHash.text = it.ubCoinAddress
            val millisBetween = it.millisBetween()
            if (millisBetween <= 0L) {
                txtTopUpTimer.text = millisBetween.toTimeLaps()
            } else {
                timeLapsManager = TimeLapsManager()
                disposable = timeLapsManager!!.subscribe(it.ubCoinAddress, millisBetween, object : SilentConsumer<Long> {
                    override fun onConsume(t: Long) {
                        txtTopUpTimer.text = t.toTimeLaps()
                        if (t <= 1000L) {
                            txtTopUpTimer.text = 0.toLong().toTimeLaps()
                        }
                    }
                })
            }
        }

    }

    private fun beginLoading() {
        llCopyCardContainer.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.greenMainColorTransparent))
        progressCenter.visibility = View.VISIBLE
        llCopyCardContainer.setOnClickListener { /*pass*/ }
    }

    private fun endLoading() {
        progressCenter.visibility = View.GONE
    }

    private fun successLoading() {
        llCopyCardContainer.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.greenMainColor))
        llCopyCardContainer.setOnClickListener {
            makeCopy()
        }
    }

    private fun makeCopy() {
        topUp!!.ubCoinAddress.copyToClipBoard(activity!!, "Copy to clipboard")
        Toast.makeText(activity, R.string.address_copied_toast, Toast.LENGTH_SHORT).show()
    }


    override fun getLayoutResId() = R.layout.fragment_topup

    override fun getHeaderText() = R.string.top_up

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

}