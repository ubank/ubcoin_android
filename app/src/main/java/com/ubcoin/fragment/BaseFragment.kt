package com.ubcoin.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.ubcoin.R
import com.ubcoin.activity.BaseActivity
import com.ubcoin.activity.IActivity
import com.ubcoin.network.HttpRequestException
import com.ubcoin.switcher.FragmentSwitcher
import com.ubcoin.utils.collapse
import com.ubcoin.utils.expand
import com.ubcoin.view.menu.MenuBottomView
import kotlinx.android.synthetic.main.common_header.*

/**
 * Created by Yuriy Aizenberg
 */
abstract class BaseFragment : Fragment(), IFragmentBehaviorAware {

    private val AGREEMENT_URL: String = "https://ubcoin.io/user-agreement"

    val NO_HEADER_OBJECT = -1

    open fun getHeaderText() = NO_HEADER_OBJECT

    open fun getHeaderIcon() = NO_HEADER_OBJECT

    private var materialDialog: MaterialDialog? = null
    private var progressDialog: MaterialDialog? = null

    private var headerIcon: View? = null
    private var llHeaderImage: View? = null
    private var txtHeader: TextView? = null

    open fun isFirstLineFragment() = false

    abstract fun getLayoutResId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutResId(), container, false)
        onViewInflated(view)
        if (getTopHeaderTextId() != NO_HEADER_OBJECT) {
            txtHeader = view.findViewById(getTopHeaderTextId())
        }
        if (getTopLeftIconId() != NO_HEADER_OBJECT) {
            headerIcon = view.findViewById(getTopLeftIconId())
        }
        if (getTopLeftLayoutId() != NO_HEADER_OBJECT) {
            llHeaderImage = view.findViewById(getTopLeftLayoutId())
        }
        return view
    }

    open fun onViewInflated(view: View) {

    }

    open fun getTopLeftIconId() = R.id.imgHeaderLeft

    open fun getTopLeftLayoutId() = R.id.llHeaderLeft

    open fun getTopHeaderTextId() = R.id.txtHeader

    override fun onResume() {
        super.onResume()
        hideKeyboard()
        changeActivityAttributes()
        if (isFooterShow()) {
            showFooter()
        } else {
            hideFooter()
        }
    }

    fun getSwitcher(): FragmentSwitcher? {
        val activity = activity
        if (activity != null && activity is BaseActivity) return activity.fragmentSwitcher
        return null
    }

    fun showUserAgreement() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(AGREEMENT_URL)
        if (intent.resolveActivity(activity?.packageManager) != null) {
            startActivity(intent)
        } else {
            TODO()
        }
    }

    open fun isGradientShow() = true


    fun hideFooter() {
        toggleFooter(false, activity as IActivity)
    }

    fun showFooter() {
        toggleFooter(true, activity as IActivity)
    }

    open fun isFooterShow() = true

    private fun toggleFooter(isVisible: Boolean, iActivity: IActivity) {
        val footer = iActivity.getFooter()
        val container = iActivity.getContainer()
        if (footer == null) return
        (footer as MenuBottomView).run {
            if (!isExpanded && isVisible) {
                expand(200)
                isExpanded = true
            } else if (isExpanded && !isVisible) {
                collapse(200)
                isExpanded = false
            }
            container.requestLayout()
        }
    }

    private fun changeActivityAttributes() {
        if (getHeaderText() != NO_HEADER_OBJECT && txtHeader != null) {
            txtHeader?.text = getString(getHeaderText())
        }
        if (getHeaderIcon() != NO_HEADER_OBJECT) {
            imgHeaderLeft?.run {
                setImageResource(getHeaderIcon())
                setOnClickListener { onIconClick() }
            }
        }
    }

    fun hideKeyboard() {
        val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    open fun onIconClick() {

    }

    protected open fun handleException(t: Throwable) {
        hideSweetAlertDialog()
        when (t) {
            is HttpRequestException -> {
                if (!handleByChild(t)) {
                    processHttpRequestException(t)
                }
            }
            else -> processThrowable(t)
        }
    }

    protected open fun handleByChild(httpRequestException: HttpRequestException) = false

    private fun processHttpRequestException(httpRequestException: HttpRequestException) {
        if (httpRequestException.isServerError()) {
            showSweetAlertDialog("Server says", httpRequestException.toServerErrorString())
        } else {
            processThrowable(RuntimeException(httpRequestException.throwable))
        }
    }

    private fun processThrowable(throwable: Throwable) {
        showSweetAlertDialog("Ooops. Something goes wrong", "" + throwable.message)
    }

    private fun hideSweetAlertDialog() {
        materialDialog?.hide()
    }

    private fun showSweetAlertDialog(title: String, message: String) {
        activity?.run {
            materialDialog = MaterialDialog.Builder(this).title(title).content(message).build()
            materialDialog?.show()
        }

    }

    protected fun hideViewQuitelly(vararg v: View?) {
        v.run {
            v.forEach {
                it?.visibility = View.GONE
            }
        }
    }

    protected fun showProgressDialog(title: String, message: String) {
        activity?.run {
            hideProgressDialog()
            progressDialog = MaterialDialog.Builder(this)
                    .title(title)
                    .content(message)
                    .progress(true, 0)
                    .cancelable(false)
                    .build()
            progressDialog?.show()

        }
    }

    protected fun hideProgressDialog() {
        progressDialog?.hide()
    }

}