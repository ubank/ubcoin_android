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
import com.afollestad.materialdialogs.MaterialDialog
import com.ubcoin.activity.BaseActivity
import com.ubcoin.activity.IActivity
import com.ubcoin.network.HttpRequestException
import com.ubcoin.switcher.FragmentSwitcher

/**
 * Created by Yuriy Aizenberg
 */
abstract class BaseFragment : Fragment(), IFragmentBehaviorAware {

    private val AGREEMENT_URL : String = "https://ubcoin.io/user-agreement"

    val NO_HEADER_OBJECT = -1

    open fun showHeader() = true

    open fun getHeaderText() = NO_HEADER_OBJECT

    open fun getHeaderIcon() = NO_HEADER_OBJECT

    private var materialDialog: MaterialDialog? = null

    open fun isFirstLineFragment() = false

    abstract fun getLayoutResId() : Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutResId(), container, false)
        onViewInflated(view)
        return view
    }

    open fun onViewInflated(view: View) {

    }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
        changeActivityHeader(activity as IActivity)
        if (isGradientShow()) {
            showGradient()
        } else {
            hideGradient()
        }

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

    private fun changeActivityHeader(iActivity: IActivity) {
        val header = iActivity.getHeader()
        if (showHeader()) {
/*            val alphaAnimation = AlphaAnimation(1f, 0f)
            alphaAnimation.fillAfter = true
            alphaAnimation.duration = 300
            alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {

                }

                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                }

            })*/
            header?.visibility = View.VISIBLE
//            header?.startAnimation(alphaAnimation)
            changeActivityAttributes(iActivity)
        } else {
            header?.visibility = View.GONE
        }
    }

    fun hideGradient() {
        toggleGradient(false, activity as IActivity)
    }

    fun showGradient() {
        toggleGradient(true, activity as IActivity)
    }

    open fun isGradientShow() = true

    private fun toggleGradient(isVisible: Boolean, iActivity: IActivity) {
        val topGradient = iActivity.getTopGradient()
        topGradient?.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun hideFooter() {
        toggleFooter(false, activity as IActivity)
    }

    fun showFooter() {
        toggleFooter(true, activity as IActivity)
    }

    open fun isFooterShow() = true

    private fun toggleFooter(isVisible: Boolean, iActivity: IActivity) {
        val footer = iActivity.getFooter()
        footer?.run {
            post {
                layoutParams?.height = if (isVisible) ViewGroup.LayoutParams.WRAP_CONTENT else 0
            }
        }
    }

    private fun changeActivityAttributes(iActivity: IActivity) {
        if (getHeaderText() != NO_HEADER_OBJECT) {
            iActivity.getTopTextView()?.text = getString(getHeaderText())
        }
        if (getHeaderIcon() != NO_HEADER_OBJECT) {
            iActivity.getTopImageView()?.run {
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
                processHttpRequestException(t)
            }
            else -> processThrowable(t)
        }
    }

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

    protected fun hideViewQuitelly(vararg v : View?) {
        v.run {
            v.forEach {
                it?.visibility = View.GONE
            }
        }
    }

}