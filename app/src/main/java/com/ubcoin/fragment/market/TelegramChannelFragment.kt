package com.ubcoin.fragment.market

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.R.id.webView
import android.webkit.WebSettings
import android.os.Build
import android.webkit.CookieManager
import com.ubcoin.ThePreferences


/**
 * Created by Yuriy Aizenberg
 */

private const val BUNDLE_WEB_ARG = "Bundle_web_arg"
private const val BUNDLE_HEADER = "Bundle_header"

class TelegramChannelFragment : BaseFragment() {

    private var url: String? = null
    private var header: String? = null
    private lateinit var webView: WebView

    companion object {
        fun getBundle(url: String, header: String): Bundle {
            val bundle = Bundle()
            bundle.putString(BUNDLE_WEB_ARG, url)
            bundle.putString(BUNDLE_HEADER, header)
            return bundle
        }
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        url = arguments!!.getString(BUNDLE_WEB_ARG)
        header = arguments!!.getString(BUNDLE_HEADER)
        webView = view.findViewById(R.id.webView)
        txtHeader?.text = header
        initializeWebView()
        webView.loadUrl(url)
    }

    private fun initializeWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= 21) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            val cookieManager = CookieManager.getInstance()
//            cookieManager.setCookie("set-cookie", ThePreferences().getWVCookie())
            cookieManager.setAcceptThirdPartyCookies(webView, true)
        }
    }

    override fun getLayoutResId() = R.layout.fragment_telegram_channel

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun isFooterShow() = false

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

}