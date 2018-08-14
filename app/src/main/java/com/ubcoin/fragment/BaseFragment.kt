package com.ubcoin.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.Crashlytics
import com.ubcoin.R
import com.ubcoin.ThePreferences
import com.ubcoin.activity.BaseActivity
import com.ubcoin.activity.IActivity
import com.ubcoin.network.HttpRequestException
import com.ubcoin.network.NetworkConnectivityException
import com.ubcoin.network.SilentConsumer
import com.ubcoin.switcher.FragmentSwitcher
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.utils.collapse
import com.ubcoin.utils.expand
import com.ubcoin.view.menu.MenuBottomView
import kotlinx.android.synthetic.main.common_header.*
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

/**
 * Created by Yuriy Aizenberg
 */
abstract class BaseFragment : Fragment(), IFragmentBehaviorAware {

    private val agreementUrl: String = "https://ubcoin.io/user-agreement"
    private val expandCollapseDuration = 200L

    val noHeaderObject = -1

    open fun getHeaderText() = noHeaderObject

    open fun getHeaderIcon() = noHeaderObject

    private var materialDialog: MaterialDialog? = null
    private var progressDialog: MaterialDialog? = null

    private var headerIcon: View? = null
    private var llHeaderImage: View? = null
    var txtHeader: TextView? = null

    open fun isFirstLineFragment() = false

    abstract fun getLayoutResId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutResId(), container, false)
        onViewInflated(view)
        onViewInflated(view, savedInstanceState)
        if (getTopHeaderTextId() != noHeaderObject) {
            txtHeader = view.findViewById(getTopHeaderTextId())
        }
        if (getTopLeftIconId() != noHeaderObject) {
            headerIcon = view.findViewById(getTopLeftIconId())
        }
        if (getTopLeftLayoutId() != noHeaderObject) {
            llHeaderImage = view.findViewById(getTopLeftLayoutId())
        }
        return view
    }

    open fun onViewInflated(view: View) {

    }

    open fun onViewInflated(view: View, savedInstanceState: Bundle?) {

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
        intent.data = Uri.parse(agreementUrl)
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
                expand(expandCollapseDuration)
                isExpanded = true
            } else if (isExpanded && !isVisible) {
                collapse(expandCollapseDuration)
                isExpanded = false
            }
            container.requestLayout()
        }
    }

    private fun changeActivityAttributes() {
        if (getHeaderText() != noHeaderObject && txtHeader != null) {
            txtHeader?.text = getString(getHeaderText())
        }
        if (getHeaderIcon() != noHeaderObject) {
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
        Crashlytics.logException(t)
        try {
            hideSweetAlertDialog()
            when (t) {
                is HttpRequestException -> {
                    val errorCode = t.errorCode
                    if (errorCode == HTTP_UNAUTHORIZED || errorCode == HTTP_BAD_REQUEST) {
                        if (onUnauthorized(t)) return
                        if (errorCode == HTTP_UNAUTHORIZED) {
                            ThePreferences().setToken(null)
                            ThePreferences().clearProfile()
                            ProfileHolder.user = null
                            return
                        }
                    }
                    if (!handleByChild(t)) {
                        processHttpRequestException(t)
                    }
                }
                is NetworkConnectivityException -> {
                    onNoNetworkException(t)
                }
                else -> processThrowable(t)
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, """${e.message}""", e)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onNoNetworkException(exception: NetworkConnectivityException) {
        showSweetAlertDialog("Error", getString(R.string.no_network_error))
    }

    protected open fun onUnauthorized(httpRequestException: HttpRequestException) = false

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

    fun showSweetAlertDialog(title: String, message: String) {
        activity?.run {
            materialDialog = MaterialDialog.Builder(this).title(title).content(message).build()
            materialDialog?.show()
        }

    }

    protected fun hideViewsQuietly(vararg v: View?) {
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

    protected fun showNeedToRegistration() {
        activity?.run {
            MaterialDialog.Builder(this)
                    .title("Error")
                    .content(R.string.need_to_logged_in)
                    .build()
                    .show()
        }
    }

}