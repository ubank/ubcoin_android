package com.ubcoin.fragment.transactions

import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.ConversionResponse
import com.ubcoin.model.response.Commission
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.TextWatcherAdatepr
import com.ubcoin.utils.bigMoneyFormat
import com.ubcoin.utils.moneyFormat
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Yuriy Aizenberg
 */
class SendFragment : BaseFragment() {


    private lateinit var edtSendAddress: EditText
    private lateinit var edtSendAmount: EditText
    private lateinit var txtSendAmount: TextView
    private lateinit var txtSendCommission: TextView
    private lateinit var btnContinue: Button

    private var currentAmount: Double = .0
    private var currentCommission: Double = .0
    private var currentConversion: Double = .0

    private val isCommissionCalculated = AtomicBoolean(false)


    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        edtSendAddress = view.findViewById(R.id.edtSendAddress)
        edtSendAmount = view.findViewById(R.id.edtSendAmount)
        txtSendAmount = view.findViewById(R.id.txtSendAmount)
        txtSendCommission = view.findViewById(R.id.txtSendCommission)
        btnContinue = view.findViewById(R.id.btnContinue)

        edtSendAddress.addTextChangedListener(object : TextWatcherAdatepr() {
            override fun afterTextChanged(p0: Editable?) {
                super.afterTextChanged(p0)
                if (checkAllCalculated()) {
                    enableNext()
                } else {
                    disableNext()
                }
            }
        })
        edtSendAmount.setOnClickListener { openPriceDialog() }
        edtSendAddress.setOnEditorActionListener(object : ImeDoneActionHandler() {
            override fun onActionCall() {
                hideKeyboard()
                openPriceDialog()
            }

        })
    }

    private fun checkAllCalculated(): Boolean {
        return edtSendAddress.text.toString().isNotBlank()
                && currentAmount > .0
                && currentCommission > .0
                && currentConversion > .0
                && isCommissionCalculated.get()

    }

    override fun getLayoutResId() = R.layout.fragment_send

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun getHeaderText() = R.string.send_fist_capital

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

    override fun isFooterShow() = false


    private fun openPriceDialog() {
        val materialDialog = MaterialDialog.Builder(activity!!)
                .customView(R.layout.fragment_content_select_amount, false)
                .build()
        val edtPrice: MaterialEditText = materialDialog.findViewById(R.id.edtPrice) as MaterialEditText

        materialDialog.findViewById(R.id.btnDialogCancel).setOnClickListener {
            materialDialog.dismiss()
        }

        materialDialog.findViewById(R.id.btnDialogDone).setOnClickListener {
            materialDialog.dismiss()
            currentAmount = try {
                edtPrice.text.toString().toDouble()
            } catch (e: Exception) {
                .0
            }
            if (currentAmount < .0) {
                currentAmount = .0
            }
            setCurrentAmount()

        }
        if (currentAmount > 0f) {
            edtPrice.setText(currentAmount.toString())
        }
        materialDialog.show()
    }

    private fun setCurrentAmount() {
        if (currentAmount > .0) {
            edtSendAmount.setText(getString(R.string.balance_placeholder_prefix, currentAmount.moneyFormat()))
            calculateCommission()
            calculateConversion()
        } else {
            edtSendAmount.text = null
            txtSendAmount.setText(R.string.transaction_send_amount_empty)
        }

    }

    private fun enableNext() {
        btnContinue.setBackgroundResource(R.drawable.rounded_green_filled_button_smallr)
        btnContinue.setOnClickListener {
            goNext()
        }
    }

    private fun goNext() {
        getSwitcher()?.addTo(
                InfoFragment::class.java,
                InfoFragment.createBundle(currentAmount, currentCommission, currentConversion, edtSendAddress.text.toString()),
                false
        )
    }

    private fun disableNext() {
        btnContinue.setBackgroundResource(R.drawable.rounded_green_filled_transparent_button_smallr)
        btnContinue.setOnClickListener { }
    }

    private fun calculateCommission() {
        isCommissionCalculated.set(false)
        currentCommission = .0
        disableNext()
        DataProvider.getCommission(currentAmount, object : SilentConsumer<Commission> {
            override fun onConsume(t: Commission) {
                currentCommission = t.commission
                isCommissionCalculated.set(true)
                txtSendCommission.text = getString(R.string.transaction_commission_format, t.commission.bigMoneyFormat())
                if (checkAllCalculated()) {
                    enableNext()
                } else {
                    disableNext()
                }
            }

        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                handleException(t)
            }

        })
    }

    private fun calculateConversion() {
        currentConversion = .0
        disableNext()
        DataProvider.getConversionFromUBC(currentAmount.toString(), object : SilentConsumer<ConversionResponse> {
            override fun onConsume(t: ConversionResponse) {
                currentConversion = t.amount
                txtSendAmount.text = getString(R.string.balance_placeholder_prefix_usd, t.amount.bigMoneyFormat())
                if (checkAllCalculated()) {
                    enableNext()
                } else {
                    disableNext()
                }
            }

        }, object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                handleException(t)
            }

        })
    }

}