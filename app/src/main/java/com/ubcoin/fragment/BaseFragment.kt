package com.ubcoin.fragment

import android.app.Activity
import android.support.v4.app.Fragment
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
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

    val NO_HEADER_OBJECT = -1

    open fun showHeader(): Boolean = true

    open fun getHeaderText(): Int = NO_HEADER_OBJECT

    open fun getHeaderIcon(): Int = NO_HEADER_OBJECT

    private var materialDialog: MaterialDialog? = null

    override fun onResume() {
        super.onResume()
        hideKeyboard()
        changeActivityHeader(activity as IActivity)
    }

    fun getSwitcher(): FragmentSwitcher? {
        val activity = activity
        if (activity != null && activity is BaseActivity) return activity.fragmentSwitcher
        return null
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